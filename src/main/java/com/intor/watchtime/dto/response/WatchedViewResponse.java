package com.intor.watchtime.dto.response;

import java.time.Instant;
import java.util.UUID;

public record WatchedViewResponse(
        UUID mediaId,
        Long externalId,
        String title,
        String posterPath,
        boolean isMovie,
        long watchedCount,
        Integer totalEpisodes,
        Instant lastWatchedAt,
        Double rating
) {
}
