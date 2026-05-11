package com.FurryArtyPawPrints.ViewModule.interfaces;

import com.FurryArtyPawPrints.ViewModule.models.UserModel;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface FAPPInterface {

    ResponseEntity<Map<String, String>> health();
    ResponseEntity<Map<String, String>> info();
    Mono<ResponseEntity<UserModel>> createUser(UserModel userModel);
}
