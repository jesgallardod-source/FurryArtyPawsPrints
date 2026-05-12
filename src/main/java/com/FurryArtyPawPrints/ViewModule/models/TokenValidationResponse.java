package com.FurryArtyPawPrints.ViewModule.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenValidationResponse {
    private Boolean isValid;
    private String userEmail;
    private Integer userId;
    private Long expiresIn; // milliseconds
    private String message;
}
