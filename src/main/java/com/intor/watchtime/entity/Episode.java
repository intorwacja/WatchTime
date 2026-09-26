package com.intor.watchtime.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "episodes",
    uniqueConstraints = @UniqueConstraint(
            name = "uq_media_episode",
            columnNames = {"media_id", "season_number", "episode_number"}
    ))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Episode {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id", nullable = false)
    private Media media;

    @Column(nullable = false)
    private Integer seasonNumber;

    @Column(nullable = false)
    private Integer episodeNumber;

    private String title;

    @Column(length = 2000)
    private String overview;

    private Integer runtimeMinutes;

    private String stillPath;

    @Builder.Default
    private boolean isSpecial = false;

    @Column(nullable = false)
    private Instant lastSyncedAt;

    @PrePersist
    private void prePersist() {
        if (lastSyncedAt == null) {
            lastSyncedAt = Instant.now();
        }
    }
}
