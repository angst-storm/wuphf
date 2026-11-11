package com.kiprin.ums.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.kiprin.ums.domain.Session;

public interface SessionRepository extends CrudRepository<Session, UUID> {

    Optional<Session> findFirstByUserIdOrderByLoggedInAtDesc(UUID userId);
}
