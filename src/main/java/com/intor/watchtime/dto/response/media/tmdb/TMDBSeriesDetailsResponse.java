package com.intor.watchtime.dto.response.media.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TMDBSeriesDetailsResponse(
        String name,
        String overview,
        @JsonProperty("vote_average") double voteAverage,
        @JsonProperty("vote_count") int voteCount,
        @JsonProperty("first_air_date") String firstAirDate,
        @JsonProperty("poster_path") String posterPath,
        @JsonProperty("backdrop_path") String backdropPath,
        @JsonProperty("number_of_seasons") int numberOfSeasons,
        @JsonProperty("number_of_episodes") int numberOfEpisodes,
        List<TMDBSeason> seasons,
        @JsonProperty("aggregate_credits") TMDBAggregateCredits aggregateCredits
) {
}
