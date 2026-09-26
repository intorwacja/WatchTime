package com.intor.watchtime.dto.response.media.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TMDBAggregateCredits(
        List<TMDBAggregateCastMember> cast
) {
    public record TMDBAggregateCastMember(
            String name,
            @JsonProperty("known_for_department") String knownForDepartment,
            @JsonProperty("profile_path") String profilePath,
            List<Role> roles
    ) {
        public record Role(String character) {}
    }
}
