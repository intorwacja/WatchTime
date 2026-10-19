package com.intor.watchtime.dto.response;

import java.time.LocalDate;

public record DailyMinutesResponse(LocalDate date, long minutes) {
}
