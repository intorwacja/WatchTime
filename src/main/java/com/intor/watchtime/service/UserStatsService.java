package com.intor.watchtime.service;

import com.intor.watchtime.dto.response.DailyMinutesResponse;
import com.intor.watchtime.dto.response.UserStatsResponse;
import com.intor.watchtime.exception.InvalidRequestException;
import com.intor.watchtime.repository.WatchRepository;
import com.intor.watchtime.repository.projection.DailyMinutesProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserStatsService {

    private static final int MAX_DAYS = 90;

    private final WatchRepository watchRepository;
    private final CurrentUserService currentUserService;

    public UserStatsResponse getStatsForCurrentUser(String zone, int days) {
        ZoneId zoneId;

        try {
            zoneId = ZoneId.of(zone);
        } catch (DateTimeException e) {
            throw new InvalidRequestException("Unknown time zone: " + zone, e);
        }

        if (days < 0 || days > MAX_DAYS) {
            throw new InvalidRequestException("days must be between 0 and " + MAX_DAYS);
        }

        UUID userId = currentUserService.get().getId();

        LocalDate today = LocalDate.now(zoneId);
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        long streak = watchRepository.findStreakBounds(userId, zoneId.getId())
                .map(bounds -> currentStreak(bounds.getLastWatchedDay(), bounds.getStreakStart(), today))
                .orElse(0L);

        return UserStatsResponse.from(watchRepository.findUserStats(
                userId,
                startOfDay(today.withDayOfMonth(1), zoneId),  // first day of the current month
                startOfDay(weekStart, zoneId),                // Monday of the current week
                startOfDay(weekStart.minusWeeks(1), zoneId),  // Monday of the previous week
                startOfDay(today.minusDays(29), zoneId)       // 30-day window ending today
        ), streak, dailyBreakdown(userId, zoneId, today, days));
    }

    private List<DailyMinutesResponse> dailyBreakdown(UUID userId, ZoneId zoneId, LocalDate today, int days) {
        if (days == 0) {
            return List.of();
        }

        LocalDate from = today.minusDays(days - 1L);
        Map<LocalDate, Long> byDay = watchRepository
                .findDailyMinutes(userId, zoneId.getId(), startOfDay(from, zoneId))
                .stream()
                .collect(Collectors.toMap(DailyMinutesProjection::getDay, DailyMinutesProjection::getMinutes));

        return IntStream.range(0, days)
                .mapToObj(from::plusDays)
                .map(date -> new DailyMinutesResponse(date, byDay.getOrDefault(date, 0L)))
                .toList();
    }

    private Instant startOfDay(LocalDate date, ZoneId zoneId) {
        return date.atStartOfDay(zoneId).toInstant();
    }

    private long currentStreak(LocalDate lastWatchedDay, LocalDate streakStart, LocalDate today) {
        if (lastWatchedDay == null || streakStart == null) {
            return 0;
        }
        if (!lastWatchedDay.equals(today) && !lastWatchedDay.equals(today.minusDays(1))) {
            return 0;
        }
        return ChronoUnit.DAYS.between(streakStart, lastWatchedDay) + 1;
    }
}
