package com.intor.watchtime.repository;

import com.intor.watchtime.entity.Media;
import com.intor.watchtime.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediaRepository extends JpaRepository<Media, UUID> {
    Optional<Media> findByExternalShowIdAndProvider(Long externalShowId, Provider provider);
}
