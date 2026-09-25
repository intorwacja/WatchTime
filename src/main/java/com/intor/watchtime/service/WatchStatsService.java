package com.intor.watchtime.service;

import com.intor.watchtime.dto.response.media.SeriesWatchInfo;
import com.intor.watchtime.dto.response.media.EpisodeWatchInfo;
import com.intor.watchtime.dto.response.media.MovieWatchInfo;
import com.intor.watchtime.dto.response.media.SeasonWatchInfo;
import com.intor.watchtime.repository.projection.EpisodeWatchInfoProjection;
import com.intor.watchtime.repository.projection.SeasonWatchInfoProjection;
import com.intor.watchtime.entity.Provider;
import com.intor.watchtime.entity.User;
import com.intor.watchtime.repository.WatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WatchStatsService {
    private final WatchRepository watchRepository;
    private final CurrentUserService currentUserService;

    public MovieWatchInfo getMovieWatchStats(Long externalMediaId, Provider provider) {
        return currentUserId()
                .flatMap(userId -> watchRepository.findMovieWatchInfo(externalMediaId, provider, userId))
                .orElse(null);
    }

    public SeriesWatchInfo getSeriesWatchStats(Long externalMediaId, Provider provider) {
        return currentUserId()
                .flatMap(userId -> watchRepository.findSeriesWatchInfo(externalMediaId, provider, userId))
                .orElse(null);
    }

    public Map<Integer, SeasonWatchInfo> getSeasonsWatchStats(Long externalMediaId, Provider provider) {
        return currentUserId()
                .map(userId -> watchRepository.findSeasonWatchInfos(externalMediaId, provider, userId).stream()
                        .collect(Collectors.toMap(SeasonWatchInfoProjection::seasonNumber,
                                info -> new SeasonWatchInfo(info.watchedEpisodesCount()))))
                .orElseGet(Map::of);
    }

    public Map<Integer, EpisodeWatchInfo> getEpisodesWatchInfoInSeason(Long externalMediaId, Provider provider, int seasonNumber) {
        return currentUserId()
                .map(userId -> watchRepository.findEpisodeWatchInfosInSeason(externalMediaId, provider, seasonNumber, userId).stream()
                        .collect(Collectors.toMap(EpisodeWatchInfoProjection::episodeNumber,
                                info -> new EpisodeWatchInfo(info.id(), info.watchedAt(), info.watchCount()))))
                .orElseGet(Map::of);
    }

    public Set<Long> getWatchedExternalIds(Set<Long> externalIds, Provider provider) {
        return currentUserId()
                .map(userId -> watchRepository.findWatchedExternalIds(externalIds, provider, userId))
                .orElseGet(Set::of);
    }

    private Optional<UUID> currentUserId() {
        return currentUserService.find().map(User::getId);
    }
}
