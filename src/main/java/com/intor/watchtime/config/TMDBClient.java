package com.intor.watchtime.config;

import com.intor.watchtime.dto.response.media.tmdb.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface TMDBClient {

    @GetExchange("/search/multi")
    TMDBPage<TMDBResponse> searchAll(@RequestParam("query") String query,
                                     @RequestParam("page") int page);

    @GetExchange("/trending/all/{timeWindow}")
    TMDBPage<TMDBResponse> getTrendingAll(@PathVariable TimeWindow timeWindow,
                                          @RequestParam("page") int page);

    @GetExchange("/movie/{id}")
    TMDBMovieDetailsResponse getMovieDetails(@PathVariable long id,
                                             @RequestParam("append_to_response") String appendToResponse);

    @GetExchange("/tv/{id}")
    TMDBSeriesDetailsResponse getSeriesDetails(@PathVariable long id,
                                               @RequestParam("append_to_response") String appendToResponse);

    @GetExchange("/tv/{seriesId}/season/{seasonNumber}")
    TMDBSeasonDetails getSeasonDetails(@PathVariable long seriesId, @PathVariable int seasonNumber);


    enum TimeWindow {
        day, week    //NOSONAR: TMDB requires lowercase letters
    }
}
