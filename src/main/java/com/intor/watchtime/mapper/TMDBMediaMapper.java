package com.intor.watchtime.mapper;

import com.intor.watchtime.dto.response.media.*;
import com.intor.watchtime.dto.response.media.tmdb.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TMDBMediaMapper {

    public MediaPageResponse toMediaPageResponse(TMDBPage<TMDBResponse> page, Set<Long> watchedMediaIds) {
        return new MediaPageResponse(
                page.page(),
                page.results().stream()
                        .filter(Objects::nonNull)
                        .map(r -> toMediaResponse(r, watchedMediaIds))
                        .toList(),
                page.totalPages(),
                page.totalResults()
        );
    }

    public MediaResponse toMediaResponse(TMDBResponse tmdb, Set<Long> watchedMediaIds) {
        return switch (tmdb) {
            case TMDBResponse.TMDBMovieResponse m -> new MediaResponse(
                    m.getId(),
                    m.getTitle(),
                    m.getVoteAverage(),
                    m.getPosterPath(),
                    m.getBackdropPath(),
                    m.getMediaType(),
                    m.getReleaseDate(),
                    watchedMediaIds.contains(m.getId())
            );
            case TMDBResponse.TMDBSeriesResponse s -> new MediaResponse(
                    s.getId(),
                    s.getName(),
                    s.getVoteAverage(),
                    s.getPosterPath(),
                    s.getBackdropPath(),
                    s.getMediaType(),
                    s.getFirstAirDate(),
                    watchedMediaIds.contains(s.getId())
            );
            default -> throw new IllegalStateException("Unknown TMDB type: " + tmdb.getClass());
        };
    }

    public MovieDetailsResponse toMovieDetailsResponse(TMDBMovieDetailsResponse details, MovieWatchInfo movieWatchInfo) {
        List<TMDBCredits.TMDBCastMember> cast = details.credits() != null ? details.credits().cast() : List.of();
        return new MovieDetailsResponse(
                details.title(),
                details.overview(),
                details.voteAverage(),
                details.runtime(),
                details.voteCount(),
                details.releaseDate(),
                details.posterPath(),
                details.backdropPath(),
                cast.stream()
                        .map(c -> new CastMember(
                                c.name(),
                                c.knownForDepartment(),
                                c.profilePath(),
                                c.character()
                        ))
                        .toList(),
                movieWatchInfo
        );
    }

    public SeriesDetailsResponse toSeriesDetailsResponse(TMDBSeriesDetailsResponse details, SeriesWatchInfo seriesWatchInfo, Map<Integer, SeasonWatchInfo> seasonsWatchInfo) {
        List<TMDBAggregateCredits.TMDBAggregateCastMember> cast = details.aggregateCredits() != null
                ? details.aggregateCredits().cast() : List.of();
        List<TMDBSeason> seasons = details.seasons() != null ? details.seasons() : List.of();
        return new SeriesDetailsResponse(
                details.name(),
                details.overview(),
                details.voteAverage(),
                details.voteCount(),
                details.firstAirDate(),
                details.posterPath(),
                details.backdropPath(),
                details.numberOfSeasons(),
                details.numberOfEpisodes(),
                seriesWatchInfo,
                cast.stream()
                        .map(c -> new CastMember(
                                c.name(),
                                c.knownForDepartment(),
                                c.profilePath(),
                                c.roles() != null
                                        ? c.roles().stream()
                                                .map(TMDBAggregateCredits.TMDBAggregateCastMember.Role::character)
                                                .collect(Collectors.joining(" / "))
                                        : null
                        ))
                        .toList(),
                seasons.stream()
                        .map(s -> new SeriesDetailsResponse.Season(
                                s.seasonNumber(),
                                s.name(),
                                s.overview(),
                                s.posterPath(),
                                s.airDate(),
                                s.episodeCount(),
                                s.voteAverage(),
                                seasonsWatchInfo.get(s.seasonNumber())
                        ))
                        .toList()
        );
    }

    public SeasonDetailsResponse toSeasonDetailsResponse(TMDBSeasonDetails seasonDetails, Map<Integer, EpisodeWatchInfo> episodesWatchInfo) {
        List<TMDBSeasonDetails.TMDBEpisode> episodes = seasonDetails.episodes() != null
                ? seasonDetails.episodes() : List.of();
        return new SeasonDetailsResponse(
                seasonDetails.id(),
                seasonDetails.overview(),
                episodes.stream()
                        .map(e -> new SeasonDetailsResponse.SeasonEpisode(
                                e.id(),
                                e.name(),
                                e.overview(),
                                e.airDate(),
                                e.episodeNumber(),
                                e.seasonNumber(),
                                e.stillPath(),
                                e.runtime(),
                                e.voteAverage(),
                                episodesWatchInfo.get(e.episodeNumber())
                        ))
                        .toList()
        );
    }
}
