package com.intor.watchtime.repository.projection;

public record SeasonWatchInfoProjection(
        int seasonNumber,
        long watchedEpisodesCount
) {}
