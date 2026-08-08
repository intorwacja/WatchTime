package com.intor.watchtime.repository;

import com.intor.watchtime.dto.response.media.SeriesWatchInfo;
import com.intor.watchtime.dto.response.WatchedViewResponse;
import com.intor.watchtime.dto.response.media.MovieWatchInfo;
import com.intor.watchtime.repository.projection.EpisodeWatchInfoProjection;
import com.intor.watchtime.repository.projection.SeasonWatchInfoProjection;
import com.intor.watchtime.entity.Provider;
import com.intor.watchtime.entity.Watch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WatchRepository extends JpaRepository<Watch, UUID> {

    @Modifying
    @Query(nativeQuery = true, value = """
      DELETE FROM watches
      WHERE id = (
          SELECT id FROM watches
          WHERE media_id = :mediaId
          ORDER BY watched_at DESC
          LIMIT 1
      )
      """)
    long deleteLastByMediaId(@Param("mediaId") UUID mediaId);

    @Modifying
    @Query(nativeQuery = true, value = """
      DELETE FROM watches
      WHERE id = (
          SELECT w.id FROM watches w
          JOIN episodes e ON e.id = w.episode_id
          WHERE w.media_id = :mediaId
            AND e.season_number = :seasonNumber
            AND e.episode_number = :episodeNumber
          ORDER BY w.watched_at DESC
          LIMIT 1
      )
      """)
    long deleteLastEpisodeWatch(@Param("mediaId") UUID mediaId, int seasonNumber, int episodeNumber);

    @Modifying
    @Query("DELETE FROM Watch w WHERE w.media.id = :mediaId")
    long deleteAllByMedia_Id(UUID mediaId);

    @Query("""
            SELECT new com.intor.watchtime.dto.response.WatchedViewResponse(
                m.id,
                m.externalShowId,
                m.title,
                m.posterPath,
                m.isMovie,
                COUNT(DISTINCT( COALESCE(w.episode.id, w.id))),
                m.numberOfEpisodes,
                MAX(w.watchedAt),
                MAX(CASE WHEN w.episode IS NULL THEN w.rating END)
            )
            FROM Watch w JOIN w.media m
            GROUP BY m.id, m.title, m.posterPath, m.isMovie, m.numberOfEpisodes
            ORDER BY MAX(w.watchedAt) DESC
            """)
    Page<WatchedViewResponse> findWatchedLibrary(Pageable pageable);

    @Query("""
      SELECT new com.intor.watchtime.dto.response.media.MovieWatchInfo(
          m.id,
          MAX(w.watchedAt),
          COUNT(w)
      )
      FROM Watch w JOIN w.media m
      WHERE m.provider = :provider
        AND m.externalShowId = :externalMediaId
      GROUP BY m.id
  """)
    Optional<MovieWatchInfo> findMovieWatchInfo(
            @Param("externalMediaId") Long externalMediaId,
            @Param("provider") Provider provider);

    @Query("""
      SELECT new com.intor.watchtime.dto.response.media.SeriesWatchInfo(
          m.id,
          MAX(w.watchedAt)
      )
      FROM Watch w JOIN w.media m
      WHERE m.provider = :provider
        AND m.externalShowId = :externalMediaId
      GROUP BY m.id
  """)
    Optional<SeriesWatchInfo> findSeriesWatchInfo(long externalMediaId, Provider provider);

    @Query("""
      SELECT new com.intor.watchtime.repository.projection.SeasonWatchInfoProjection(
          w.episode.seasonNumber,
          COUNT(DISTINCT w.episode.id)
      )
      FROM Watch w
      WHERE w.episode IS NOT NULL
        AND w.media.provider = :provider
        AND w.media.externalShowId = :externalMediaId
      GROUP BY w.episode.seasonNumber
  """)
    List<SeasonWatchInfoProjection> findSeasonWatchInfos(@Param("externalMediaId") Long externalMediaId,
                                                         @Param("provider") Provider provider);

    @Query("""
      SELECT new com.intor.watchtime.repository.projection.EpisodeWatchInfoProjection(
          w.episode.id,
          w.episode.episodeNumber,
          MAX(w.watchedAt),
          COUNT(w.id)
      )
      FROM Watch w
      WHERE w.episode IS NOT NULL
        AND w.media.provider = :provider
        AND w.media.externalShowId = :externalMediaId
        AND w.episode.seasonNumber = :seasonNumber
      GROUP BY w.episode.id, w.episode.episodeNumber
  """)
    List<EpisodeWatchInfoProjection> findEpisodeWatchInfosInSeason(@Param("externalMediaId") Long externalMediaId,
                                                                   @Param("provider") Provider provider,
                                                                   @Param("seasonNumber") Integer seasonNumber);
    boolean existsByMedia_Id(UUID mediaId);

    @Query("""
      SELECT COUNT(w) > 0 FROM Watch w
      WHERE w.media.id = :mediaId
        AND w.episode.seasonNumber = :seasonNumber
        AND w.episode.episodeNumber = :episodeNumber
    """)
    boolean existsForEpisode(UUID mediaId, int episodeNumber, int seasonNumber);

    @Query("SELECT MAX(w.watchedAt) FROM Watch w WHERE w.media.id = :mediaId")
    Optional<Instant> findMovieLatestWatch(@Param("mediaId") UUID mediaId);

    @Query("""
        SELECT MAX(w.watchedAt)
        FROM Watch w
        WHERE w.media.id = :mediaId
            AND w.episode.seasonNumber = :seasonNumber
            AND w.episode.episodeNumber = :episodeNumber
    """)
    Optional<Instant> findEpisodeLatestWatch(@Param("mediaId") UUID mediaId,
                                             @Param("seasonNumber") int seasonNumber,
                                             @Param("episodeNumber") int episodeNumber);
}
