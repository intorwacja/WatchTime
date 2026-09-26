package com.intor.watchtime.dto.response.media;

import java.time.Instant;
import java.util.UUID;

public record EpisodeWatchInfo(
        UUID id,
        Instant watchedAt,
        long watchCount
) {}
