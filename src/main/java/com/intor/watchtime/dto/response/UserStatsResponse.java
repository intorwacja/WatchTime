package com.intor.watchtime.dto.response;

import com.intor.watchtime.repository.projection.UserStatsProjection;

import java.time.Instant;
import java.util.List;

public record UserStatsResponse(
        long totalMinutes,
        long totalWatches,
        long filmWatches,
        long episodeWatches,
        long distinctTitles,
        Instant firstWatchAt,
        long filmsWatchedThisMonth,
        long minutesThisWeek,
        long minutesLastWeek,
        long minutesLast30Days,
        long currentStreakDays,
        List<DailyMinutesResponse> daily
) {
    public static UserStatsResponse from(UserStatsProjection stats,
                                         long currentStreakDays,
                                         List<DailyMinutesResponse> daily) {
        return new UserStatsResponse(
                stats.totalMinutes(),
                stats.filmWatches() + stats.episodeWatches(),
                stats.filmWatches(),
                stats.episodeWatches(),
                stats.distinctTitles(),
                stats.firstWatchAt(),
                stats.filmsWatchedThisMonth(),
                stats.minutesThisWeek(),
                stats.minutesLastWeek(),
                stats.minutesLast30Days(),
                currentStreakDays,
                daily
        );
    }
}
