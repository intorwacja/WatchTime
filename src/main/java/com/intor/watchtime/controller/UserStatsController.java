package com.intor.watchtime.controller;

import com.intor.watchtime.dto.response.UserStatsResponse;
import com.intor.watchtime.service.UserStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
public class UserStatsController {

    private final UserStatsService userStatsService;

    @GetMapping
    public UserStatsResponse getStats(@RequestParam(defaultValue = "UTC") String zone,
                                      @RequestParam(defaultValue = "7") int days) {
        return userStatsService.getStatsForCurrentUser(zone, days);
    }
}
