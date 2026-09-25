package com.intor.watchtime.controller;

import com.intor.watchtime.dto.response.UserResponse;
import com.intor.watchtime.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CurrentUserService currentUserService;

    @GetMapping("/me")
    public UserResponse me() {
        return UserResponse.from(currentUserService.get());
    }
}
