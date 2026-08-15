package com.williamhsieh.financeapp.dto.verification;

import jakarta.validation.constraints.NotBlank;

public record ConfirmEmailVerificationRequest(

    @NotBlank(message = "Email 驗證 token 不得為空")
    String token

) {
}
