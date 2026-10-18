package com.intor.watchtime.service;

import com.intor.watchtime.dto.response.changelog.ChangelogEntryJson;
import com.intor.watchtime.dto.response.changelog.ChangelogEntryResponse;
import com.intor.watchtime.entity.User;
import com.intor.watchtime.exception.InvalidRequestException;
import com.intor.watchtime.mapper.ChangelogMapper;
import com.intor.watchtime.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChangelogService {

    private static final String CHANGELOG_PATH = "changelog.json";

    private final ObjectMapper objectMapper;
    private final ChangelogMapper changelogMapper;
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;

    @Getter
    private List<ChangelogEntryJson> entries;

    @Getter
    private int latestId;

    @PostConstruct
    void load() {
        load(CHANGELOG_PATH);
    }

    void load(String path) {
        List<ChangelogEntryJson> loaded = read(path);

        long distinctIds = loaded.stream().map(ChangelogEntryJson::id).collect(Collectors.toSet()).size();
        if (distinctIds != loaded.size()) {
            throw new IllegalStateException("Duplicate ids in " + path);
        }

        entries = loaded.stream()
                .sorted(Comparator.comparingInt(ChangelogEntryJson::id).reversed())
                .toList();
        latestId = entries.isEmpty() ? 0 : entries.getFirst().id();
    }

    @Transactional(readOnly = true)
    public List<ChangelogEntryResponse> getUnseen(Locale locale) {
        int lastSeenId = currentUserService.get().getLastSeenChangelogId();
        return entries.stream()
                .filter(entry -> entry.id() > lastSeenId)
                .map(entry -> changelogMapper.toResponse(entry, locale))
                .toList();
    }

    @Transactional
    public void markSeen(int id) {
        if (entries.stream().noneMatch(entry -> entry.id() == id)) {
            throw new InvalidRequestException("Unknown changelog id: " + id);
        }

        User user = currentUserService.get();
        if (id <= user.getLastSeenChangelogId()) {
            return;
        }

        user.setLastSeenChangelogId(id);
        userRepository.save(user);
    }

    private List<ChangelogEntryJson> read(String path) {
        try (InputStream inputStream = new ClassPathResource(path).getInputStream()) {
            return objectMapper.readValue(inputStream, new TypeReference<>() {});
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read " + path, e);
        }
    }
}
