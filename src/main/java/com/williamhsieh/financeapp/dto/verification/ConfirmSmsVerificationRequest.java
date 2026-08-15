package com.williamhsieh.financeapp.dto.verification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ConfirmSmsVerificationRequest(

    @NotBlank(message = "手機號碼不得為空")
    String phone,

    @NotBlank(message = "驗證碼不得為空")
    @Pattern(
        regexp = "^[0-9]{4}$",
        message = "驗證碼必須是四位數字"
    )
    String code

) {
}
