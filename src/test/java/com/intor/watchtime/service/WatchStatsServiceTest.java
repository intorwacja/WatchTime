package com.intor.watchtime.service;

import com.intor.watchtime.entity.Provider;
import com.intor.watchtime.repository.WatchRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WatchStatsServiceTest {

    @Mock
    private WatchRepository watchRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private WatchStatsService watchStatsService;

    private void givenNobodySignedIn() {
        when(currentUserService.find()).thenReturn(Optional.empty());
    }

    @Test
    @DisplayName("an anonymous visitor gets no watch details for a single title")
    void returnsNothingForAnonymousVisitorOnSingleTitle() {
        givenNobodySignedIn();

        assertThat(watchStatsService.getMovieWatchStats(603L, Provider.TMDB)).isNull();
        assertThat(watchStatsService.getSeriesWatchStats(1396L, Provider.TMDB)).isNull();

        verifyNoInteractions(watchRepository);
    }

    @Test
    @DisplayName("an anonymous visitor gets empty season and episode details")
    void returnsEmptyMapsForAnonymousVisitor() {
        givenNobodySignedIn();

        assertThat(watchStatsService.getSeasonsWatchStats(1396L, Provider.TMDB)).isEmpty();
        assertThat(watchStatsService.getEpisodesWatchInfoInSeason(1396L, Provider.TMDB, 1)).isEmpty();

        verifyNoInteractions(watchRepository);
    }

    @Test
    @DisplayName("an anonymous visitor sees nothing marked as watched in a listing")
    void returnsNoWatchedMarkersForAnonymousVisitor() {
        givenNobodySignedIn();

        assertThat(watchStatsService.getWatchedExternalIds(Set.of(603L, 1396L), Provider.TMDB)).isEmpty();

        verifyNoInteractions(watchRepository);
    }
}
