package com.intor.watchtime.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record WatchSeasonRequest(
        @NotNull Long externalMediaId,
        @NotNull Integer seasonNumber,
        @PastOrPresent Instant watchedAt,
        @Size(max = 255) String markedViaSource,
        @Size(max = 255) String watchedOnPlatform
) {}
