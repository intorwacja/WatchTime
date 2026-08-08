package com.intor.watchtime.service;

import com.intor.watchtime.dto.response.media.SeriesWatchInfo;
import com.intor.watchtime.dto.response.media.EpisodeWatchInfo;
import com.intor.watchtime.dto.response.media.MovieWatchInfo;
import com.intor.watchtime.dto.response.media.SeasonWatchInfo;
import com.intor.watchtime.repository.projection.EpisodeWatchInfoProjection;
import com.intor.watchtime.repository.projection.SeasonWatchInfoProjection;
import com.intor.watchtime.entity.Provider;
import com.intor.watchtime.repository.WatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WatchStatsService {
    private final WatchRepository watchRepository;

    public MovieWatchInfo getMovieWatchStats(Long externalMediaId, Provider provider) {
        return watchRepository.findMovieWatchInfo(externalMediaId, provider).orElse(null);
    }

    public SeriesWatchInfo getSeriesWatchStats(Long externalMediaId, Provider provider) {
        return watchRepository.findSeriesWatchInfo(externalMediaId, provider).orElse(null);
    }

    public Map<Integer, SeasonWatchInfo> getSeasonsWatchStats(Long externalMediaId, Provider provider) {
        return watchRepository.findSeasonWatchInfos(externalMediaId, provider).stream()
                .collect(Collectors.toMap(SeasonWatchInfoProjection::seasonNumber,
                        info -> new SeasonWatchInfo(info.watchedEpisodesCount())));
    }
    public Map<Integer, EpisodeWatchInfo> getEpisodesWatchInfoInSeason(Long externalMediaId, Provider provider, int seasonNumber) {
        return watchRepository.findEpisodeWatchInfosInSeason(externalMediaId, provider, seasonNumber).stream()
                .collect(Collectors.toMap(EpisodeWatchInfoProjection::episodeNumber,
                        info -> new EpisodeWatchInfo(info.id(), info.watchedAt(), info.watchCount())));
    }
}
