package com.intor.watchtime.repository;

import com.intor.watchtime.entity.Watch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WatchRepository extends JpaRepository<Watch, UUID> {
}
