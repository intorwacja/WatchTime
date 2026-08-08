package com.intor.watchtime.dto.response.media;

import java.util.List;

public record MovieDetailsResponse(
        String title,
        String overview,
        double voteAverage,
        int voteCount,
        String releaseDate,
        String posterPath,
        String backdropPath,
        List<CastMember> cast,
        MovieWatchInfo movieWatchInfo
) {
}
