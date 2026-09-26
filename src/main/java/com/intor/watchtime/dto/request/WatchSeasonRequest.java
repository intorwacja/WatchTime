package com.intor.watchtime.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record WatchSeasonRequest(
        @NotNull Long externalMediaId,
        @NotNull Integer seasonNumber,
        Instant watchedAt,
        String markedViaSource,
        String watchedOnPlatform
) {}
