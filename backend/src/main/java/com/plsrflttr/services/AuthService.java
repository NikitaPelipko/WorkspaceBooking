package com.plsrflttr.services;

import com.plsrflttr.models.Role;
import com.plsrflttr.models.User;
import com.plsrflttr.repositories.UserRepository;
import com.plsrflttr.security.JwtService;
import com.plsrflttr.dto.AuthResponse;
import com.plsrflttr.dto.AuthRequest;
import com.plsrflttr.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public AuthResponse register(RegisterRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        });

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.getRoles().add(Role.USER);
        User saved = userRepository.save(user);

        var userDetails = org.springframework.security.core.userdetails.User.withUsername(saved.getEmail())
                .password(saved.getPasswordHash())
                .authorities(saved.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).toList())
                .build();
        String accessToken = jwtService.generateAccessToken(userDetails, saved.getId());
        String refreshToken = jwtService.generateRefreshToken(userDetails, saved.getId());
        return buildAuthResponse(accessToken, refreshToken, saved);
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        var userDetails = org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(user.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).toList())
                .build();
        String accessToken = jwtService.generateAccessToken(userDetails, user.getId());
        String refreshToken = jwtService.generateRefreshToken(userDetails, user.getId());
        return buildAuthResponse(accessToken, refreshToken, user);
    }

    public AuthResponse refresh(String refreshToken) {
        String username = jwtService.extractUsernameFromRefreshToken(refreshToken);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        var userDetails = org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(user.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).toList())
                .build();
        if (!jwtService.isRefreshTokenValid(refreshToken, userDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        String accessToken = jwtService.generateAccessToken(userDetails, user.getId());
        String newRefreshToken = jwtService.generateRefreshToken(userDetails, user.getId());
        return buildAuthResponse(accessToken, newRefreshToken, user);
    }

    private AuthResponse buildAuthResponse(String accessToken, String refreshToken, User user) {
        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setRoles(user.getRoles());
        return response;
    }
}
