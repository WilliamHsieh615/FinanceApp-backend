package com.williamhsieh.financeapp.dto.registration;

import java.util.List;

public record RegistrationOptionsResponse(
    List<CountryOptionResponse> countries,
    List<LanguageOptionResponse> languages
) {
}
