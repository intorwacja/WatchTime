CREATE TABLE episodes
(
    id              UUID                        NOT NULL,
    media_id        UUID                        NOT NULL,
    season_number   INTEGER                     NOT NULL,
    episode_number  INTEGER                     NOT NULL,
    title           VARCHAR(255),
    overview        VARCHAR(2000),
    runtime_minutes INTEGER,
    still_path      VARCHAR(255),
    is_special      BOOLEAN                     NOT NULL,
    last_synced_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_episodes PRIMARY KEY (id)
);

CREATE TABLE media
(
    id                 UUID                        NOT NULL,
    external_show_id   BIGINT                      NOT NULL,
    title              VARCHAR(255)                NOT NULL,
    is_movie           BOOLEAN                     NOT NULL,
    poster_path        VARCHAR(255),
    backdrop_path      VARCHAR(255),
    number_of_episodes INTEGER,
    runtime_minutes    INTEGER,
    provider           VARCHAR(255)                NOT NULL,
    last_synced_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_media PRIMARY KEY (id)
);

CREATE TABLE users
(
    id           UUID         NOT NULL,
    issuer       VARCHAR(255) NOT NULL,
    subject      VARCHAR(255) NOT NULL,
    email        VARCHAR(255),
    display_name VARCHAR(255),
    picture_url  VARCHAR(255),
    created_at   TIMESTAMP WITHOUT TIME ZONE,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE watches
(
    id                  UUID                        NOT NULL,
    user_id             UUID                        NOT NULL,
    media_id            UUID                        NOT NULL,
    episode_id          UUID,
    watched_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    marked_via_source   VARCHAR(255),
    watched_on_platform VARCHAR(255),
    rating              DOUBLE PRECISION,
    created_at          TIMESTAMP WITHOUT TIME ZONE,
    updated_at          TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_watches PRIMARY KEY (id)
);

ALTER TABLE episodes
    ADD CONSTRAINT uq_media_episode UNIQUE (media_id, season_number, episode_number);

ALTER TABLE media
    ADD CONSTRAINT uq_media_provider_external_id UNIQUE (external_show_id, provider);

ALTER TABLE users
    ADD CONSTRAINT uq_users_issuer_subject UNIQUE (issuer, subject);

CREATE INDEX idx_watches_user_episode_watched_at ON watches (user_id, episode_id, watched_at DESC);

CREATE INDEX idx_watches_user_media_watched_at ON watches (user_id, media_id, watched_at DESC);

ALTER TABLE episodes
    ADD CONSTRAINT FK_EPISODES_ON_MEDIA FOREIGN KEY (media_id) REFERENCES media (id);

ALTER TABLE watches
    ADD CONSTRAINT FK_WATCHES_ON_EPISODE FOREIGN KEY (episode_id) REFERENCES episodes (id);

ALTER TABLE watches
    ADD CONSTRAINT FK_WATCHES_ON_MEDIA FOREIGN KEY (media_id) REFERENCES media (id);

ALTER TABLE watches
    ADD CONSTRAINT FK_WATCHES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);