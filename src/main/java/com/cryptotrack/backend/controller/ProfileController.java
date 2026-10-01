package com.cryptotrack.backend.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProfileController {

    @GetMapping("/profile")
    public String profile(Authentication authentication) {

        String email = authentication.getName();

        return "Authenticated successfully! Welcome " + email;
    }
}