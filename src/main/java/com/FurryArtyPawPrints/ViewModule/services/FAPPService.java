package com.FurryArtyPawPrints.ViewModule.services;

import com.FurryArtyPawPrints.ViewModule.interfaces.FAPPInterface;
import com.FurryArtyPawPrints.ViewModule.models.UserModel;
import com.FurryArtyPawPrints.ViewModule.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class FAPPService implements FAPPInterface {

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<Map<String, String>> health() {
        // Simple health check response
        return ResponseEntity.ok(Map.of("status", "UP", "service", "FAPP"));
    }

    @Override
    public ResponseEntity<Map<String, String>> info() {
        // Basic info response; customize as needed
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

        // Flatten SocialMedia into UserModel for R2DBC
        if (userModel.getUserTwitter() == null && userModel.getUserFacebook() == null &&
                userModel.getUserBlueSky() == null && userModel.getUserPatreon() == null) {
            // If no social media provided, set defaults or handle accordingly
        }

        // Save user reactively and return response
        return userRepository.save(userModel)
                .map(savedUser -> ResponseEntity.status(201).body(savedUser))
                .onErrorResume(error -> {
                    // Log error and return error response
                    System.err.println("Error saving user: " + error.getMessage());
                    return Mono.just(ResponseEntity.status(500).build());
                });
    }
}
