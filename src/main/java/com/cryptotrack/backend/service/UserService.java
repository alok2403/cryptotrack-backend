package com.cryptotrack.backend.service;

import com.cryptotrack.backend.dto.LoginRequest;
import com.cryptotrack.backend.dto.UserRequest;
import com.cryptotrack.backend.entity.User;
import com.cryptotrack.backend.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger log =
            LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(UserRequest request) {

        long start = System.nanoTime();

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        long hashStart = System.nanoTime();

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        long hashEnd = System.nanoTime();

        User savedUser = userRepository.save(user);

        long saveEnd = System.nanoTime();

        log.info(
                "REGISTER_TIMING passwordHash={}ms dbSave={}ms total={}ms",
                (hashEnd - hashStart) / 1_000_000,
                (saveEnd - hashEnd) / 1_000_000,
                (saveEnd - start) / 1_000_000
        );

        return savedUser;
    }

    public User login(LoginRequest request) {

        long start = System.nanoTime();

        Optional<User> result =
                userRepository.findByEmail(request.getEmail());

        long lookupEnd = System.nanoTime();

        log.info(
                "LOGIN_TIMING userLookup={}ms",
                (lookupEnd - start) / 1_000_000
        );

        User user = result.orElseThrow(() ->
                new RuntimeException("Invalid email or password")
        );

        boolean matches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        long passwordEnd = System.nanoTime();

        log.info(
                "LOGIN_TIMING passwordCheck={}ms serviceTotal={}ms",
                (passwordEnd - lookupEnd) / 1_000_000,
                (passwordEnd - start) / 1_000_000
        );

        if (!matches) {
            throw new RuntimeException(
                    "Invalid email or password"
            );
        }

        return user;
    }

    public User findByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );
    }
}