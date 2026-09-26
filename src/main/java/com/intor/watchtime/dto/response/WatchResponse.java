package com.intor.watchtime.dto.response;

import java.time.Instant;
import java.util.UUID;

public record WatchResponse(
        UUID id,
        UUID mediaId,
        Long externalMediaId,
        Integer seasonNumber,
        Integer episodeNumber,
        boolean isMovie,
        Instant watchedAt,
        String markedViaSource,
        String watchedOnPlatform,
        Double rating,
        Instant createdAt
) {}
