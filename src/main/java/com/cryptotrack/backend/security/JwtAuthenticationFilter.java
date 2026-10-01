package com.cryptotrack.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {


    private final JwtService jwtService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public JwtAuthenticationFilter(
            JwtService jwtService
    ) {

        this.jwtService = jwtService;
    }


    // =========================================================
    // FILTER
    // =========================================================

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {


        String authorizationHeader =
                request.getHeader("Authorization");


        // -----------------------------------------------------
        // NO TOKEN
        // -----------------------------------------------------

        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }


        // -----------------------------------------------------
        // EXTRACT TOKEN
        // -----------------------------------------------------

        String token =
                authorizationHeader.substring(7);


        try {

            // -------------------------------------------------
            // VALIDATE TOKEN
            // -------------------------------------------------

            if (jwtService.isTokenValid(token)) {

                String email =
                        jwtService.extractEmail(token);


                if (email != null &&
                        !email.isBlank()) {


                    UsernamePasswordAuthenticationToken
                            authentication =

                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    null,
                                    AuthorityUtils.NO_AUTHORITIES
                            );


                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(
                                    authentication
                            );
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Invalid JWT: "
                            + e.getMessage()
            );
        }


        // -----------------------------------------------------
        // CONTINUE FILTER CHAIN
        // -----------------------------------------------------

        filterChain.doFilter(
                request,
                response
        );
    }
}