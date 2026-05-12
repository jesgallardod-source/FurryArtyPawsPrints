package com.FurryArtyPawPrints.ViewModule.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";
    private long expiresIn;
    private Integer userId;
    private String userEmail;
    private String userName;
}
