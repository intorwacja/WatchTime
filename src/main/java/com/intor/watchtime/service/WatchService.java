package com.intor.watchtime.service;

import com.intor.watchtime.dto.request.WatchRequest;
import com.intor.watchtime.dto.response.WatchResponse;

import java.util.List;
import java.util.UUID;

public interface WatchService {

    WatchResponse create(WatchRequest request);

    WatchResponse getById(UUID id);

    List<WatchResponse> getAll();

    WatchResponse update(UUID id, WatchRequest request);

    void delete(UUID id);
}
