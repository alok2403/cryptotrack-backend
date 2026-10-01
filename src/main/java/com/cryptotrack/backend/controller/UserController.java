package com.cryptotrack.backend.controller;

import com.cryptotrack.backend.dto.UserRequest;
import com.cryptotrack.backend.entity.User;
import com.cryptotrack.backend.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(
            @Valid @RequestBody UserRequest request) {

        User user = userService.createUser(request);

        // Don't return password
        user.setPassword(null);

        return user;
    }
}