package com.williamhsieh.financeapp.service.auth;

public record RefreshTokenRevocationResult(
    Long refreshTokenId,
    Long userId,
    String email,
    boolean found,
    boolean newlyRevoked
) {
}
