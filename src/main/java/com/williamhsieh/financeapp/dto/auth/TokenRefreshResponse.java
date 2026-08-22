package com.williamhsieh.financeapp.dto.auth;

public record TokenRefreshResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn,
    long refreshTokenExpiresIn
) {
}
