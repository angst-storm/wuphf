package com.kiprin.ums.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kiprin.ums.domain.Role;
import com.kiprin.ums.repository.RoleRepository;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> findAll() {
        return roleRepository.findAllByOrderByNameAsc();
    }
}
