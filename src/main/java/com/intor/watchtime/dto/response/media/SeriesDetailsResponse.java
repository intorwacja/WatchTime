package com.intor.watchtime.dto.response.media;

import java.util.List;

public record SeriesDetailsResponse(
        String title,
        String overview,
        double voteAverage,
        int voteCount,
        String releaseDate,
        String posterPath,
        String backdropPath,
        int numberOfSeasons,
        int numberOfEpisodes,
        SeriesWatchInfo watchInfo,
        List<CastMember> cast,
        List<Season> seasons
) {
    public record Season(
            int seasonNumber,
            String name,
            String overview,
            String posterPath,
            String releaseDate,
            int episodeCount,
            double voteAverage,
            SeasonWatchInfo watchInfo
    ) {}
}
