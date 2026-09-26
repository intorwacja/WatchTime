package com.intor.watchtime.dto.response.media.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "media_type",
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        visible = true,
        defaultImpl = Void.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TMDBResponse.TMDBMovieResponse.class, name = "movie"),
        @JsonSubTypes.Type(value = TMDBResponse.TMDBSeriesResponse.class, name = "tv")
})
public abstract class TMDBResponse {

    private Long id;

    private String overview;

    @JsonProperty("vote_average")
    private double voteAverage;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("media_type")
    private String mediaType;

    @JsonProperty("backdrop_path")
    private String backdropPath;

    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TMDBMovieResponse extends TMDBResponse {
        private String title;

        @JsonProperty("release_date")
        private String releaseDate;
    }

    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TMDBSeriesResponse extends TMDBResponse {
        private String name;

        @JsonProperty("first_air_date")
        private String firstAirDate;
    }
}
