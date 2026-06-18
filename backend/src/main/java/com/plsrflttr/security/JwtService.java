package com.plsrflttr.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtService {
    private static final String TOKEN_TYPE_CLAIM = "tokenType";
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";

    private final SecretKey accessKey;
    private final SecretKey refreshKey;
    private final long accessExpirationMinutes;
    private final long refreshExpirationDays;

    public JwtService(
            @Value("${app.jwt.access-secret}") String accessSecret,
            @Value("${app.jwt.refresh-secret}") String refreshSecret,
            @Value("${app.jwt.access-expiration-minutes}") long accessExpirationMinutes,
            @Value("${app.jwt.refresh-expiration-days}") long refreshExpirationDays
    ) {
        this.accessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecret));
        this.refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecret));
        this.accessExpirationMinutes = accessExpirationMinutes;
        this.refreshExpirationDays = refreshExpirationDays;
    }

    public String generateAccessToken(UserDetails userDetails, UUID userId) {
        return buildToken(userDetails, Map.of(
                "userId", userId.toString(),
                TOKEN_TYPE_CLAIM, TOKEN_TYPE_ACCESS
        ), Instant.now().plus(accessExpirationMinutes, ChronoUnit.MINUTES), accessKey);
    }

    public String generateRefreshToken(UserDetails userDetails, UUID userId) {
        return buildToken(userDetails, Map.of(
                "userId", userId.toString(),
                TOKEN_TYPE_CLAIM, TOKEN_TYPE_REFRESH
        ), Instant.now().plus(refreshExpirationDays, ChronoUnit.DAYS), refreshKey);
    }

    public String extractUsernameFromAccessToken(String token) {
        return extractClaim(token, Claims::getSubject, accessKey);
    }

    public String extractUsernameFromRefreshToken(String token) {
        return extractClaim(token, Claims::getSubject, refreshKey);
    }

    public UUID extractUserIdFromRefreshToken(String token) {
        String value = extractClaim(token, claims -> claims.get("userId", String.class), refreshKey);
        return UUID.fromString(value);
    }

    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        String username = extractUsernameFromAccessToken(token);
        return username.equals(userDetails.getUsername())
                && isTokenType(token, TOKEN_TYPE_ACCESS, accessKey)
                && !isTokenExpired(token, accessKey);
    }

    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        String username = extractUsernameFromRefreshToken(token);
        return username.equals(userDetails.getUsername())
                && isTokenType(token, TOKEN_TYPE_REFRESH, refreshKey)
                && !isTokenExpired(token, refreshKey);
    }

    private String buildToken(
            UserDetails userDetails,
            Map<String, Object> extraClaims,
            Instant expiresAt,
            SecretKey key
    ) {
        Instant now = Instant.now();
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(key)
                .compact();
    }

    private boolean isTokenType(String token, String expected, SecretKey key) {
        return expected.equals(extractClaim(token, claims -> claims.get(TOKEN_TYPE_CLAIM, String.class), key));
    }

    private boolean isTokenExpired(String token, SecretKey key) {
        return extractClaim(token, Claims::getExpiration, key).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver, SecretKey key) {
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return resolver.apply(claims);
    }
}
