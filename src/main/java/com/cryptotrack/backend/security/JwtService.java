package com.cryptotrack.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET_KEY =
            "VGhpcy1pcy1hLXN1cGVyLXNlY3JldC1rZXktZm9yLWNyeXB0by10cmFjaw==";


    private final SecretKey key;


    public JwtService() {

        this.key =
                Keys.hmacShaKeyFor(
                        Decoders.BASE64.decode(
                                SECRET_KEY
                        )
                );
    }


    // =========================================================
    // GENERATE TOKEN
    // =========================================================

    public String generateToken(
            String email
    ) {

        return Jwts.builder()

                .subject(email)

                .issuedAt(
                        new Date()
                )

                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 1000L * 60 * 60
                        )
                )

                .signWith(key)

                .compact();
    }


    // =========================================================
    // EXTRACT EMAIL
    // =========================================================

    public String extractEmail(
            String token
    ) {

        return extractAllClaims(token)
                .getSubject();
    }


    // =========================================================
    // VALIDATE TOKEN
    // =========================================================

    public boolean isTokenValid(
            String token
    ) {

        try {

            Claims claims =
                    extractAllClaims(token);

            return claims
                    .getExpiration()
                    .after(new Date());

        } catch (Exception e) {

            return false;
        }
    }


    // =========================================================
    // EXTRACT CLAIMS
    // =========================================================

    private Claims extractAllClaims(
            String token
    ) {

        return Jwts.parser()

                .verifyWith(key)

                .build()

                .parseSignedClaims(token)

                .getPayload();
    }
}