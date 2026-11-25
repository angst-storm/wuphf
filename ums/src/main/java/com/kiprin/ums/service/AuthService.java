package com.kiprin.ums.service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.kiprin.common.auth.JwtService;
import com.kiprin.common.auth.RoleName;
import com.kiprin.common.exception.UnauthorizedException;
import com.kiprin.ums.domain.Role;
import com.kiprin.ums.domain.User;
import com.kiprin.ums.domain.UserRole;
import com.kiprin.ums.dto.LoginRequest;
import com.kiprin.ums.dto.LoginResponse;
import com.kiprin.ums.repository.RoleRepository;
import com.kiprin.ums.repository.UserRepository;

@Service
public class AuthService {

    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest login) {
        User user = userRepository.findByEmail(login.getEmail().trim())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!PASSWORD_ENCODER.matches(login.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String accessToken = jwtService.issue(user.getId(), loadRoleNames(user));
        return new LoginResponse(user.getId(), accessToken);
    }

    private List<RoleName> loadRoleNames(User user) {
        if (user.getRoleRefs() == null || user.getRoleRefs().isEmpty()) {
            return List.of();
        }
        Set<UUID> roleIds = user.getRoleRefs().stream()
                .map(UserRole::getRoleId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return StreamSupport.stream(roleRepository.findAllById(roleIds).spliterator(), false)
                .map(Role::getName)
                .filter(Objects::nonNull)
                .toList();
    }
}
