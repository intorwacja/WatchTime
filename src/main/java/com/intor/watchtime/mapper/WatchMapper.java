package com.intor.watchtime.mapper;

import com.intor.watchtime.dto.request.WatchRequest;
import com.intor.watchtime.dto.response.WatchResponse;
import com.intor.watchtime.entity.Episode;
import com.intor.watchtime.entity.Media;
import com.intor.watchtime.entity.User;
import com.intor.watchtime.entity.Watch;
import org.springframework.stereotype.Component;

@Component
public class WatchMapper {

    public Watch toEntity(WatchRequest request, User user, Media media, Episode episode) {
        return Watch.builder()
                .user(user)
                .media(media)
                .episode(episode)
                .watchedAt(request.watchedAt())
                .markedViaSource(request.markedViaSource())
                .watchedOnPlatform(request.watchedOnPlatform())
                .rating(request.rating())
                .build();
    }

    public WatchResponse toResponse(Watch watch) {
        Episode episode = watch.getEpisode();
        return new WatchResponse(
                watch.getId(),
                watch.getMedia().getId(),
                watch.getMedia().getExternalShowId(),
                episode != null ? episode.getSeasonNumber() : null,
                episode != null ? episode.getEpisodeNumber() : null,
                watch.getMedia().isMovie(),
                watch.getWatchedAt(),
                watch.getMarkedViaSource(),
                watch.getWatchedOnPlatform(),
                watch.getRating(),
                watch.getCreatedAt()
        );
    }

}
