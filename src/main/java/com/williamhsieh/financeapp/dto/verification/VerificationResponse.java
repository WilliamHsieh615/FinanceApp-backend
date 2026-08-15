package com.williamhsieh.financeapp.dto.verification;

public record VerificationResponse(
    boolean verified,
    boolean active,
    String message
) {
}
