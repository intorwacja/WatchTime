package com.intor.watchtime.service;

import com.intor.watchtime.config.TMDBClient;
import com.intor.watchtime.dto.response.media.MovieDetailsResponse;
import com.intor.watchtime.dto.response.media.MediaPageResponse;
import com.intor.watchtime.dto.response.media.SeasonDetailsResponse;
import com.intor.watchtime.dto.response.media.SeriesDetailsResponse;

public interface ExternalMediaService {

    MediaPageResponse getTrendingAll(TMDBClient.TimeWindow timeWindow, int page);

    MovieDetailsResponse getMovieDetails(Long id);

    SeriesDetailsResponse getSeriesDetails(Long id);

    SeasonDetailsResponse getSeasonDetails(Long seriesId, Integer seasonNumber);

    MediaPageResponse search(String query, int page);
}
