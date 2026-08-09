package com.intor.watchtime.service;

import com.intor.watchtime.config.TMDBClient;
import com.intor.watchtime.dto.response.media.SeriesWatchInfo;
import com.intor.watchtime.dto.response.media.*;
import com.intor.watchtime.dto.response.media.tmdb.TMDBMovieDetailsResponse;
import com.intor.watchtime.dto.response.media.tmdb.TMDBPage;
import com.intor.watchtime.dto.response.media.tmdb.TMDBResponse;
import com.intor.watchtime.dto.response.media.MovieWatchInfo;
import com.intor.watchtime.entity.Provider;
import com.intor.watchtime.mapper.TMDBMediaMapper;
import com.intor.watchtime.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class TMDBExternalMediaService implements ExternalMediaService {

    @Value("${media.provider}")
    private Provider provider;

    private final TMDBClient tmdbClient;
    private final TMDBMediaMapper tmdbMediaMapper;
    private final MediaRepository mediaRepository;
    private final WatchStatsService watchStatsService;

    @Override
    public MediaPageResponse getTrendingAll(TimeWindow timeWindow, int page) {
        TMDBPage<TMDBResponse> tmdbPage = tmdbClient.getTrendingAll(toTmdb(timeWindow), page);
        Set<Long> watchedMediaIds = getWatchedMediaIds(tmdbPage);
        return tmdbMediaMapper.toMediaPageResponse(tmdbPage, watchedMediaIds);
    }

    private TMDBClient.TimeWindow toTmdb(TimeWindow timeWindow) {
        return switch (timeWindow) {
            case DAY -> TMDBClient.TimeWindow.day;
            case WEEK -> TMDBClient.TimeWindow.week;
        };
    }

    @Override
    public MovieDetailsResponse getMovieDetails(Long id) {
        TMDBMovieDetailsResponse response = tmdbClient.getMovieDetails(id, "credits");
        MovieWatchInfo movieWatchInfo = watchStatsService.getMovieWatchStats(id, provider);
        return tmdbMediaMapper.toMovieDetailsResponse(response, movieWatchInfo);
    }

    @Override
    public SeriesDetailsResponse getSeriesDetails(Long id) {
        Map<Integer, SeasonWatchInfo> seasonsWatchInfo = watchStatsService.getSeasonsWatchStats(id, provider);
        SeriesWatchInfo seriesWatchInfo = watchStatsService.getSeriesWatchStats(id, provider);
        return tmdbMediaMapper.toSeriesDetailsResponse(tmdbClient.getSeriesDetails(id, "aggregate_credits"), seriesWatchInfo, seasonsWatchInfo);
    }

    @Override
    public SeasonDetailsResponse getSeasonDetails(Long seriesId, Integer seasonNumber) {
        Map<Integer, EpisodeWatchInfo> episodesWatchInfo = watchStatsService.getEpisodesWatchInfoInSeason(seriesId, provider, seasonNumber);
        return tmdbMediaMapper.toSeasonDetailsResponse(tmdbClient.getSeasonDetails(seriesId, seasonNumber), episodesWatchInfo);
    }

    @Override
    public MediaPageResponse search(String query, int page) {
        TMDBPage<TMDBResponse> tmdbPage = tmdbClient.searchAll(query, page);
        return tmdbMediaMapper.toMediaPageResponse(tmdbPage, getWatchedMediaIds(tmdbPage));
    }

    private Set<Long> getWatchedMediaIds(TMDBPage<TMDBResponse> tmdbPage) {
        return mediaRepository.findWatchedExternalIds(
                tmdbPage.results().stream().filter(Objects::nonNull).map(TMDBResponse::getId).collect(Collectors.toSet()), provider
        );
    }
}
