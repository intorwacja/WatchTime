package com.intor.watchtime.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record WatchedViewPageResponse(
        int page,
        List<WatchedViewResponse> results,
        @JsonProperty("total_pages") int totalPages,
        @JsonProperty("total_results") long totalResults
) {}
