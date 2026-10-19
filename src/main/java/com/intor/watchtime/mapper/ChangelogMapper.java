package com.intor.watchtime.mapper;

import com.intor.watchtime.dto.response.changelog.ChangelogEntryJson;
import com.intor.watchtime.dto.response.changelog.ChangelogEntryResponse;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

@Component
public class ChangelogMapper {

    private static final String DEFAULT_LANGUAGE = "en";

    public ChangelogEntryResponse toResponse(ChangelogEntryJson entry, Locale locale) {
        String language = locale.getLanguage();
        return new ChangelogEntryResponse(
                entry.id(),
                entry.version(),
                entry.date(),
                translate(entry.title(), language),
                entry.changes().stream()
                        .map(change -> translate(change, language))
                        .toList()
        );
    }

    private String translate(Map<String, String> translations, String language) {
        return translations.getOrDefault(language, translations.get(DEFAULT_LANGUAGE));
    }
}
