package com.intor.watchtime.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record WatchRequest(
        @NotBlank String title,
        Long externalShowId,
        Long externalEpisodeId,
        Integer seasonNumber,
        Integer episodeNumber,
        Boolean isMovie,
        Boolean isSpecial,
        Integer runtimeSeconds,
        Integer rewatchCount,
        @NotNull LocalDateTime watchedAt,
        String markedViaSource,
        String watchedOnPlatform,
        @Min(1) @Max(10) Integer rating
) {}
