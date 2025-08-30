package com.skillswap.skillswaphub.controller;

import com.skillswap.skillswaphub.dto.AuthDTOs;
import com.skillswap.skillswaphub.entities.User;
import com.skillswap.skillswaphub.security.JwtUtil;
import com.skillswap.skillswaphub.service.user_service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;
    private UserService userService;
    private JwtUtil jwtUtil;

    AuthController(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthDTOs.LoginRequest loginRequest) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            String jwt = jwtUtil.generateJwtToken(authentication);

            // Fetch user details
            User user = userService.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Return JSON response
            return ResponseEntity.ok(
                    AuthDTOs.JwtResponse.builder()
                            .token(jwt)
                            .id(user.getId())
                            .name(user.getName())
                            .email(user.getEmail())
                            .build()
            );
        } catch (Exception e) {
            // Return JSON message on failure
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthDTOs.MessageResponse("Invalid email or password"));
        }
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody AuthDTOs.RegisterRequest signUpRequest) {
        try {
            if (userService.existsByEmail(signUpRequest.getEmail())) {
                return ResponseEntity
                        .badRequest()
                        .body(new AuthDTOs.MessageResponse("Error: Email is already in use!"));
            }

            User user = userService.createUser(
                    signUpRequest.getName(),
                    signUpRequest.getEmail(),
                    signUpRequest.getPassword()
            );

            String jwt = jwtUtil.generateJwtToken(user.getEmail());

            // Always return JSON with proper structure
            return ResponseEntity.ok(
                    AuthDTOs.JwtResponse.builder()
                            .token(jwt)
                            .id(user.getId())
                            .name(user.getName())
                            .email(user.getEmail())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthDTOs.MessageResponse("Error: Could not register user!"));
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new AuthDTOs.MessageResponse("User logged out successfully!"));
    }
}
