package com.williamhsieh.financeapp.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(

    @NotBlank(message = "Email 不得為空")
    @Email(message = "Email 格式不正確")
    @Size(max = 255, message = "Email 長度不得超過 255 個字元")
    String email,

    @NotBlank(message = "密碼不得為空")
    @Size(
        max = 72,
        message = "密碼長度不得超過 72 個字元"
    )
    String password

) {
}
