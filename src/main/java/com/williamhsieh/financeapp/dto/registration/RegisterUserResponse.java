package com.williamhsieh.financeapp.dto.registration;

public record RegisterUserResponse(
    Long id,
    String userNumber,
    String email,
    boolean emailVerified,
    boolean smsVerified,
    boolean active,
    String message
) {
}
