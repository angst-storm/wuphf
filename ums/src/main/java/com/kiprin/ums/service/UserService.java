package com.kiprin.ums.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kiprin.common.auth.RoleName;
import com.kiprin.common.exception.BadRequestException;
import com.kiprin.common.exception.DbCreateException;
import com.kiprin.common.exception.ForbiddenException;
import com.kiprin.common.exception.NotFoundException;
import com.kiprin.ums.domain.Role;
import com.kiprin.ums.domain.User;
import com.kiprin.ums.domain.UserRole;
import com.kiprin.ums.dto.PatchUserRequest;
import com.kiprin.ums.repository.RoleRepository;
import com.kiprin.ums.repository.UserRepository;

@Service
public class UserService {

    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthorizationService authorizationService;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            AuthorizationService authorizationService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authorizationService = authorizationService;
    }

    public List<User> findAll(UUID actorId) {
        authorizationService.requireAdmin(actorId);
        return userRepository.findAllByOrderByCreatedAtAscIdAsc().stream()
                .map(this::enrich)
                .toList();
    }

    public Optional<User> findById(UUID userId) {
        return userRepository.findById(userId).map(this::enrich);
    }

    public User getById(UUID userId) {
        return findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Transactional
    public User create(User request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(encodePassword(request.getPasswordHash()));
        Set<UUID> roleIds = resolveRoleIds(request.getRoles());
        forbidAdminRoleIds(roleIds);
        user.replaceRoleIds(roleIds);
        prepareForCreate(user);
        try {
            return enrich(userRepository.save(user));
        } catch (DbActionExecutionException e) {
            throw new DbCreateException(e);
        }
    }

    @Transactional
    public User update(UUID actorId, UUID userId, User request) {
        authorizationService.requireAdminOrSelf(actorId, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(encodePassword(request.getPasswordHash()));
        if (request.getRoles() != null) {
            if (!authorizationService.isAdmin(actorId)) {
                throw new ForbiddenException("Only admin can change roles");
            }
            user.replaceRoleIds(resolveRoleIds(request.getRoles()));
        }
        touchUpdatedAt(user);
        return enrich(userRepository.save(user));
    }

    @Transactional
    public User patch(UUID actorId, UUID userId, PatchUserRequest patch) {
        authorizationService.requireAdminOrSelf(actorId, userId);
        if (patch == null
                || (patch.getName() == null && patch.getEmail() == null && patch.getPasswordHash() == null)) {
            throw new BadRequestException("No fields to update");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (patch.getName() != null) {
            user.setName(patch.getName());
        }
        if (patch.getEmail() != null) {
            user.setEmail(patch.getEmail());
        }
        if (patch.getPasswordHash() != null && !patch.getPasswordHash().isBlank()) {
            user.setPasswordHash(encodePassword(patch.getPasswordHash()));
        }
        touchUpdatedAt(user);
        return enrich(userRepository.save(user));
    }

    @Transactional
    public void delete(UUID actorId, UUID userId) {
        authorizationService.requireAdmin(actorId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        userRepository.deleteById(userId);
    }

    @Transactional
    public User assignRole(UUID actorId, UUID userId, UUID roleId) {
        authorizationService.requireAdmin(actorId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (!roleRepository.existsById(roleId)) {
            throw new NotFoundException("Role not found");
        }
        user.assignRoleId(roleId);
        touchUpdatedAt(user);
        return enrich(userRepository.save(user));
    }

    private static void prepareForCreate(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }
        LocalDateTime now = LocalDateTime.now();
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(now);
        }
        user.setUpdatedAt(now);
    }

    private static void touchUpdatedAt(User user) {
        user.setUpdatedAt(LocalDateTime.now());
    }

    private User enrich(User user) {
        List<Role> roles = new ArrayList<>();
        if (user.getRoleRefs() != null && !user.getRoleRefs().isEmpty()) {
            Set<UUID> ids = user.getRoleRefs().stream()
                    .map(UserRole::getRoleId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            Map<UUID, Role> byId = StreamSupport.stream(roleRepository.findAllById(ids).spliterator(), false)
                    .collect(Collectors.toMap(Role::getId, Function.identity()));
            roles = ids.stream()
                    .map(byId::get)
                    .filter(Objects::nonNull)
                    .sorted((a, b) -> a.getName().compareTo(b.getName()))
                    .toList();
        }
        user.setRoles(roles);
        return user;
    }

    private Set<UUID> resolveRoleIds(List<Role> roles) {
        Set<UUID> ids = new HashSet<>();
        if (roles == null) {
            return ids;
        }
        for (Role role : roles) {
            if (role == null) {
                continue;
            }
            if (role.getId() != null) {
                if (!roleRepository.existsById(role.getId())) {
                    throw new BadRequestException("Unknown role id: " + role.getId());
                }
                ids.add(role.getId());
            } else if (role.getName() != null) {
                Role found = roleRepository.findByName(role.getName())
                        .orElseThrow(() -> new BadRequestException("Unknown role: " + role.getName()));
                ids.add(found.getId());
            }
        }
        return ids;
    }

    private void forbidAdminRoleIds(Set<UUID> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        roleRepository.findByName(RoleName.ADMIN).ifPresent(admin -> {
            if (roleIds.contains(admin.getId())) {
                throw new ForbiddenException("Cannot self-assign ADMIN role");
            }
        });
    }

    private static String encodePassword(String passwordOrHash) {
        if (passwordOrHash == null || passwordOrHash.isBlank()) {
            return passwordOrHash;
        }
        if (passwordOrHash.startsWith("$2a$")
                || passwordOrHash.startsWith("$2b$")
                || passwordOrHash.startsWith("$2y$")) {
            return passwordOrHash;
        }
        return PASSWORD_ENCODER.encode(passwordOrHash);
    }
}
