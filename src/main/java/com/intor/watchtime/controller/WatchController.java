package com.intor.watchtime.controller;

import com.intor.watchtime.dto.request.WatchRequest;
import com.intor.watchtime.dto.request.WatchSeasonRequest;
import com.intor.watchtime.dto.response.WatchResponse;
import com.intor.watchtime.dto.response.WatchedViewPageResponse;
import com.intor.watchtime.service.WatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/watches")
@RequiredArgsConstructor
public class WatchController {

    private final WatchService watchService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WatchResponse create(@Valid @RequestBody WatchRequest request) {
        return watchService.create(request);
    }

    @PostMapping("/season")
    @ResponseStatus(HttpStatus.CREATED)
    public List<WatchResponse> createForSeason(@Valid @RequestBody WatchSeasonRequest request) {
        return watchService.createForSeason(request);
    }

    @GetMapping("/{id}")
    public WatchResponse getById(@PathVariable UUID id) {
        return watchService.getById(id);
    }

    @GetMapping
    public WatchedViewPageResponse getWatchedList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return watchService.getWatchedList(page, size);
    }

    @PutMapping("/{id}")
    public WatchResponse update(@PathVariable UUID id, @Valid @RequestBody WatchRequest request) {
        return watchService.update(id, request);
    }

    @DeleteMapping("/media/{mediaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllByMediaId(@PathVariable UUID mediaId) {
        watchService.deleteAllByMediaId(mediaId);
    }

    @DeleteMapping("/media/{mediaId}/last")
    @ResponseStatus(HttpStatus.OK)
    public Instant deleteLastMovieWatch(@PathVariable UUID mediaId) {
        return watchService.deleteLastWatchByMediaId(mediaId);
    }

    @DeleteMapping("/media/{mediaId}/seasons/{seasonNumber}/episodes/{episodeNumber}")
    @ResponseStatus(HttpStatus.OK)
    public Instant deleteLastEpisodeWatch(@PathVariable UUID mediaId, @PathVariable int seasonNumber, @PathVariable int episodeNumber) {
        return watchService.deleteLastEpisodeWatch(mediaId, seasonNumber, episodeNumber);
    }
}
