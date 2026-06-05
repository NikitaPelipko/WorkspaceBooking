package com.plsrflttr.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {
    @Test
    void generatesAndParsesToken() {
        String secret = "Rk1uUE1rYkF1WmE3bmZtbG1sQ0tyc2M5QmE3N2xwN0lOQ0ZPVGxwVT0=";
        JwtService service = new JwtService(secret, 5);
        User user = new User("user@example.com", "hash", java.util.List.of());

        String token = service.generateToken(user, java.util.UUID.randomUUID());
        String username = service.extractUsername(token);

        assertEquals("user@example.com", username);
        assertTrue(service.isTokenValid(token, user));
    }
}

