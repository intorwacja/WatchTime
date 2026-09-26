package com.intor.watchtime.mapper;

import com.intor.watchtime.dto.response.media.MediaResponse;
import com.intor.watchtime.dto.response.media.MovieDetailsResponse;
import com.intor.watchtime.dto.response.media.SeriesDetailsResponse;
import com.intor.watchtime.dto.response.media.tmdb.TMDBMovieDetailsResponse;
import com.intor.watchtime.dto.response.media.tmdb.TMDBResponse;
import com.intor.watchtime.dto.response.media.tmdb.TMDBSeriesDetailsResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TMDBMediaMapperTest {

    private final TMDBMediaMapper mapper = new TMDBMediaMapper();

    @Test
    @DisplayName("a film is described by its title and release date")
    void mapsMovieFieldsFromTheMovieShape() {
        TMDBResponse.TMDBMovieResponse movie = TMDBResponse.TMDBMovieResponse.builder()
                .id(603L)
                .title("The Matrix")
                .releaseDate("1999-03-30")
                .mediaType("movie")
                .voteAverage(8.2)
                .build();

        MediaResponse result = mapper.toMediaResponse(movie, Set.of());

        assertThat(result.title()).isEqualTo("The Matrix");
        assertThat(result.releaseDate()).isEqualTo("1999-03-30");
        assertThat(result.watched()).isFalse();
    }

    @Test
    @DisplayName("a series is described by its name and first air date")
    void mapsSeriesFieldsFromTheSeriesShape() {
        TMDBResponse.TMDBSeriesResponse series = TMDBResponse.TMDBSeriesResponse.builder()
                .id(1396L)
                .name("Breaking Bad")
                .firstAirDate("2008-01-20")
                .mediaType("tv")
                .voteAverage(8.9)
                .build();

        MediaResponse result = mapper.toMediaResponse(series, Set.of());

        assertThat(result.title()).isEqualTo("Breaking Bad");
        assertThat(result.releaseDate()).isEqualTo("2008-01-20");
    }

    @Test
    @DisplayName("titles the viewer has already watched are flagged as such")
    void flagsAlreadyWatchedTitles() {
        TMDBResponse.TMDBMovieResponse movie = TMDBResponse.TMDBMovieResponse.builder()
                .id(603L)
                .title("The Matrix")
                .build();

        assertThat(mapper.toMediaResponse(movie, Set.of(603L)).watched()).isTrue();
        assertThat(mapper.toMediaResponse(movie, Set.of(1396L)).watched()).isFalse();
    }

    @Test
    @DisplayName("details survive a provider response that omits every optional section")
    void toleratesMissingOptionalSections() {
        TMDBMovieDetailsResponse movie = new TMDBMovieDetailsResponse(
                "The Matrix", "overview", 136, 8.2, 100, "1999-03-30", null, null, null);
        TMDBSeriesDetailsResponse series = new TMDBSeriesDetailsResponse(
                "Breaking Bad", "overview", 8.9, 100, "2008-01-20", null, null, 5, 62, null, null);

        MovieDetailsResponse movieResult = mapper.toMovieDetailsResponse(movie, null);
        SeriesDetailsResponse seriesResult = mapper.toSeriesDetailsResponse(series, null, Map.of());

        assertThat(movieResult.cast()).isEmpty();
        assertThat(seriesResult.cast()).isEmpty();
        assertThat(seriesResult.seasons()).isEmpty();
    }
}
