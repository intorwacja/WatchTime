package com.intor.watchtime.dto.response.changelog;

import java.time.LocalDate;
import java.util.List;

public record ChangelogEntryResponse(
        int id,
        String version,
        LocalDate date,
        String title,
        List<String> changes
) {
}
