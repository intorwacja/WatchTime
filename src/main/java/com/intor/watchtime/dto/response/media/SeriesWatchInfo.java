package com.intor.watchtime.dto.response.media;

import java.time.Instant;
import java.util.UUID;

public record SeriesWatchInfo(
    UUID mediaId,
    Instant lastWatchedAt
){}
