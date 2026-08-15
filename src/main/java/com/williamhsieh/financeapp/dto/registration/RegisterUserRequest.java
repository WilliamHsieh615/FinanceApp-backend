package com.williamhsieh.financeapp.dto.registration;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(

    Long countryId,

    Long timezoneId,

    @NotNull(message = "語言不得為空")
    Long languageId,

    @NotBlank(message = "姓名不得為空")
    @Size(max = 255, message = "姓名不得超過 255 個字元")
    String name,

    @NotBlank(message = "暱稱不得為空")
    @Size(max = 100, message = "暱稱不得超過 100 個字元")
    String nickname,

    @NotBlank(message = "Email 不得為空")
    @Email(message = "Email 格式不正確")
    @Size(max = 255, message = "Email 不得超過 255 個字元")
    String email,

    @NotBlank(message = "密碼不得為空")
    @Size(
        min = 8,
        max = 72,
        message = "密碼長度必須介於 8 到 72 個字元"
    )
    String password,

    @NotNull(message = "生日不得為空")
    @Past(message = "生日必須早於今天")
    LocalDate birthday,

    @NotBlank(message = "手機號碼不得為空")
    @Pattern(
        regexp = "^\\+?[0-9]{8,15}$",
        message = "手機號碼格式不正確"
    )
    String phone

) {
}
