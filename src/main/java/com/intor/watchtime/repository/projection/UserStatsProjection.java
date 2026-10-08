package com.intor.watchtime.repository.projection;

import java.time.Instant;

public record UserStatsProjection(
        long totalMinutes,
        long filmWatches,
        long episodeWatches,
        long distinctTitles,
        Instant firstWatchAt,
        long filmsWatchedThisMonth,
        long minutesThisWeek,
        long minutesLastWeek,
        long minutesLast30Days
) {}
