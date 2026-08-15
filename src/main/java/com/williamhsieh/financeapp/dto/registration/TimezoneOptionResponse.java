package com.williamhsieh.financeapp.dto.registration;

public record TimezoneOptionResponse(
    Long id,
    String code,
    String ianaName,
    String name,
    Integer utcOffset,
    Boolean hasDst,
    Boolean defaultTimezone
) {
}
