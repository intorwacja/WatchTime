package com.intor.watchtime.controller;

import com.intor.watchtime.dto.request.WatchRequest;
import com.intor.watchtime.dto.response.WatchResponse;
import com.intor.watchtime.service.WatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/watches")
@RequiredArgsConstructor
public class WatchController {

    private final WatchService watchService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WatchResponse create(@Valid @RequestBody WatchRequest request) {
        return watchService.create(request);
    }

    @GetMapping("/{id}")
    public WatchResponse getById(@PathVariable UUID id) {
        return watchService.getById(id);
    }

    @GetMapping
    public List<WatchResponse> getAll() {
        return watchService.getAll();
    }

    @PutMapping("/{id}")
    public WatchResponse update(@PathVariable UUID id, @Valid @RequestBody WatchRequest request) {
        return watchService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        watchService.delete(id);
    }
}
