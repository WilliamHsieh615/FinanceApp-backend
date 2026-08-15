package com.williamhsieh.financeapp.service.registration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.williamhsieh.financeapp.dto.registration.RegistrationOptionsResponse;
import com.williamhsieh.financeapp.repository.region.CountryLanguageRepository;
import com.williamhsieh.financeapp.repository.region.CountryRepository;
import com.williamhsieh.financeapp.repository.region.CountryTimezoneRepository;
import com.williamhsieh.financeapp.repository.region.LanguageRepository;

@ExtendWith(MockitoExtension.class)
class RegistrationOptionsServiceTests {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CountryTimezoneRepository countryTimezoneRepository;

    @Mock
    private CountryLanguageRepository countryLanguageRepository;

    @Mock
    private LanguageRepository languageRepository;

    private RegistrationOptionsService service;

    @BeforeEach
    void setUp() {
        service = new RegistrationOptionsService(
            countryRepository,
            countryTimezoneRepository,
            countryLanguageRepository,
            languageRepository
        );
    }

    @Test
    void returnsEmptyListsWhenReferenceDataIsEmpty() {
        when(countryTimezoneRepository.findAllAvailableWithCountryAndTimezone())
            .thenReturn(List.of());
        when(countryLanguageRepository.findAllAvailableDefaults())
            .thenReturn(List.of());
        when(countryRepository.findAllByDeletedDateIsNullOrderByNameAsc())
            .thenReturn(List.of());
        when(languageRepository.findAllByActiveTrueAndDeletedDateIsNullOrderByNameAsc())
            .thenReturn(List.of());

        RegistrationOptionsResponse response = service.getOptions();

        assertThat(response.countries()).isEmpty();
        assertThat(response.languages()).isEmpty();
    }
}
