package com.williamhsieh.financeapp.service.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.williamhsieh.financeapp.config.JwtProperties;
import com.williamhsieh.financeapp.entity.user.User;
import com.williamhsieh.financeapp.entity.user.UserRefreshToken;
import com.williamhsieh.financeapp.repository.user.UserRefreshTokenRepository;
import com.williamhsieh.financeapp.constant.auth.AuthEventCodes;

@Service
public class RefreshTokenService {

    private static final SecureRandom SECURE_RANDOM =
        new SecureRandom();

    private static final int TOKEN_BYTES = 64;

    private final UserRefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    public RefreshTokenService(
        UserRefreshTokenRepository refreshTokenRepository,
        JwtProperties jwtProperties
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public RefreshTokenResult create(User user) {
        String rawToken = generateToken();
        String tokenHash = hashToken(rawToken);

        long expirationSeconds =
            jwtProperties.refreshTokenExpirationSeconds();

        UserRefreshToken refreshToken =
            new UserRefreshToken();

        refreshToken.setUser(user);
        refreshToken.setTokenHash(tokenHash);
        refreshToken.setExpiresDate(
            LocalDateTime.now().plusSeconds(
                expirationSeconds
            )
        );

        UserRefreshToken savedToken = refreshTokenRepository.save(refreshToken);

        return new RefreshTokenResult(
            savedToken.getId(),
            rawToken,
            expirationSeconds
        );
    }

    @Transactional(
        noRollbackFor = RefreshTokenException.class
    )
    public RefreshTokenRotationResult rotate(
        String rawToken
    ) {
        String tokenHash = hashToken(rawToken);

        UserRefreshToken currentToken =
            refreshTokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(() ->
                    new RefreshTokenException(
                        HttpStatus.UNAUTHORIZED,
                        AuthEventCodes.REFRESH_TOKEN_INVALID,
                        "找不到對應的 Refresh Token",
                        null,
                        null,
                        null,
                        null
                    )
                );

        User user = currentToken.getUser();

        if (currentToken.isRevoked()) {
            boolean previouslyUsed =
                currentToken.getLastUsedDate() != null;

            /*
             * 如果這個 Token 曾經成功用於輪替，
             * 現在又再次出現，代表可能發生重放攻擊。
             */
            if (previouslyUsed) {
                revokeAllUserTokens(user.getId());

                throw new RefreshTokenException(
                    HttpStatus.UNAUTHORIZED,
                    AuthEventCodes.REFRESH_TOKEN_REUSE_DETECTED,
                    "偵測到已使用過的 Refresh Token 再次被使用",
                    user.getId(),
                    currentToken.getId(),
                    user.getEmail(),
                    null
                );
            }

            /*
             * Token 可能是在 logout 時被撤銷。
             */
            throw new RefreshTokenException(
                HttpStatus.UNAUTHORIZED,
                AuthEventCodes.REFRESH_TOKEN_REVOKED,
                "Refresh Token 已被撤銷",
                user.getId(),
                currentToken.getId(),
                user.getEmail(),
                null
            );
        }

        if (currentToken.isExpired()) {
            currentToken.revoke();

            throw new RefreshTokenException(
                HttpStatus.UNAUTHORIZED,
                AuthEventCodes.REFRESH_TOKEN_EXPIRED,
                "Refresh Token 已過期",
                user.getId(),
                currentToken.getId(),
                user.getEmail(),
                null
            );
        }

        if (!user.isActive() || user.getDeletedDate() != null) {
            revokeAllUserTokens(user.getId());

            throw new RefreshTokenException(
                HttpStatus.FORBIDDEN,
                AuthEventCodes.REFRESH_TOKEN_REVOKED,
                "使用者帳號已停用，所有 Refresh Token 已撤銷",
                user.getId(),
                currentToken.getId(),
                user.getEmail(),
                null
            );
        }

        currentToken.markAsUsed();
        currentToken.revoke();

        RefreshTokenResult newRefreshToken =
            createTokenWithoutNewTransaction(user);

        return new RefreshTokenRotationResult(
            user,
            currentToken.getId(),
            newRefreshToken.id(),
            newRefreshToken.token(),
            newRefreshToken.expiresIn()
        );
    }

    @Transactional
    public RefreshTokenRevocationResult revoke(
        String rawToken
    ) {
        String tokenHash = hashToken(rawToken);

        var tokenOptional =
            refreshTokenRepository.findByTokenHash(
                tokenHash
            );

        if (tokenOptional.isEmpty()) {
            return new RefreshTokenRevocationResult(
                null,
                null,
                null,
                false,
                false
            );
        }

        UserRefreshToken token = tokenOptional.get();
        User user = token.getUser();

        if (token.isRevoked()) {
            return new RefreshTokenRevocationResult(
                token.getId(),
                user.getId(),
                user.getEmail(),
                true,
                false
            );
        }

        token.revoke();

        return new RefreshTokenRevocationResult(
            token.getId(),
            user.getId(),
            user.getEmail(),
            true,
            true
        );
    }

    private RefreshTokenResult createTokenWithoutNewTransaction(
        User user
    ) {
        String rawToken = generateToken();
        String tokenHash = hashToken(rawToken);

        long expirationSeconds =
            jwtProperties.refreshTokenExpirationSeconds();

        UserRefreshToken refreshToken =
            new UserRefreshToken();

        refreshToken.setUser(user);
        refreshToken.setTokenHash(tokenHash);
        refreshToken.setExpiresDate(
            LocalDateTime.now().plusSeconds(
                expirationSeconds
            )
        );

        UserRefreshToken savedToken = refreshTokenRepository.save(refreshToken);

        return new RefreshTokenResult(
            savedToken.getId(),
            rawToken,
            expirationSeconds
        );
    }

    private void revokeAllUserTokens(Long userId) {
        var activeTokens = refreshTokenRepository
            .findAllByUser_IdAndRevokedDateIsNull(userId);

        for (UserRefreshToken token : activeTokens) {
            token.revoke();
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
