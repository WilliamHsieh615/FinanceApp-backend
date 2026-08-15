package com.williamhsieh.financeapp.dto.verification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SendSmsVerificationRequest(

    @NotBlank(message = "手機號碼不得為空")
    @Pattern(
        regexp = "^\\+?[0-9]{8,15}$",
        message = "手機號碼格式不正確"
    )
    String phone

) {
}
