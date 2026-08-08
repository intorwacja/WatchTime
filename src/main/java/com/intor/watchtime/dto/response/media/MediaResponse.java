package com.intor.watchtime.dto.response.media;

public record MediaResponse(
        Long id,
        String title,
        double voteAverage,
        String posterPath,
        String backdropPath,
        String mediaType,
        String releaseDate,
        boolean watched
) {}
