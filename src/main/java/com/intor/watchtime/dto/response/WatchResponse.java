package com.intor.watchtime.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record WatchResponse(
        UUID id,
        String title,
        Long externalShowId,
        Long externalEpisodeId,
        Integer seasonNumber,
        Integer episodeNumber,
        Boolean isMovie,
        Boolean isSpecial,
        Integer runtimeSeconds,
        Integer rewatchCount,
        LocalDateTime watchedAt,
        String markedViaSource,
        String watchedOnPlatform,
        Integer rating,
        LocalDateTime createdAt
) {}
