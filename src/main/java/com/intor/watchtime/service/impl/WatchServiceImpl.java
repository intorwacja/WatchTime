package com.intor.watchtime.service.impl;

import com.intor.watchtime.dto.request.WatchRequest;
import com.intor.watchtime.dto.response.WatchResponse;
import com.intor.watchtime.entity.Watch;
import com.intor.watchtime.exception.ResourceNotFoundException;
import com.intor.watchtime.mapper.WatchMapper;
import com.intor.watchtime.repository.WatchRepository;
import com.intor.watchtime.service.WatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WatchServiceImpl implements WatchService {

    private final WatchRepository watchRepository;
    private final WatchMapper watchMapper;

    @Override
    @Transactional
    public WatchResponse create(WatchRequest request) {
        Watch watch = watchMapper.toEntity(request);
        return watchMapper.toResponse(watchRepository.save(watch));
    }

    @Override
    public WatchResponse getById(UUID id) {
        Watch watch = watchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Watch", id));
        return watchMapper.toResponse(watch);
    }

    @Override
    public List<WatchResponse> getAll() {
        return watchRepository.findAll().stream()
                .map(watchMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public WatchResponse update(UUID id, WatchRequest request) {
        Watch watch = watchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Watch", id));
        watch.setTitle(request.title());
        watch.setExternalShowId(request.externalShowId());
        watch.setExternalEpisodeId(request.externalEpisodeId());
        watch.setSeasonNumber(request.seasonNumber());
        watch.setEpisodeNumber(request.episodeNumber());
        watch.setIsMovie(Boolean.TRUE.equals(request.isMovie()));
        watch.setIsSpecial(Boolean.TRUE.equals(request.isSpecial()));
        watch.setRuntimeSeconds(request.runtimeSeconds());
        watch.setRewatchCount(request.rewatchCount() != null ? request.rewatchCount() : 0);
        watch.setWatchedAt(request.watchedAt());
        watch.setMarkedViaSource(request.markedViaSource());
        watch.setWatchedOnPlatform(request.watchedOnPlatform());
        watch.setRating(request.rating());
        return watchMapper.toResponse(watchRepository.save(watch));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!watchRepository.existsById(id)) {
            throw new ResourceNotFoundException("Watch", id);
        }
        watchRepository.deleteById(id);
    }
}
