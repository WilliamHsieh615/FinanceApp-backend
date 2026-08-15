package com.williamhsieh.financeapp.dto.registration;

import java.util.List;

public record CountryOptionResponse(
    Long id,
    String iso2,
    String iso3,
    String isoNumeric,
    String phoneCode,
    String name,
    String nativeName,
    Long defaultTimezoneId,
    Long defaultLanguageId,
    List<TimezoneOptionResponse> timezones
) {
}
