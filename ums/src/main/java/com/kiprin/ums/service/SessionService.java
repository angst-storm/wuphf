package com.kiprin.ums.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kiprin.ums.domain.Session;
import com.kiprin.ums.domain.User;
import com.kiprin.ums.dto.LoginRequest;
import com.kiprin.ums.repository.SessionRepository;
import com.kiprin.ums.repository.UserRepository;

@Service
public class SessionService {

    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final AuthorizationService authorizationService;

    public SessionService(
            SessionRepository sessionRepository,
            UserRepository userRepository,
            AuthorizationService authorizationService) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public Session open(LoginRequest login) {
        if (login == null
                || login.getEmail() == null || login.getEmail().isBlank()
                || login.getPassword() == null || login.getPassword().isBlank()) {
            throw new BadRequestException("email and password are required");
        }

        User user = userRepository.findByEmail(login.getEmail().trim())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!PASSWORD_ENCODER.matches(login.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        Session session = new Session();
        session.setId(UUID.randomUUID());
        session.setUserId(user.getId());
        session.setLoggedInAt(LocalDateTime.now());
        return sessionRepository.save(session);
    }

    @Transactional
    public Session close(UUID actorId, UUID sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Session not found or already closed"));
        if (session.getLoggedOutAt() != null) {
            throw new NotFoundException("Session not found or already closed");
        }
        authorizationService.requireAdminOrSelf(actorId, session.getUserId());
        session.setLoggedOutAt(LocalDateTime.now());
        return sessionRepository.save(session);
    }

    public Optional<Session> findLastForUser(UUID actorId, UUID userId) {
        authorizationService.requireAdminOrSelf(actorId, userId);
        return sessionRepository.findFirstByUserIdOrderByLoggedInAtDesc(userId);
    }

    public Session requireActive(UUID sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new UnauthorizedException("Invalid or expired session"));
        if (session.getLoggedOutAt() != null) {
            throw new UnauthorizedException("Invalid or expired session");
        }
        return session;
    }
}
