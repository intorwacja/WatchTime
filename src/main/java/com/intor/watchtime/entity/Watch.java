package com.intor.watchtime.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "watches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Watch {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    private Long externalShowId;
    private Long externalEpisodeId;

    private Integer seasonNumber;
    private Integer episodeNumber;

    @Builder.Default
    private Boolean isMovie = false;

    @Builder.Default
    private Boolean isSpecial = false;

    private Integer runtimeSeconds;

    @Builder.Default
    private Integer rewatchCount = 0;

    @Column(nullable = false)
    private LocalDateTime watchedAt;

    private String markedViaSource;

    private String watchedOnPlatform;

    private Integer rating;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
