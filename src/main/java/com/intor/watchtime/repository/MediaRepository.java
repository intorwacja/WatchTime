package com.intor.watchtime.repository;

import com.intor.watchtime.entity.Media;
import com.intor.watchtime.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface MediaRepository extends JpaRepository<Media, UUID> {
    Optional<Media> findByExternalShowIdAndProvider(Long externalShowId, Provider provider);

    @Query("""
      SELECT m.externalShowId FROM Media m
      WHERE m.provider = :provider
        AND m.externalShowId IN :externalIds
        AND EXISTS (SELECT 1 FROM Watch w WHERE w.media = m)
  """)
    Set<Long> findWatchedExternalIds(@Param("externalIds") Set<Long> externalIds,
                                     @Param("provider") Provider provider);
}
