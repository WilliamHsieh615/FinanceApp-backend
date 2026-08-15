package com.williamhsieh.financeapp.service.verification;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.williamhsieh.financeapp.dto.verification.VerificationResponse;
import com.williamhsieh.financeapp.entity.user.User;
import com.williamhsieh.financeapp.entity.user.UserSmsVerification;
import com.williamhsieh.financeapp.repository.user.UserRepository;
import com.williamhsieh.financeapp.repository.user.UserSmsVerificationRepository;
import com.williamhsieh.financeapp.service.notification.SmsSender;

@Service
public class SmsVerificationService {

    private static final SecureRandom SECURE_RANDOM =
        new SecureRandom();

    private static final int EXPIRATION_MINUTES = 5;
    private static final int RESEND_SECONDS = 60;
    private static final int MAX_ATTEMPTS = 5;

    private final UserRepository userRepository;
    private final UserSmsVerificationRepository verificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final SmsSender smsSender;

    public SmsVerificationService(
        UserRepository userRepository,
        UserSmsVerificationRepository verificationRepository,
        PasswordEncoder passwordEncoder,
        SmsSender smsSender
    ) {
        this.userRepository = userRepository;
        this.verificationRepository = verificationRepository;
        this.passwordEncoder = passwordEncoder;
        this.smsSender = smsSender;
    }

    @Transactional
    public void send(String phone) {
        String normalizedPhone = phone.trim();

        User user = userRepository
            .findByPhoneAndDeletedDateIsNull(normalizedPhone)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "找不到使用者"
            ));

        if (user.isSmsVerified()) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "手機號碼已經完成驗證"
            );
        }

        validateResendInterval(user.getId(), normalizedPhone);

        String rawCode = generateCode();

        UserSmsVerification verification =
            new UserSmsVerification();

        verification.setUser(user);
        verification.setPhone(normalizedPhone);
        verification.setCodeHash(
            passwordEncoder.encode(rawCode)
        );
        verification.setExpiresDate(
            LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES)
        );
        verification.setAttemptCount(0);

        verificationRepository.save(verification);

        smsSender.sendVerificationCode(
            normalizedPhone,
            rawCode
        );
    }

    @Transactional(
        noRollbackFor = ResponseStatusException.class
    )
    public VerificationResponse confirm(
        String phone,
        String rawCode
    ) {
        String normalizedPhone = phone.trim();

        UserSmsVerification verification =
            verificationRepository
                .findFirstByPhoneAndVerifiedDateIsNullOrderByCreatedDateDesc(
                    normalizedPhone
                )
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "找不到有效的手機驗證碼"
                ));

        if (verification.isExpired()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "手機驗證碼已過期"
            );
        }

        if (verification.hasExceededMaxAttempts(MAX_ATTEMPTS)) {
            throw new ResponseStatusException(
                HttpStatus.TOO_MANY_REQUESTS,
                "驗證失敗次數過多，請重新取得驗證碼"
            );
        }

        boolean matched = passwordEncoder.matches(
            rawCode,
            verification.getCodeHash()
        );

        if (!matched) {
            verification.increaseAttemptCount();

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "手機驗證碼不正確"
            );
        }

        User user = verification.getUser();
        LocalDateTime now = LocalDateTime.now();

        verification.setVerifiedDate(now);
        user.setSmsVerified(true);
        user.refreshActiveStatus();

        return new VerificationResponse(
            true,
            user.isActive(),
            user.isActive()
                ? "手機驗證成功，帳號已啟用"
                : "手機驗證成功，請繼續完成 Email 驗證"
        );
    }

    private void validateResendInterval(
        Long userId,
        String phone
    ) {
        verificationRepository
            .findFirstByUser_IdAndPhoneAndVerifiedDateIsNullOrderByCreatedDateDesc(
                userId,
                phone
            )
            .ifPresent(latest -> {
                LocalDateTime nextSendDate =
                    latest.getCreatedDate()
                        .plusSeconds(RESEND_SECONDS);

                if (LocalDateTime.now().isBefore(nextSendDate)) {
                    throw new ResponseStatusException(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "請稍後再重新發送驗證碼"
                    );
                }
            });
    }

    private String generateCode() {
        int code = SECURE_RANDOM.nextInt(10_000);
        return String.format("%04d", code);
    }
}
