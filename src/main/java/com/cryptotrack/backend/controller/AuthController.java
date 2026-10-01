package com.cryptotrack.backend.controller;

import com.cryptotrack.backend.dto.LoginRequest;
import com.cryptotrack.backend.dto.LoginResponse;
import com.cryptotrack.backend.entity.User;
import com.cryptotrack.backend.service.UserService;
import com.cryptotrack.backend.security.JwtService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(
            UserService userService,
            JwtService jwtService) {

        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request) {

        User user = userService.login(request);

        String token = jwtService.generateToken(
                user.getEmail()
        );

        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                token
        );
    }
}