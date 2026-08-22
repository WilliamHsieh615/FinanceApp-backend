package com.williamhsieh.financeapp.controller.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.williamhsieh.financeapp.dto.auth.LoginRequest;
import com.williamhsieh.financeapp.dto.auth.LoginResponse;
import com.williamhsieh.financeapp.dto.auth.LogoutRequest;
import com.williamhsieh.financeapp.dto.auth.RefreshTokenRequest;
import com.williamhsieh.financeapp.dto.auth.TokenRefreshResponse;
import com.williamhsieh.financeapp.service.auth.AuthService;
import com.williamhsieh.financeapp.service.auth.LoginRequestMetadata;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
        @Valid @RequestBody LoginRequest request,
        HttpServletRequest httpRequest
    ) {
        LoginRequestMetadata metadata =
            LoginRequestMetadata.from(httpRequest);

        return ResponseEntity.ok(
            authService.login(request, metadata)
        );
    }

    @PostMapping("/refresh")
    public TokenRefreshResponse refresh(
        @RequestBody RefreshTokenRequest request,
        HttpServletRequest httpRequest
    ) {
        LoginRequestMetadata metadata =
            LoginRequestMetadata.from(httpRequest);

        return authService.refresh(request, metadata);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        @Valid @RequestBody LogoutRequest request
    ) {
        authService.logout(request);

        return ResponseEntity.noContent().build();
    }
}