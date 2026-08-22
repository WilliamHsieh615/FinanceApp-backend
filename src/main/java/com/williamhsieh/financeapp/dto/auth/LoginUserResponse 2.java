package com.williamhsieh.financeapp.dto.auth;

public record LoginUserResponse(
    Long id,
    String userNumber,
    String name,
    String nickname,
    String email
) {
}
