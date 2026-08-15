package com.williamhsieh.financeapp.service.registration;

import java.security.SecureRandom;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.williamhsieh.financeapp.dto.registration.RegisterUserRequest;
import com.williamhsieh.financeapp.dto.registration.RegisterUserResponse;
import com.williamhsieh.financeapp.entity.region.Country;
import com.williamhsieh.financeapp.entity.region.Language;
import com.williamhsieh.financeapp.entity.region.Timezone;
import com.williamhsieh.financeapp.entity.user.User;
import com.williamhsieh.financeapp.repository.region.CountryRepository;
import com.williamhsieh.financeapp.repository.region.CountryTimezoneRepository;
import com.williamhsieh.financeapp.repository.region.LanguageRepository;
import com.williamhsieh.financeapp.repository.region.TimezoneRepository;
import com.williamhsieh.financeapp.repository.user.UserRepository;

@Service
public class RegistrationService {

    private static final String USER_NUMBER_CHARACTERS =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final int USER_NUMBER_LENGTH = 10;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final TimezoneRepository timezoneRepository;
    private final LanguageRepository languageRepository;
    private final CountryTimezoneRepository countryTimezoneRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(
        UserRepository userRepository,
        CountryRepository countryRepository,
        TimezoneRepository timezoneRepository,
        LanguageRepository languageRepository,
        CountryTimezoneRepository countryTimezoneRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.countryRepository = countryRepository;
        this.timezoneRepository = timezoneRepository;
        this.languageRepository = languageRepository;
        this.countryTimezoneRepository = countryTimezoneRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RegisterUserResponse register(RegisterUserRequest request) {
        String normalizedEmail = request.email()
            .trim()
            .toLowerCase(Locale.ROOT);

        String normalizedPhone = request.phone().trim();

        validateUniqueUserData(normalizedEmail, normalizedPhone);

        Country country = findCountry(request.countryId());
        Timezone timezone = findTimezone(request.timezoneId());
        Language language = findLanguage(request.languageId());

        validateCountryTimezone(
            request.countryId(),
            request.timezoneId()
        );

        User user = new User();

        user.setCountry(country);
        user.setTimezone(timezone);
        user.setLanguage(language);
        user.setUserNumber(generateUniqueUserNumber());
        user.setName(request.name().trim());
        user.setNickname(request.nickname().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setBirthday(request.birthday());
        user.setPhone(normalizedPhone);

        // 新註冊使用者尚未完成驗證
        user.setEmailVerified(false);
        user.setSmsVerified(false);
        user.setActive(false);

        User savedUser = userRepository.save(user);

        return new RegisterUserResponse(
            savedUser.getId(),
            savedUser.getUserNumber(),
            savedUser.getEmail(),
            savedUser.isEmailVerified(),
            savedUser.isSmsVerified(),
            savedUser.isActive(),
            "註冊資料建立成功，請完成電子郵件與手機驗證"
        );
    }

    private void validateUniqueUserData(
        String email,
        String phone
    ) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "此 Email 已被註冊"
            );
        }

        if (userRepository.existsByPhone(phone)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "此手機號碼已被註冊"
            );
        }
    }

    private Country findCountry(Long countryId) {
        if (countryId == null) {
            return null;
        }

        return countryRepository
            .findByIdAndDeletedDateIsNull(countryId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "選擇的國家不存在"
            ));
    }

    private Timezone findTimezone(Long timezoneId) {
        if (timezoneId == null) {
            return null;
        }

        return timezoneRepository
            .findByIdAndDeletedDateIsNull(timezoneId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "選擇的時區不存在"
            ));
    }

    private Language findLanguage(Long languageId) {
        return languageRepository
            .findByIdAndActiveTrueAndDeletedDateIsNull(languageId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "選擇的語言不存在或已停用"
            ));
    }

    private void validateCountryTimezone(
        Long countryId,
        Long timezoneId
    ) {
        if (countryId == null && timezoneId == null) {
            return;
        }

        if (countryId == null || timezoneId == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "國家與時區必須同時選擇"
            );
        }

        boolean validMapping =
            countryTimezoneRepository
                .existsByCountry_IdAndTimezone_IdAndDeletedDateIsNull(
                    countryId,
                    timezoneId
                );

        if (!validMapping) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "選擇的時區不屬於該國家"
            );
        }
    }

    private String generateUniqueUserNumber() {
        String userNumber;

        do {
            userNumber = generateRandomUserNumber();
        } while (userRepository.existsByUserNumber(userNumber));

        return userNumber;
    }

    private String generateRandomUserNumber() {
        StringBuilder result =
            new StringBuilder(USER_NUMBER_LENGTH);

        for (int i = 0; i < USER_NUMBER_LENGTH; i++) {
            int index = SECURE_RANDOM.nextInt(
                USER_NUMBER_CHARACTERS.length()
            );

            result.append(
                USER_NUMBER_CHARACTERS.charAt(index)
            );
        }

        return result.toString();
    }
}
