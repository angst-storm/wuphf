package com.kiprin.ums.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.kiprin.ums.domain.Role;
import com.kiprin.ums.domain.RoleName;

public interface RoleRepository extends CrudRepository<Role, UUID> {

    List<Role> findAllByOrderByNameAsc();

    Optional<Role> findByName(RoleName name);
}
