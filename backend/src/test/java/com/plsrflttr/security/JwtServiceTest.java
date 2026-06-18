package com.plsrflttr.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {
    @Test
    void generatesAndParsesToken() {
        String accessSecret = "Rk1uUE1rYkF1WmE3bmZtbG1sQ0tyc2M5QmE3N2xwN0lOQ0ZPVGxwVT0=";
        String refreshSecret = "9MOGEQ+i2nb1fGt7JvJhKuY/FYyKnSFTN+OzxFaAlyA=";
        JwtService service = new JwtService(accessSecret, refreshSecret, 5, 7);
        User user = new User("user@example.com", "hash", java.util.List.of());

        String token = service.generateAccessToken(user, java.util.UUID.randomUUID());
        String username = service.extractUsernameFromAccessToken(token);

        assertEquals("user@example.com", username);
        assertTrue(service.isAccessTokenValid(token, user));
    }
}
