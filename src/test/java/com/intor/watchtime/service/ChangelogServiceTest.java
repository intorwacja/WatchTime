package com.intor.watchtime.service;

import com.intor.watchtime.dto.response.changelog.ChangelogEntryJson;
import com.intor.watchtime.dto.response.changelog.ChangelogEntryResponse;
import com.intor.watchtime.entity.User;
import com.intor.watchtime.exception.InvalidRequestException;
import com.intor.watchtime.mapper.ChangelogMapper;
import com.intor.watchtime.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.json.JsonMapper;

import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangelogServiceTest {

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private UserRepository userRepository;

    private ChangelogService changelogService;

    @BeforeEach
    void setUp() {
        changelogService = new ChangelogService(
                JsonMapper.builder().build(), new ChangelogMapper(), currentUserService, userRepository);
    }

    private User givenUserWhoSaw(int lastSeenChangelogId) {
        User user = User.builder().lastSeenChangelogId(lastSeenChangelogId).build();
        when(currentUserService.get()).thenReturn(user);
        return user;
    }

    @Test
    @DisplayName("loads entries sorted from newest to oldest regardless of their order in the file")
    void loadsEntriesSortedById() {
        changelogService.load("changelog/changelog.json");

        assertThat(changelogService.getEntries()).extracting(ChangelogEntryJson::id).containsExactly(3, 2, 1);
        assertThat(changelogService.getLatestId()).isEqualTo(3);
    }

    @Test
    @DisplayName("an empty changelog has no latest entry")
    void emptyChangelogHasLatestIdZero() {
        changelogService.load("changelog/changelog-empty.json");

        assertThat(changelogService.getEntries()).isEmpty();
        assertThat(changelogService.getLatestId()).isZero();
    }

    @Test
    @DisplayName("refuses to start when two entries share an id")
    void failsOnDuplicateIds() {
        assertThatThrownBy(() -> changelogService.load("changelog/changelog-duplicate-ids.json"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("refuses to start when the changelog file is missing")
    void failsOnMissingFile() {
        assertThatThrownBy(() -> changelogService.load("changelog/missing.json"))
                .isInstanceOf(UncheckedIOException.class);
    }

    @Test
    @DisplayName("a user who has seen nothing gets every entry, newest first")
    void returnsAllEntriesForUserWhoSawNothing() {
        changelogService.load("changelog/changelog.json");
        givenUserWhoSaw(0);

        List<ChangelogEntryResponse> unseen = changelogService.getUnseen(Locale.ENGLISH);

        assertThat(unseen).extracting(ChangelogEntryResponse::id).containsExactly(3, 2, 1);
    }

    @Test
    @DisplayName("a user who missed a few releases gets only the entries newer than the last one seen")
    void returnsOnlyNewerEntries() {
        changelogService.load("changelog/changelog.json");
        givenUserWhoSaw(1);

        List<ChangelogEntryResponse> unseen = changelogService.getUnseen(Locale.ENGLISH);

        assertThat(unseen).extracting(ChangelogEntryResponse::id).containsExactly(3, 2);
    }

    @Test
    @DisplayName("a user who has seen the latest entry gets an empty list")
    void returnsNothingForUpToDateUser() {
        changelogService.load("changelog/changelog.json");
        givenUserWhoSaw(3);

        assertThat(changelogService.getUnseen(Locale.ENGLISH)).isEmpty();
    }

    @Test
    @DisplayName("maps an entry to the response in the requested language")
    void translatesToRequestedLanguage() {
        changelogService.load("changelog/changelog.json");
        givenUserWhoSaw(0);

        ChangelogEntryResponse first = changelogService.getUnseen(Locale.of("pl", "PL")).getLast();

        assertThat(first.id()).isEqualTo(1);
        assertThat(first.version()).isEqualTo("1.0.0");
        assertThat(first.date()).isEqualTo(LocalDate.of(2026, 1, 10));
        assertThat(first.title()).isEqualTo("Pierwsze wydanie");
        assertThat(first.changes()).containsExactly("Pierwsza wersja");
    }

    @Test
    @DisplayName("falls back to English when the requested language has no translation")
    void fallsBackToEnglish() {
        changelogService.load("changelog/changelog.json");
        givenUserWhoSaw(0);

        List<ChangelogEntryResponse> unseen = changelogService.getUnseen(Locale.GERMAN);

        assertThat(unseen.getFirst().title()).isEqualTo("Statistics");
        assertThat(unseen.getFirst().changes()).containsExactly("Added statistics", "Faster loading");
        assertThat(unseen.getLast().title()).isEqualTo("First release");
    }

    @Test
    @DisplayName("marking a newer entry as seen stores it on the user")
    void marksNewerEntryAsSeen() {
        changelogService.load("changelog/changelog.json");
        User user = givenUserWhoSaw(1);

        changelogService.markSeen(3);

        assertThat(user.getLastSeenChangelogId()).isEqualTo(3);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("marking an older or the same entry as seen never moves the user back")
    void ignoresOlderOrSameEntry() {
        changelogService.load("changelog/changelog.json");
        User user = givenUserWhoSaw(2);

        changelogService.markSeen(1);
        changelogService.markSeen(2);

        assertThat(user.getLastSeenChangelogId()).isEqualTo(2);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("rejects an id that is not in the changelog")
    void rejectsUnknownId() {
        changelogService.load("changelog/changelog.json");

        assertThatThrownBy(() -> changelogService.markSeen(99))
                .isInstanceOf(InvalidRequestException.class);
        verify(userRepository, never()).save(any());
    }
}
