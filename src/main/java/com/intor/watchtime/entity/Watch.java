package com.intor.watchtime.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "watches",
    indexes = {
            @Index(name = "idx_watches_user_media_watched_at", columnList = "user_id, media_id, watched_at DESC"),
            @Index(name = "idx_watches_user_episode_watched_at", columnList = "user_id, episode_id, watched_at DESC")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Watch {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id", nullable = false)
    private Media media;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id")
    private Episode episode;

    @Column(nullable = false)
    private Instant watchedAt;

    private String markedViaSource;

    private String watchedOnPlatform;

    @DecimalMin("0.0")
    @DecimalMax("10.0")
    private Double rating;

    @Column(updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    private void prePersist() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        if (watchedAt == null) {
            watchedAt = Instant.now();
        }
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = Instant.now();
    }
}
