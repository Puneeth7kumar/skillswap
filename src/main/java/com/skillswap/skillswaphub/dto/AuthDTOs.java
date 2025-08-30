package com.skillswap.skillswaphub.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public class AuthDTOs {

    // Login Request DTO
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class LoginRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;

    }

    // Register Request DTO
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class RegisterRequest {
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

    }

    // JWT Response DTO
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class JwtResponse {
        private String token;
        @Builder.Default
        private String type = "Bearer";
        private Long id;
        private String name;
        private String email;

    }

    // Message Response DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MessageResponse {
        private String message;
    }
}
