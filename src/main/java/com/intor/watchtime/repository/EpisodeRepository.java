package com.intor.watchtime.repository;

import com.intor.watchtime.entity.Episode;
import com.intor.watchtime.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, UUID> {

    Optional<Episode> findByMediaAndSeasonNumberAndEpisodeNumber(Media media, Integer seasonNumber, Integer episodeNumber);

    List<Episode> findAllByMediaAndSeasonNumber(Media media, Integer seasonNumber);
}
