package com.williamhsieh.financeapp.service.auth;

public record RefreshTokenResult(
    Long id,
    String token,
    long expiresIn
) {
}
