package com.intor.watchtime.dto.response.media.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intor.watchtime.dto.response.media.MediaPageResponse;

import java.util.List;

/**
 * Internal representation of a paginated response from the TMDB API.
 * <p>
 * Mirrors the raw JSON structure returned by TMDB.
 * <p>
 * For the outward-facing API response, see {@link MediaPageResponse}.
 * Keeping these two types separate decouples our public API from TMDB's schema:
 * if TMDB changes their field names, only this class needs to be updated.
 */
public record TMDBPage<T>(
        int page,
        List<T> results,
        @JsonProperty("total_pages") int totalPages,
        @JsonProperty("total_results") int totalResults
) {}
