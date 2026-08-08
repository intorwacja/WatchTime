package com.intor.watchtime.controller;

import com.intor.watchtime.config.TMDBClient;
import com.intor.watchtime.dto.response.media.SeasonDetailsResponse;
import com.intor.watchtime.dto.response.media.MediaPageResponse;
import com.intor.watchtime.dto.response.media.MovieDetailsResponse;
import com.intor.watchtime.dto.response.media.SeriesDetailsResponse;
import com.intor.watchtime.service.ExternalMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/media")
public class MediaController {

    private final ExternalMediaService externalMediaService;

    @GetMapping("/movies/{id}")
    public MovieDetailsResponse getMovieDetails(@PathVariable Long id) {
        return externalMediaService.getMovieDetails(id);
    }

    @GetMapping("/series/{id}")
    public SeriesDetailsResponse getSeriesDetails(@PathVariable Long id) {
        return externalMediaService.getSeriesDetails(id);
    }

    @GetMapping("/series/{id}/{seasonNumber}")
    public SeasonDetailsResponse getSeasonDetails(@PathVariable Long id, @PathVariable Integer seasonNumber) {
        return externalMediaService.getSeasonDetails(id, seasonNumber);
    }

    @GetMapping("/trending")
    public MediaPageResponse getTrendingAll(
            @RequestParam(defaultValue = "week") TMDBClient.TimeWindow timeWindow,
            @RequestParam(defaultValue = "1") int page) {
        return externalMediaService.getTrendingAll(timeWindow, page);
    }

    @GetMapping("/search")
    public MediaPageResponse search(@RequestParam String query, @RequestParam(defaultValue = "1") int page) {
        return externalMediaService.search(query, page);
    }
}
