package com.intor.watchtime.dto.response.media.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TMDBSeason(
        @JsonProperty("air_date") String airDate,
        @JsonProperty("episode_count") int episodeCount,
        String name,
        String overview,
        @JsonProperty("poster_path") String posterPath,
        @JsonProperty("season_number") int seasonNumber,
        @JsonProperty("vote_average") double voteAverage
) {}
