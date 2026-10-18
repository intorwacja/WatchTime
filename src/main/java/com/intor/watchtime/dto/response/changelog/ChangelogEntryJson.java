package com.intor.watchtime.dto.response.changelog;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record ChangelogEntryJson(
        int id,
        String version,
        LocalDate date,
        Map<String, String> title,
        List<Map<String, String>> changes
) {
}
