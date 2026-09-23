package com.intor.watchtime.service;

import com.intor.watchtime.dto.request.WatchRequest;
import com.intor.watchtime.dto.request.WatchSeasonRequest;
import com.intor.watchtime.dto.response.WatchResponse;
import com.intor.watchtime.dto.response.WatchedViewPageResponse;
import com.intor.watchtime.dto.response.WatchedViewResponse;
import com.intor.watchtime.dto.response.media.MovieDetailsResponse;
import com.intor.watchtime.dto.response.media.SeasonDetailsResponse;
import com.intor.watchtime.dto.response.media.SeriesDetailsResponse;
import com.intor.watchtime.entity.Episode;
import com.intor.watchtime.entity.Media;
import com.intor.watchtime.entity.Provider;
import com.intor.watchtime.entity.User;
import com.intor.watchtime.entity.Watch;
import com.intor.watchtime.exception.ExternalMediaException;
import com.intor.watchtime.exception.ResourceNotFoundException;
import com.intor.watchtime.mapper.MediaMapper;
import com.intor.watchtime.mapper.WatchMapper;
import com.intor.watchtime.repository.EpisodeRepository;
import com.intor.watchtime.repository.MediaRepository;
import com.intor.watchtime.repository.WatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class WatchService {

    private final WatchRepository watchRepository;
    private final MediaRepository mediaRepository;
    private final EpisodeRepository episodeRepository;
    private final ExternalMediaService externalMediaService;
    private final CurrentUserService currentUserService;
    private final WatchMapper watchMapper;
    private final MediaMapper mediaMapper;

    @Value("${media.provider}")
    private Provider provider;

    @Transactional
    public WatchResponse create(WatchRequest request) {
        User user = currentUserService.get();
        Media media = mediaRepository.findByExternalShowIdAndProvider(request.externalMediaId(), provider)
                .orElseGet(() -> createMedia(request.externalMediaId(), request.isMovie()));
        Episode episode = request.isMovie()
                ? null
                : resolveOrCreateEpisode(media, request.seasonNumber(), request.episodeNumber());
        Watch watch = watchMapper.toEntity(request, user, media, episode);
        return watchMapper.toResponse(watchRepository.save(watch));
    }

    @Transactional
    public List<WatchResponse> createForSeason(WatchSeasonRequest request) {
        User user = currentUserService.get();
        Media media = mediaRepository.findByExternalShowIdAndProvider(request.externalMediaId(), provider)
                .orElseGet(() -> createMedia(request.externalMediaId(), false));
        SeasonDetailsResponse season = externalMediaService.getSeasonDetails(
                media.getExternalShowId(), request.seasonNumber());

        Map<Integer, Episode> existingByNumber = episodeRepository
                .findAllByMediaAndSeasonNumber(media, request.seasonNumber())
                .stream()
                .collect(Collectors.toMap(Episode::getEpisodeNumber, Function.identity()));

        List<Episode> toCreate = season.episodes().stream()
                .filter(e -> !existingByNumber.containsKey(e.episodeNumber()))
                .map(e -> mediaMapper.toEpisodeEntity(e, media))
                .toList();
        episodeRepository.saveAll(toCreate)
                .forEach(e -> existingByNumber.put(e.getEpisodeNumber(), e));

        List<Watch> watches = season.episodes().stream()
                .map(e -> Watch.builder()
                        .user(user)
                        .media(media)
                        .episode(existingByNumber.get(e.episodeNumber()))
                        .watchedAt(request.watchedAt())
                        .markedViaSource(request.markedViaSource())
                        .watchedOnPlatform(request.watchedOnPlatform())
                        .build())
                .toList();

        return watchRepository.saveAll(watches).stream()
                .map(watchMapper::toResponse)
                .toList();
    }

    private Media createMedia(Long externalId, boolean isMovie) {
        if (isMovie) {
            MovieDetailsResponse movieDetails = externalMediaService.getMovieDetails(externalId);
            return mediaRepository.save(mediaMapper.toEntity(movieDetails, externalId, provider));
        }
        SeriesDetailsResponse seriesDetails = externalMediaService.getSeriesDetails(externalId);
        return mediaRepository.save(mediaMapper.toEntity(seriesDetails, externalId, provider));
    }

    private Episode resolveOrCreateEpisode(Media media, Integer seasonNumber, Integer episodeNumber) {
        if (seasonNumber == null || episodeNumber == null) {
            throw new IllegalArgumentException("seasonNumber and episodeNumber are required for series");
        }
        return episodeRepository.findByMediaAndSeasonNumberAndEpisodeNumber(media, seasonNumber, episodeNumber)
                .orElseGet(() -> createEpisode(media, seasonNumber, episodeNumber));
    }

    private Episode createEpisode(Media media, int seasonNumber, int episodeNumber) {
        SeasonDetailsResponse season = externalMediaService.getSeasonDetails(media.getExternalShowId(), seasonNumber);
        SeasonDetailsResponse.SeasonEpisode episodeData = season.episodes().stream()
                .filter(e -> e.episodeNumber() == episodeNumber)
                .findFirst()
                .orElseThrow(() -> new ExternalMediaException(
                        "Episode S%dE%d not found for media %d".formatted(seasonNumber, episodeNumber, media.getExternalShowId())));
        return episodeRepository.save(mediaMapper.toEpisodeEntity(episodeData, media));
    }

    public WatchResponse getById(UUID id) {
        Watch watch = watchRepository.findByIdAndUserId(id, currentUserService.get().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Watch", id));
        return watchMapper.toResponse(watch);
    }

    public WatchedViewPageResponse getWatchedList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<WatchedViewResponse> libraryPage =
                watchRepository.findWatchedLibrary(currentUserService.get().getId(), pageable);
        return new WatchedViewPageResponse(
                libraryPage.getNumber(),
                libraryPage.getContent(),
                libraryPage.getTotalPages(),
                libraryPage.getTotalElements()
        );
    }

    @Transactional
    public WatchResponse update(UUID id, WatchRequest request) {
        Watch watch = watchRepository.findByIdAndUserId(id, currentUserService.get().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Watch", id));
        if (watch.getMedia().getExternalShowId() != request.externalMediaId()) {
            Media media = mediaRepository.findByExternalShowIdAndProvider(request.externalMediaId(), provider)
                    .orElseGet(() -> createMedia(request.externalMediaId(), request.isMovie()));
            watch.setMedia(media);
        }
        watch.setEpisode(request.isMovie()
                ? null
                : resolveOrCreateEpisode(watch.getMedia(), request.seasonNumber(), request.episodeNumber()));
        if (request.watchedAt() != null) {
            watch.setWatchedAt(request.watchedAt());
        }
        watch.setMarkedViaSource(request.markedViaSource());
        watch.setWatchedOnPlatform(request.watchedOnPlatform());
        watch.setRating(request.rating());
        return watchMapper.toResponse(watchRepository.save(watch));
    }

    @Transactional
    public void deleteAllByMediaId(UUID mediaId) {
        long count = watchRepository.deleteAllByMediaIdAndUserId(mediaId, currentUserService.get().getId());
        if (count == 0) {
            if (!mediaRepository.existsById(mediaId)) {
                throw new ResourceNotFoundException("Media", mediaId);
            }
            throw new ResourceNotFoundException("Watch for media", mediaId);
        }
        log.debug("Deleted {} watches from media {}", count, mediaId);
    }

    @Transactional
    public Instant deleteLastWatchByMediaId(UUID mediaId) {
        UUID userId = currentUserService.get().getId();
        long count = watchRepository.deleteLastByMediaId(mediaId, userId);

        if (count == 0) {
            throw new ResourceNotFoundException("Media with id " + mediaId + " did not contain any watches");
        }

        return watchRepository.findMovieLatestWatch(mediaId, userId).orElse(null);
    }

    @Transactional
    public Instant deleteLastEpisodeWatch(UUID mediaId, int seasonNumber, int episodeNumber) {
        UUID userId = currentUserService.get().getId();
        long count = watchRepository.deleteLastEpisodeWatch(mediaId, userId, seasonNumber, episodeNumber);

        if (count == 0) {
            throw new ResourceNotFoundException("Media with id " + mediaId + " and episode S" + seasonNumber + "E" + episodeNumber + " did not contain any watches");
        }

        return watchRepository.findEpisodeLatestWatch(mediaId, seasonNumber, episodeNumber, userId).orElse(null);
    }
}
