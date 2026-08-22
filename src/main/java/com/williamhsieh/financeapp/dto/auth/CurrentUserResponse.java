package com.williamhsieh.financeapp.dto.auth;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CurrentUserResponse(
    Long id,
    String userNumber,
    String name,
    String nickname,
    String email,
    LocalDate birthday,
    String phone,
    boolean emailVerified,
    boolean smsVerified,
    boolean active,
    CountryInfo country,
    TimezoneInfo timezone,
    LanguageInfo language,
    LocalDateTime createdDate,
    LocalDateTime updatedDate
) {

    public record CountryInfo(
        Long id,
        String iso2,
        String iso3,
        String name,
        String nativeName,
        String phoneCode
    ) {
    }

    public record TimezoneInfo(
        Long id,
        String code,
        String ianaName,
        String name,
        Integer utcOffset,
        Boolean hasDst
    ) {
    }

    public record LanguageInfo(
        Long id,
        String code,
        String name
    ) {
    }
}
