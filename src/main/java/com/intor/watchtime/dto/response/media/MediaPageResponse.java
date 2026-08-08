package com.intor.watchtime.dto.response.media;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Public paginated response returned by our own API to frontend clients.
 * <p>
 * The service layer is responsible for mapping external media providers into this type.
 */
public record MediaPageResponse(
        int page,
        List<MediaResponse> results,
        @JsonProperty("total_pages") int totalPages,
        @JsonProperty("total_results") int totalResults
) {}
