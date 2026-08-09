package com.intor.watchtime.dto.response.media;

public record CastMember(
        String name,
        String knownForDepartment,
        String profilePath,
        String character
) {}
