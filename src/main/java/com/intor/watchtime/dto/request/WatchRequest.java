package com.intor.watchtime.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record WatchRequest(
        @NotNull Long externalMediaId,
        Integer seasonNumber,
        Integer episodeNumber,
        @NotNull Boolean isMovie,
        Instant watchedAt,
        String markedViaSource,
        String watchedOnPlatform,
        @DecimalMin("0.0") @DecimalMax("10.0") Double rating
) {}
