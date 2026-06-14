package com.nikita.kitchentracker.auth;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final AppUserRepository userRepository;
    private final AuthSessionRepository sessionRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(AppUserRepository userRepository, AuthSessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    public AuthResponse register(AuthRequest request) {
        String email = normalizeEmail(request.getEmail());
        userRepository.findByEmailIgnoreCase(email).ifPresent(user -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account already exists for this email.");
        });

        AppUser user = new AppUser();
        user.setEmail(email);
        user.setDisplayName(normalizeDisplayName(request.getDisplayName(), email));
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        return createSession(userRepository.save(user));
    }

    public AuthResponse login(AuthRequest request) {
        AppUser user = userRepository.findByEmailIgnoreCase(normalizeEmail(request.getEmail()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password."));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        }
        return createSession(user);
    }

    public AuthResponse currentUser(String authorization) {
        AppUser user = requireUser(authorization);
        return new AuthResponse(null, user.getEmail(), displayName(user));
    }

    public AuthResponse updateProfile(String authorization, ProfileRequest request) {
        AppUser user = requireUser(authorization);
        user.setDisplayName(normalizeDisplayName(request.getDisplayName(), user.getEmail()));
        AppUser saved = userRepository.save(user);
        return new AuthResponse(null, saved.getEmail(), displayName(saved));
    }

    @Transactional
    public void logout(String authorization) {
        String token = extractToken(authorization);
        if (token != null) {
            sessionRepository.deleteByToken(token);
        }
    }

    public AppUser requireUser(String authorization) {
        String token = extractToken(authorization);
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required.");
        }
        return sessionRepository.findByToken(token)
                .map(AuthSession::getUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required."));
    }

    private AuthResponse createSession(AppUser user) {
        AuthSession session = new AuthSession();
        session.setToken(newToken());
        session.setUser(user);
        session.setCreatedAt(LocalDateTime.now());
        AuthSession saved = sessionRepository.save(session);
        return new AuthResponse(saved.getToken(), user.getEmail(), displayName(user));
    }

    private String newToken() {
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String extractToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        return authorization.substring("Bearer ".length()).trim();
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private String normalizeDisplayName(String displayName, String email) {
        if (displayName != null && !displayName.isBlank()) {
            return displayName.trim();
        }
        int at = email.indexOf('@');
        return at > 0 ? email.substring(0, at) : email;
    }

    private String displayName(AppUser user) {
        return normalizeDisplayName(user.getDisplayName(), user.getEmail());
    }
}
