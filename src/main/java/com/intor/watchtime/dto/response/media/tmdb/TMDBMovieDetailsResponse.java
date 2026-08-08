package com.intor.watchtime.dto.response.media.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TMDBMovieDetailsResponse(
        String title,
        String overview,
        Integer runtime,
        @JsonProperty("vote_average") double voteAverage,
        @JsonProperty("vote_count") int voteCount,
        @JsonProperty("release_date") String releaseDate,
        @JsonProperty("poster_path") String posterPath,
        @JsonProperty("backdrop_path") String backdropPath,
        TMDBCredits credits
) {}
