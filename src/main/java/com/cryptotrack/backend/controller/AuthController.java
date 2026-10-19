package com.cryptotrack.backend.controller;

import com.cryptotrack.backend.dto.LoginRequest;
import com.cryptotrack.backend.dto.LoginResponse;
import com.cryptotrack.backend.entity.User;
import com.cryptotrack.backend.security.JwtService;
import com.cryptotrack.backend.service.UserService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log =
            LoggerFactory.getLogger(AuthController.class);

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

        long start = System.nanoTime();

        // Find user and verify password
        User user = userService.login(request);

        long authenticationEnd = System.nanoTime();

        // Generate JWT
        String token = jwtService.generateToken(user.getEmail());

        long end = System.nanoTime();

        long authenticationMs =
                (authenticationEnd - start) / 1_000_000;

        long jwtMs =
                (end - authenticationEnd) / 1_000_000;

        long totalMs =
                (end - start) / 1_000_000;

        log.info(
                "AUTH_TIMING authentication={}ms, jwt={}ms, total={}ms",
                authenticationMs,
                jwtMs,
                totalMs
        );

        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                token
        );
    }
}