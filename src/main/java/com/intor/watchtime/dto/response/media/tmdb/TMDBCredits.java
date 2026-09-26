package com.intor.watchtime.dto.response.media.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TMDBCredits(
        List<TMDBCastMember> cast
) {
    public record TMDBCastMember(
            String name,
            @JsonProperty("known_for_department") String knownForDepartment,
            @JsonProperty("profile_path") String profilePath,
            String character
    ) {}
}
