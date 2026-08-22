package com.williamhsieh.financeapp.service.auth;

public record RefreshTokenRevocationResult(
    Long refreshTokenId,
    boolean found,
    boolean newlyRevoked
) {
}
