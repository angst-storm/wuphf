package com.kiprin.ums.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.kiprin.ums.domain.User;

public interface UserRepository extends CrudRepository<User, UUID> {

    List<User> findAllByOrderByCreatedAtAscIdAsc();

    Optional<User> findByEmail(String email);
}
