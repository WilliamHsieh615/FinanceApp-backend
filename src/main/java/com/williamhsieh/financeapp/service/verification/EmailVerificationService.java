package com.williamhsieh.financeapp.service.verification;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.williamhsieh.financeapp.dto.verification.VerificationResponse;
import com.williamhsieh.financeapp.entity.user.User;
import com.williamhsieh.financeapp.entity.user.UserEmailVerification;
import com.williamhsieh.financeapp.repository.user.UserEmailVerificationRepository;
import com.williamhsieh.financeapp.repository.user.UserRepository;
import com.williamhsieh.financeapp.service.notification.EmailSender;

@Service
public class EmailVerificationService {

    private static final SecureRandom SECURE_RANDOM =
        new SecureRandom();

    private static final int TOKEN_BYTES = 32;
    private static final int EXPIRATION_MINUTES = 30;
    private static final int RESEND_SECONDS = 60;

    private final UserRepository userRepository;
    private final UserEmailVerificationRepository verificationRepository;
    private final EmailSender emailSender;

    public EmailVerificationService(
        UserRepository userRepository,
        UserEmailVerificationRepository verificationRepository,
        EmailSender emailSender
    ) {
        this.userRepository = userRepository;
        this.verificationRepository = verificationRepository;
        this.emailSender = emailSender;
    }

    @Transactional
    public void send(String email) {
        String normalizedEmail = email
            .trim()
            .toLowerCase(Locale.ROOT);

        User user = userRepository
            .findByEmailIgnoreCaseAndDeletedDateIsNull(normalizedEmail)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "找不到使用者"
            ));

        if (user.isEmailVerified()) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Email 已經完成驗證"
            );
        }

        validateResendInterval(user.getId());

        String rawToken = generateToken();
        String tokenHash = hashToken(rawToken);

        UserEmailVerification verification =
            new UserEmailVerification();

        verification.setUser(user);
        verification.setEmail(normalizedEmail);
        verification.setTokenHash(tokenHash);
        verification.setExpiresDate(
            LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES)
        );

        verificationRepository.save(verification);

        emailSender.sendVerificationEmail(
            normalizedEmail,
            rawToken
        );
    }

    @Transactional
    public VerificationResponse confirm(String rawToken) {
        String tokenHash = hashToken(rawToken);

        UserEmailVerification verification =
            verificationRepository
                .findByTokenHashAndVerifiedDateIsNull(tokenHash)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email 驗證連結無效"
                ));

        if (verification.isExpired()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Email 驗證連結已過期"
            );
        }

        validateLatestToken(verification);

        User user = verification.getUser();
        LocalDateTime now = LocalDateTime.now();

        verification.setVerifiedDate(now);
        user.setEmailVerified(true);
        user.refreshActiveStatus();

        return new VerificationResponse(
            true,
            user.isActive(),
            user.isActive()
                ? "Email 驗證成功，帳號已啟用"
                : "Email 驗證成功，請繼續完成手機驗證"
        );
    }

    private void validateResendInterval(Long userId) {
        verificationRepository
            .findFirstByUser_IdAndVerifiedDateIsNullOrderByCreatedDateDesc(
                userId
            )
            .ifPresent(latest -> {
                LocalDateTime nextSendDate =
                    latest.getCreatedDate()
                        .plusSeconds(RESEND_SECONDS);

                if (LocalDateTime.now().isBefore(nextSendDate)) {
                    throw new ResponseStatusException(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "請稍後再重新寄送驗證信"
                    );
                }
            });
    }

    private void validateLatestToken(
        UserEmailVerification verification
    ) {
        UserEmailVerification latest = verificationRepository
            .findFirstByUser_IdAndVerifiedDateIsNullOrderByCreatedDateDesc(
                verification.getUser().getId()
            )
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Email 驗證連結無效"
            ));

        if (!Objects.equals(latest.getId(), verification.getId())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "此驗證連結已失效，請使用最新的驗證信"
            );
        }
    }

    private String generateToken() {
        byte[] tokenBytes = new byte[TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(tokenBytes);

        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(tokenBytes);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest =
                MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                rawToken.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                "系統不支援 SHA-256",
                exception
            );
        }
    }
}
