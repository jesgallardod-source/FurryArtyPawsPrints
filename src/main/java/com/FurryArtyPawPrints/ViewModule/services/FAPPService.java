package com.FurryArtyPawPrints.ViewModule.services;

import com.FurryArtyPawPrints.ViewModule.interfaces.FAPPInterface;
import com.FurryArtyPawPrints.ViewModule.models.UserModel;
import com.FurryArtyPawPrints.ViewModule.models.LoginRequest;
import com.FurryArtyPawPrints.ViewModule.models.LoginResponse;
import com.FurryArtyPawPrints.ViewModule.repositories.UserRepository;
import com.FurryArtyPawPrints.ViewModule.security.JwtUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
public class FAPPService implements FAPPInterface {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtility jwtUtility;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Override
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "FAPP"));
    }

    @Override
    public ResponseEntity<Map<String, String>> info() {
        return ResponseEntity.ok(Map.of(
                "app", "Furry-Arty-Paw-Prints ViewModule",
                "version", "1.0.0",
                "description", "A Spring Boot application for user management with R2DBC"
        ));
    }

    @Override
    public Mono<ResponseEntity<UserModel>> createUser(UserModel userModel) {
        // Set creation timestamp if not already set
        if (userModel.getUserComDate() == null) {
            userModel.setUserComDate(LocalDateTime.now());
        }

        // Hash the password before saving
        String hashedPassword = passwordEncoder.encode(userModel.getUserPassword());
        userModel.setUserPassword(hashedPassword);

        // Save user reactively and return response
        return userRepository.save(userModel)
                .map(savedUser -> {
                    log.info("User created successfully with email: {}", savedUser.getUserEmail());
                    return ResponseEntity.status(201).body(savedUser);
                })
                .onErrorResume(error -> {
                    log.error("Error saving user: {}", error.getMessage(), error);
                    return Mono.just(ResponseEntity.status(500).build());
                });
    }

    /**
     * Authenticate user with email and password
     * @param loginRequest LoginRequest with email and password
     * @return Mono<LoginResponse> with JWT token if successful
     */
    public Mono<ResponseEntity<LoginResponse>> authenticateUser(LoginRequest loginRequest) {
        return userRepository.findByUserEmail(loginRequest.getUserEmail())
                .flatMap(user -> {
                    // Verify password
                    if (passwordEncoder.matches(loginRequest.getUserPassword(), user.getUserPassword())) {
                        // Generate JWT token
                        String token = jwtUtility.generateToken(user.getUserEmail(), user.getUserId());

                        // Update last login
                        user.setLastLogin(LocalDateTime.now());
                        return userRepository.save(user)
                                .map(updatedUser -> {
                                    LoginResponse response = new LoginResponse();
                                    response.setToken(token);
                                    response.setTokenType("Bearer");
                                    response.setExpiresIn(jwtExpirationMs / 1000); // Convert to seconds
                                    response.setUserId(user.getUserId());
                                    response.setUserEmail(user.getUserEmail());
                                    response.setUserName(user.getUserName());

                                    log.info("User authenticated successfully: {}", user.getUserEmail());
                                    return ResponseEntity.ok(response);
                                });
                    } else {
                        log.warn("Invalid password for user: {}", loginRequest.getUserEmail());
                        return Mono.just(ResponseEntity.status(401)
                                .body(new LoginResponse(null, "Bearer", 0, null, null, null)));
                    }
                })
                .onErrorResume(error -> {
                    log.error("Authentication error: {}", error.getMessage());
                    return Mono.just(ResponseEntity.status(401).build());
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(401).build()));
    }

    /**
     * Validate JWT token
     * @param token JWT token to validate
     * @return Mono<ResponseEntity> with validation result
     */
    public Mono<ResponseEntity<Map<String, Object>>> validateToken(String token) {
        try {
            // Extract token from Bearer prefix if present
            String extractedToken = token;
            if (token.startsWith("Bearer ")) {
                extractedToken = token.substring(7);
            }

            if (jwtUtility.validateToken(extractedToken)) {
                String email = jwtUtility.extractEmail(extractedToken);
                Integer userId = jwtUtility.extractUserId(extractedToken);
                long expiresIn = jwtUtility.getExpirationTimeRemaining(extractedToken);

                log.info("Token validated for user: {}", email);
                return Mono.just(ResponseEntity.ok(Map.of(
                        "valid", true,
                        "email", email,
                        "userId", userId,
                        "expiresIn", expiresIn
                )));
            } else {
                log.warn("Invalid or expired token");
                return Mono.just(ResponseEntity.status(401)
                        .body(Map.of("valid", false, "message", "Invalid or expired token")));
            }
        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            return Mono.just(ResponseEntity.status(400)
                    .body(Map.of("valid", false, "message", e.getMessage())));
        }
    }
}
