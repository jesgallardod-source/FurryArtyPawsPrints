package com.FurryArtyPawPrints.ViewModule.controller;

import com.FurryArtyPawPrints.ViewModule.constants.FAPPConstants;
import com.FurryArtyPawPrints.ViewModule.interfaces.FAPPInterface;
import com.FurryArtyPawPrints.ViewModule.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping(FAPPConstants.BASE)
public class MainController {

    @Autowired
    FAPPInterface fappInterface;

    @GetMapping(FAPPConstants.HEALTH)
    public ResponseEntity<Map<String, String>> health() {
        return fappInterface.health();
    }

    @GetMapping(FAPPConstants.INFO)
    public ResponseEntity<Map<String, String>> info() {

        return fappInterface.info();
    }


    @PostMapping(FAPPConstants.USER)
    public Mono<ResponseEntity<UserModel>> createUser(@RequestBody UserModel userModel) {
        return fappInterface.createUser(userModel);
    }
}
