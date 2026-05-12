package com.FurryArtyPawPrints.ViewModule.interfaces;

import com.FurryArtyPawPrints.ViewModule.models.LoginRequest;
import com.FurryArtyPawPrints.ViewModule.models.LoginResponse;
import com.FurryArtyPawPrints.ViewModule.models.UserModel;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface FAPPInterface {

    ResponseEntity<Map<String, String>> health();
    ResponseEntity<Map<String, String>> info();
    Mono<ResponseEntity<UserModel>> createUser(UserModel userModel);
    Mono<ResponseEntity<LoginResponse>> authenticateUser(LoginRequest loginRequest);
    Mono<ResponseEntity<Map<String, Object>>> validateToken(String token);
}
