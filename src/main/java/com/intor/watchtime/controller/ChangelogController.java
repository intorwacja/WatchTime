package com.intor.watchtime.controller;

import com.intor.watchtime.dto.request.ChangelogSeenRequest;
import com.intor.watchtime.dto.response.changelog.ChangelogEntryResponse;
import com.intor.watchtime.service.ChangelogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/changelog")
@RequiredArgsConstructor
public class ChangelogController {

    private final ChangelogService changelogService;

    @GetMapping
    public List<ChangelogEntryResponse> getUnseen(Locale locale) {
        return changelogService.getUnseen(locale);
    }

    @PostMapping("/seen")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markSeen(@Valid @RequestBody ChangelogSeenRequest request) {
        changelogService.markSeen(request.id());
    }
}
