package com.intor.watchtime.mapper;

import com.intor.watchtime.dto.response.media.MovieDetailsResponse;
import com.intor.watchtime.dto.response.media.SeasonDetailsResponse;
import com.intor.watchtime.dto.response.media.SeriesDetailsResponse;
import com.intor.watchtime.entity.Episode;
import com.intor.watchtime.entity.Media;
import com.intor.watchtime.entity.Provider;
import org.springframework.stereotype.Component;

@Component
public class MediaMapper {

    public Media toEntity(MovieDetailsResponse details, long externalShowId, Provider provider) {
        return Media.builder()
                .externalShowId(externalShowId)
                .provider(provider)
                .title(details.title())
                .isMovie(true)
                .posterPath(details.posterPath())
                .backdropPath(details.backdropPath())
                .build();
    }

    public Media toEntity(SeriesDetailsResponse details, long externalShowId, Provider provider) {
        return Media.builder()
                .externalShowId(externalShowId)
                .provider(provider)
                .title(details.title())
                .isMovie(false)
                .posterPath(details.posterPath())
                .backdropPath(details.backdropPath())
                .numberOfEpisodes(details.numberOfEpisodes())
                .build();
    }

    public Episode toEpisodeEntity(SeasonDetailsResponse.SeasonEpisode episode, Media media) {
        return Episode.builder()
                .media(media)
                .seasonNumber(episode.seasonNumber())
                .episodeNumber(episode.episodeNumber())
                .title(episode.title())
                .overview(episode.overview())
                .runtimeMinutes(episode.runtime())
                .stillPath(episode.stillPath())
                .build();
    }
}
