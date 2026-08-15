package com.williamhsieh.financeapp.dto.verification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendEmailVerificationRequest(

    @NotBlank(message = "Email 不得為空")
    @Email(message = "Email 格式不正確")
    String email

) {
}
