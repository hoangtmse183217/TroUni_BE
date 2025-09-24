package com.trouni.tro_uni.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "Username or email is required")
    private String usernameOrEmail; // This can be either username or email
    
    @NotBlank(message = "Password is required")
    private String password;
}
