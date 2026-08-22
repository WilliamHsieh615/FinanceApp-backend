package com.williamhsieh.financeapp.service.auth;

import com.williamhsieh.financeapp.entity.user.User;

public record RefreshTokenRotationResult(
    User user,
    Long oldRefreshTokenId,
    Long newRefreshTokenId,
    String refreshToken,
    long refreshTokenExpiresIn
) {
}
