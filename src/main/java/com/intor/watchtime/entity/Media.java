package com.intor.watchtime.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "media",
    uniqueConstraints = @UniqueConstraint(
            name = "uq_media_provider_external_id",
            columnNames = {"external_show_id", "provider"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private long externalShowId;

    @Column(nullable = false)
    private String title;

    @Builder.Default
    private boolean isMovie = false;

    private String posterPath;

    private String backdropPath;

    private Integer numberOfEpisodes;

    private Integer runtimeMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @Column(nullable = false)
    private Instant lastSyncedAt;

    @PrePersist
    private void prePersist() {
        if (lastSyncedAt == null) {
            lastSyncedAt = Instant.now();
        }
    }
}
