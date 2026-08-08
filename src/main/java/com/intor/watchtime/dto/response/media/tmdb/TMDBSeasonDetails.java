package com.intor.watchtime.dto.response.media.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TMDBSeasonDetails(
        Long id,
        String overview,
        List<TMDBEpisode> episodes
) {
    public record TMDBEpisode(
            Long id,
            String name,
            String overview,
            @JsonProperty("air_date") String airDate,
            @JsonProperty("episode_number") int episodeNumber,
            @JsonProperty("season_number") int seasonNumber,
            @JsonProperty("still_path") String stillPath,
            Integer runtime,
            @JsonProperty("vote_average") double voteAverage
    ) {}
}
