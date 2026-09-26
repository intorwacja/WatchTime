package com.intor.watchtime.dto.response.media;

import java.time.Instant;
import java.util.UUID;

public record MovieWatchInfo(
        UUID mediaId,
        Instant lastWatchedAt,
        long watchCount
) {}
