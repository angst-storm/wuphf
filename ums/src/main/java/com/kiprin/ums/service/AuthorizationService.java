package com.kiprin.ums.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.kiprin.ums.domain.Role;
import com.kiprin.ums.domain.RoleName;
import com.kiprin.ums.domain.User;
import com.kiprin.ums.domain.UserRole;
import com.kiprin.ums.repository.RoleRepository;
import com.kiprin.ums.repository.UserRepository;

@Service
public class AuthorizationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AuthorizationService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public boolean isAdmin(UUID actorId) {
        User actor = userRepository.findById(actorId)
                .orElseThrow(() -> new UnauthorizedException("Not authenticated"));
        Role admin = roleRepository.findByName(RoleName.ADMIN)
                .orElseThrow(() -> new IllegalStateException("ADMIN role is not seeded"));
        if (actor.getRoleRefs() == null) {
            return false;
        }
        return actor.getRoleRefs().stream()
                .map(UserRole::getRoleId)
                .anyMatch(admin.getId()::equals);
    }

    public void requireAdmin(UUID actorId) {
        if (!isAdmin(actorId)) {
            throw new ForbiddenException("Admin role required");
        }
    }

    public void requireAdminOrSelf(UUID actorId, UUID targetUserId) {
        if (actorId.equals(targetUserId)) {
            return;
        }
        requireAdmin(actorId);
    }
}
