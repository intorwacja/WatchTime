package com.intor.watchtime.dto.response;

import com.intor.watchtime.entity.User;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String displayName,
        String pictureUrl
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getPictureUrl()
        );
    }
}
