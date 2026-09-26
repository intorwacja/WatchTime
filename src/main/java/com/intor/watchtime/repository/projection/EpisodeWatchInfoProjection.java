package com.intor.watchtime.repository.projection;

import java.time.Instant;
import java.util.UUID;

public record EpisodeWatchInfoProjection(
        UUID id,
        int episodeNumber,
        Instant watchedAt,
        long watchCount
) {}
