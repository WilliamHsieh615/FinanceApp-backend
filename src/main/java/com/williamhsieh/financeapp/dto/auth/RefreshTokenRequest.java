package com.williamhsieh.financeapp.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(

    @NotBlank(message = "Refresh Token 不得為空")
    String refreshToken

) {
}
