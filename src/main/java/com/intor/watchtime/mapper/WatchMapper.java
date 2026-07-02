package com.intor.watchtime.mapper;

import com.intor.watchtime.dto.request.WatchRequest;
import com.intor.watchtime.dto.response.WatchResponse;
import com.intor.watchtime.entity.Watch;
import org.springframework.stereotype.Component;

@Component
public class WatchMapper {

    public Watch toEntity(WatchRequest request) {
        return Watch.builder()
                .title(request.title())
                .externalShowId(request.externalShowId())
                .externalEpisodeId(request.externalEpisodeId())
                .seasonNumber(request.seasonNumber())
                .episodeNumber(request.episodeNumber())
                .isMovie(request.isMovie())
                .isSpecial(request.isSpecial())
                .runtimeSeconds(request.runtimeSeconds())
                .rewatchCount(request.rewatchCount() != null ? request.rewatchCount() : 0)
                .watchedAt(request.watchedAt())
                .markedViaSource(request.markedViaSource())
                .watchedOnPlatform(request.watchedOnPlatform())
                .rating(request.rating())
                .build();
    }

    public WatchResponse toResponse(Watch watch) {
        return new WatchResponse(
                watch.getId(),
                watch.getTitle(),
                watch.getExternalShowId(),
                watch.getExternalEpisodeId(),
                watch.getSeasonNumber(),
                watch.getEpisodeNumber(),
                watch.getIsMovie(),
                watch.getIsSpecial(),
                watch.getRuntimeSeconds(),
                watch.getRewatchCount(),
                watch.getWatchedAt(),
                watch.getMarkedViaSource(),
                watch.getWatchedOnPlatform(),
                watch.getRating(),
                watch.getCreatedAt()
        );
    }
}
