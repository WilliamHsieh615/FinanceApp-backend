package com.williamhsieh.financeapp.service.registration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.williamhsieh.financeapp.dto.registration.CountryOptionResponse;
import com.williamhsieh.financeapp.dto.registration.LanguageOptionResponse;
import com.williamhsieh.financeapp.dto.registration.RegistrationOptionsResponse;
import com.williamhsieh.financeapp.dto.registration.TimezoneOptionResponse;
import com.williamhsieh.financeapp.entity.region.CountryLanguage;
import com.williamhsieh.financeapp.entity.region.CountryTimezone;
import com.williamhsieh.financeapp.entity.region.Language;
import com.williamhsieh.financeapp.repository.region.CountryLanguageRepository;
import com.williamhsieh.financeapp.repository.region.CountryRepository;
import com.williamhsieh.financeapp.repository.region.CountryTimezoneRepository;
import com.williamhsieh.financeapp.repository.region.LanguageRepository;

@Service
@Transactional(readOnly = true)
public class RegistrationOptionsService {

    private final CountryRepository countryRepository;
    private final CountryTimezoneRepository countryTimezoneRepository;
    private final CountryLanguageRepository countryLanguageRepository;
    private final LanguageRepository languageRepository;

    public RegistrationOptionsService(
        CountryRepository countryRepository,
        CountryTimezoneRepository countryTimezoneRepository,
        CountryLanguageRepository countryLanguageRepository,
        LanguageRepository languageRepository
    ) {
        this.countryRepository = countryRepository;
        this.countryTimezoneRepository = countryTimezoneRepository;
        this.countryLanguageRepository = countryLanguageRepository;
        this.languageRepository = languageRepository;
    }

    public RegistrationOptionsResponse getOptions() {
        Map<Long, List<CountryTimezone>> timezonesByCountryId =
            countryTimezoneRepository.findAllAvailableWithCountryAndTimezone()
                .stream()
                .collect(Collectors.groupingBy(item -> item.getCountry().getId()));

        Map<Long, CountryLanguage> defaultLanguageByCountryId =
            countryLanguageRepository.findAllAvailableDefaults()
                .stream()
                .collect(Collectors.toMap(
                    item -> item.getCountry().getId(),
                    Function.identity(),
                    (first, ignored) -> first
                ));

        List<CountryOptionResponse> countries = countryRepository
            .findAllByDeletedDateIsNullOrderByNameAsc()
            .stream()
            .map(country -> {
                List<CountryTimezone> countryTimezones = timezonesByCountryId
                    .getOrDefault(country.getId(), List.of());

                List<TimezoneOptionResponse> timezoneOptions = countryTimezones
                    .stream()
                    .map(this::toTimezoneOption)
                    .toList();

                Long defaultTimezoneId = countryTimezones.stream()
                    .filter(item -> Boolean.TRUE.equals(item.getDefaultTimezone()))
                    .map(item -> item.getTimezone().getId())
                    .findFirst()
                    .orElse(null);

                CountryLanguage defaultLanguage = defaultLanguageByCountryId.get(country.getId());
                Long defaultLanguageId = defaultLanguage == null
                    ? null
                    : defaultLanguage.getLanguage().getId();

                return new CountryOptionResponse(
                    country.getId(),
                    country.getIso2(),
                    country.getIso3(),
                    country.getIsoNumeric(),
                    country.getPhoneCode(),
                    country.getName(),
                    country.getNativeName(),
                    defaultTimezoneId,
                    defaultLanguageId,
                    timezoneOptions
                );
            })
            .toList();

        List<LanguageOptionResponse> languages = languageRepository
            .findAllByActiveTrueAndDeletedDateIsNullOrderByNameAsc()
            .stream()
            .map(this::toLanguageOption)
            .toList();

        return new RegistrationOptionsResponse(countries, languages);
    }

    private TimezoneOptionResponse toTimezoneOption(CountryTimezone countryTimezone) {
        var timezone = countryTimezone.getTimezone();
        return new TimezoneOptionResponse(
            timezone.getId(),
            timezone.getCode(),
            timezone.getIanaName(),
            timezone.getName(),
            timezone.getUtcOffset(),
            timezone.getHasDst(),
            Boolean.TRUE.equals(countryTimezone.getDefaultTimezone())
        );
    }

    private LanguageOptionResponse toLanguageOption(Language language) {
        return new LanguageOptionResponse(
            language.getId(),
            language.getCode(),
            language.getName()
        );
    }
}
