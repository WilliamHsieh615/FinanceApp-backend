package com.williamhsieh.financeapp.service.auth;

public record AccessTokenResult(
    String token,
    long expiresIn
) {
}
