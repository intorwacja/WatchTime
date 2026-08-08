package com.intor.watchtime.dto.response.media;

import java.util.List;

public record SeasonDetailsResponse (
        Long externalId,
        String overview,
        List<SeasonEpisode> episodes
) {
    public record SeasonEpisode(
            Long externalId,
            String title,
            String overview,
            String releaseDate,
            int episodeNumber,
            int seasonNumber,
            String stillPath,
            Integer runtime,
            double voteAverage,
            EpisodeWatchInfo watchInfo
    ){}
}
