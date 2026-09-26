package com.intor.watchtime.service;

import com.intor.watchtime.dto.request.WatchRequest;
import com.intor.watchtime.dto.request.WatchSeasonRequest;
import com.intor.watchtime.dto.response.media.SeasonDetailsResponse;
import com.intor.watchtime.entity.Episode;
import com.intor.watchtime.entity.Media;
import com.intor.watchtime.entity.Provider;
import com.intor.watchtime.entity.User;
import com.intor.watchtime.entity.Watch;
import com.intor.watchtime.exception.ExternalMediaException;
import com.intor.watchtime.exception.InvalidRequestException;
import com.intor.watchtime.exception.ResourceNotFoundException;
import com.intor.watchtime.mapper.MediaMapper;
import com.intor.watchtime.mapper.WatchMapper;
import com.intor.watchtime.repository.EpisodeRepository;
import com.intor.watchtime.repository.MediaRepository;
import com.intor.watchtime.repository.WatchRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WatchServiceTest {

    @Mock
    private WatchRepository watchRepository;
    @Mock
    private MediaRepository mediaRepository;
    @Mock
    private EpisodeRepository episodeRepository;
    @Mock
    private ExternalMediaService externalMediaService;
    @Mock
    private CurrentUserService currentUserService;
    @Mock
    private WatchMapper watchMapper;
    @Mock
    private MediaMapper mediaMapper;

    @InjectMocks
    private WatchService watchService;

    @Captor
    private ArgumentCaptor<List<Watch>> watchesCaptor;
    @Captor
    private ArgumentCaptor<List<Episode>> episodesCaptor;

    private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID MEDIA_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    private static User user() {
        return User.builder().id(USER_ID).issuer("https://issuer.example.com/").subject("subject-1").build();
    }

    private static Media series(long externalShowId) {
        return Media.builder()
                .id(MEDIA_ID)
                .externalShowId(externalShowId)
                .title("Some series")
                .isMovie(false)
                .provider(Provider.TMDB)
                .build();
    }

    private static Episode episode(int number) {
        return Episode.builder()
                .id(UUID.randomUUID())
                .seasonNumber(1)
                .episodeNumber(number)
                .title("E" + number)
                .build();
    }

    private static SeasonDetailsResponse season(Integer... episodeNumbers) {
        List<SeasonDetailsResponse.SeasonEpisode> episodes = Arrays.stream(episodeNumbers)
                .map(n -> new SeasonDetailsResponse.SeasonEpisode(
                        n.longValue(), "E" + n, "overview", "2026-01-01", n, 1, null, 30, 7.5, null))
                .toList();
        return new SeasonDetailsResponse(1L, "season overview", episodes);
    }

    private static WatchRequest seriesRequest(Integer seasonNumber, Integer episodeNumber) {
        return new WatchRequest(99L, seasonNumber, episodeNumber, false, null, null, null, null);
    }

    @Test
    @DisplayName("marking a partially watched season stores only the missing episodes, but records a watch for every one")
    void createForSeason_persistsOnlyMissingEpisodesButWatchesEveryOne() {
        Media media = series(99L);
        Episode existing = episode(2);

        when(currentUserService.get()).thenReturn(user());
        when(mediaRepository.findByExternalShowIdAndProvider(eq(99L), any())).thenReturn(Optional.of(media));
        when(externalMediaService.getSeasonDetails(99L, 1)).thenReturn(season(1, 2, 3));
        when(episodeRepository.findAllByMediaAndSeasonNumber(media, 1)).thenReturn(List.of(existing));
        when(mediaMapper.toEpisodeEntity(any(), eq(media)))
                .thenAnswer(call -> episode(((SeasonDetailsResponse.SeasonEpisode) call.getArgument(0)).episodeNumber()));
        when(episodeRepository.saveAll(episodesCaptor.capture())).thenAnswer(call -> call.getArgument(0));
        when(watchRepository.saveAll(watchesCaptor.capture())).thenAnswer(call -> call.getArgument(0));

        watchService.createForSeason(new WatchSeasonRequest(99L, 1, null, null, null));

        assertThat(episodesCaptor.getValue())
                .extracting(Episode::getEpisodeNumber)
                .containsExactly(1, 3);

        assertThat(watchesCaptor.getValue()).hasSize(3);
        assertThat(watchesCaptor.getValue())
                .allSatisfy(watch -> assertThat(watch.getEpisode()).isNotNull());
        assertThat(watchesCaptor.getValue())
                .extracting(watch -> watch.getEpisode().getEpisodeNumber())
                .containsExactly(1, 2, 3);
    }

    @Test
    @DisplayName("marking an already stored season reuses its episodes instead of duplicating them")
    void createForSeason_reusesExistingEpisodes_withoutPersistingAnyNewOnes() {
        Media media = series(99L);

        when(currentUserService.get()).thenReturn(user());
        when(mediaRepository.findByExternalShowIdAndProvider(eq(99L), any())).thenReturn(Optional.of(media));
        when(externalMediaService.getSeasonDetails(99L, 1)).thenReturn(season(1, 2));
        when(episodeRepository.findAllByMediaAndSeasonNumber(media, 1))
                .thenReturn(List.of(episode(1), episode(2)));
        when(episodeRepository.saveAll(episodesCaptor.capture())).thenAnswer(call -> call.getArgument(0));
        when(watchRepository.saveAll(watchesCaptor.capture())).thenAnswer(call -> call.getArgument(0));

        watchService.createForSeason(new WatchSeasonRequest(99L, 1, null, null, null));

        assertThat(episodesCaptor.getValue()).isEmpty();
        assertThat(watchesCaptor.getValue()).hasSize(2);
        verify(mediaMapper, never()).toEpisodeEntity(any(), any());
    }

    @Test
    @DisplayName("a series watch without a season and episode number is rejected as a bad request")
    void create_rejectsSeriesWatchWithoutEpisodeCoordinates() {
        when(currentUserService.get()).thenReturn(user());
        when(mediaRepository.findByExternalShowIdAndProvider(eq(99L), any()))
                .thenReturn(Optional.of(series(99L)));

        WatchRequest request = seriesRequest(null, 3);

        assertThatThrownBy(() -> watchService.create(request))
                .isInstanceOf(InvalidRequestException.class);

        verify(watchRepository, never()).save(any());
    }

    @Test
    @DisplayName("an episode the provider does not know about fails as an upstream error, saving nothing")
    void create_failsWhenProviderDoesNotKnowTheEpisode() {
        Media media = series(99L);

        when(currentUserService.get()).thenReturn(user());
        when(mediaRepository.findByExternalShowIdAndProvider(eq(99L), any())).thenReturn(Optional.of(media));
        when(episodeRepository.findByMediaAndSeasonNumberAndEpisodeNumber(media, 1, 5))
                .thenReturn(Optional.empty());
        when(externalMediaService.getSeasonDetails(99L, 1)).thenReturn(season(1, 2));

        WatchRequest request = seriesRequest(1, 5);

        assertThatThrownBy(() -> watchService.create(request))
                .isInstanceOf(ExternalMediaException.class);

        verify(episodeRepository, never()).save(any());
        verify(watchRepository, never()).save(any());
    }

    @Test
    @DisplayName("leaving out the watch date on update keeps the previous one instead of clearing it")
    void update_keepsPreviousWatchedAt_whenRequestOmitsIt() {
        Instant original = Instant.parse("2026-01-01T20:00:00Z");
        Watch watch = Watch.builder()
                .id(UUID.randomUUID())
                .user(user())
                .media(series(99L))
                .episode(episode(1))
                .watchedAt(original)
                .build();

        when(currentUserService.get()).thenReturn(user());
        when(watchRepository.findByIdAndUserId(watch.getId(), USER_ID)).thenReturn(Optional.of(watch));
        when(episodeRepository.findByMediaAndSeasonNumberAndEpisodeNumber(any(), eq(1), eq(1)))
                .thenReturn(Optional.of(episode(1)));
        when(watchRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        watchService.update(watch.getId(), seriesRequest(1, 1));

        assertThat(watch.getWatchedAt()).isEqualTo(original);
        verify(watchMapper).toResponse(watch);
    }

    @Test
    @DisplayName("pointing an update at a different title swaps the media and drops the episode")
    void update_swapsMedia_whenExternalIdChanged() {
        Media current = series(99L);
        Media replacement = series(123L);
        Watch watch = Watch.builder()
                .id(UUID.randomUUID())
                .user(user())
                .media(current)
                .watchedAt(Instant.parse("2026-01-01T20:00:00Z"))
                .build();

        when(currentUserService.get()).thenReturn(user());
        when(watchRepository.findByIdAndUserId(watch.getId(), USER_ID)).thenReturn(Optional.of(watch));
        when(mediaRepository.findByExternalShowIdAndProvider(eq(123L), any())).thenReturn(Optional.of(replacement));
        when(watchRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        watchService.update(watch.getId(), new WatchRequest(123L, null, null, true, null, null, null, null));

        assertThat(watch.getMedia()).isSameAs(replacement);
        assertThat(watch.getEpisode()).isNull();
    }

    @Test
    @DisplayName("an update for the same title never looks the media up again")
    void update_leavesMediaAlone_whenExternalIdMatches() {
        Media current = series(99L);
        Watch watch = Watch.builder()
                .id(UUID.randomUUID())
                .user(user())
                .media(current)
                .watchedAt(Instant.parse("2026-01-01T20:00:00Z"))
                .build();

        when(currentUserService.get()).thenReturn(user());
        when(watchRepository.findByIdAndUserId(watch.getId(), USER_ID)).thenReturn(Optional.of(watch));
        when(watchRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        watchService.update(watch.getId(), new WatchRequest(99L, null, null, true, null, null, null, null));

        assertThat(watch.getMedia()).isSameAs(current);
        verify(mediaRepository, never()).findByExternalShowIdAndProvider(anyLong(), any());
    }

    @Test
    @DisplayName("deleting watches for an unknown title reports the title as missing")
    void deleteAllByMediaId_reportsMissingMedia_whenNothingDeletedAndMediaUnknown() {
        when(currentUserService.get()).thenReturn(user());
        when(watchRepository.deleteAllByMediaIdAndUserId(MEDIA_ID, USER_ID)).thenReturn(0L);
        when(mediaRepository.existsById(MEDIA_ID)).thenReturn(false);

        assertThatThrownBy(() -> watchService.deleteAllByMediaId(MEDIA_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Media");
    }

    @Test
    @DisplayName("deleting watches the user does not have reports the watches as missing, not the title")
    void deleteAllByMediaId_reportsMissingWatches_whenMediaExistsButUserHasNone() {
        when(currentUserService.get()).thenReturn(user());
        when(watchRepository.deleteAllByMediaIdAndUserId(MEDIA_ID, USER_ID)).thenReturn(0L);
        when(mediaRepository.existsById(MEDIA_ID)).thenReturn(true);

        assertThatThrownBy(() -> watchService.deleteAllByMediaId(MEDIA_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Watch for media");
    }

    @Test
    @DisplayName("deleting watches that exist succeeds without checking whether the title exists")
    void deleteAllByMediaId_succeeds_whenSomethingWasDeleted() {
        when(currentUserService.get()).thenReturn(user());
        when(watchRepository.deleteAllByMediaIdAndUserId(MEDIA_ID, USER_ID)).thenReturn(3L);

        watchService.deleteAllByMediaId(MEDIA_ID);

        verify(mediaRepository, never()).existsById(any());
    }

    @Test
    @DisplayName("removing the last episode watch returns the date of the one still remaining")
    void deleteLastEpisodeWatch_returnsRemainingLatestWatch() {
        Instant remaining = Instant.parse("2026-01-01T20:00:00Z");

        when(currentUserService.get()).thenReturn(user());
        when(watchRepository.deleteLastEpisodeWatch(MEDIA_ID, USER_ID, 1, 2)).thenReturn(1L);
        when(watchRepository.findEpisodeLatestWatch(eq(MEDIA_ID), anyInt(), anyInt(), eq(USER_ID)))
                .thenReturn(Optional.of(remaining));

        assertThat(watchService.deleteLastEpisodeWatch(MEDIA_ID, 1, 2)).isEqualTo(remaining);
    }

    @Test
    @DisplayName("removing an episode watch that is not there fails without asking for a remaining date")
    void deleteLastEpisodeWatch_failsWhenNothingWasDeleted() {
        when(currentUserService.get()).thenReturn(user());
        when(watchRepository.deleteLastEpisodeWatch(MEDIA_ID, USER_ID, 1, 2)).thenReturn(0L);

        assertThatThrownBy(() -> watchService.deleteLastEpisodeWatch(MEDIA_ID, 1, 2))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(watchRepository, never()).findEpisodeLatestWatch(any(), anyInt(), anyInt(), any());
    }
}
