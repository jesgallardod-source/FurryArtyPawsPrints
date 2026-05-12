package com.FurryArtyPawPrints.ViewModule.repositories;


import com.FurryArtyPawPrints.ViewModule.models.LoginRequest;
import com.FurryArtyPawPrints.ViewModule.models.LoginResponse;
import com.FurryArtyPawPrints.ViewModule.models.UserModel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Map;

@Repository
public interface UserRepository extends ReactiveCrudRepository<UserModel, Integer> {
    Mono<UserModel> findByUserEmail(String userEmail);  // Already in your code
    Mono<Boolean> existsByUserEmail(String userEmail);
}