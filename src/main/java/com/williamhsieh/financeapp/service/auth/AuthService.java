package com.williamhsieh.financeapp.service.auth;

import java.util.Locale;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.williamhsieh.financeapp.constant.auth.AuthEventCodes;
import com.williamhsieh.financeapp.constant.auth.LoginStatusCodes;
import com.williamhsieh.financeapp.dto.auth.LoginRequest;
import com.williamhsieh.financeapp.dto.auth.LoginResponse;
import com.williamhsieh.financeapp.dto.auth.LoginUserResponse;
import com.williamhsieh.financeapp.dto.auth.LogoutRequest;
import com.williamhsieh.financeapp.dto.auth.RefreshTokenRequest;
import com.williamhsieh.financeapp.dto.auth.TokenRefreshResponse;
import com.williamhsieh.financeapp.entity.user.User;
import com.williamhsieh.financeapp.repository.user.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final LoginAuditService loginAuditService;
    private final AuthEventAuditService authEventAuditService;

    public AuthService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        RefreshTokenService refreshTokenService,
        LoginAuditService loginAuditService,
        AuthEventAuditService authEventAuditService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.loginAuditService = loginAuditService;
        this.authEventAuditService = authEventAuditService;
    }

    @Transactional
    public LoginResponse login(
        LoginRequest request,
        LoginRequestMetadata metadata
    ) {
        String normalizedEmail = request.email()
            .trim()
            .toLowerCase(Locale.ROOT);

        User user = userRepository
            .findByEmailIgnoreCaseAndDeletedDateIsNull(
                normalizedEmail
            )
            .orElse(null);

        if (user == null) {
            loginAuditService.recordFailure(
                null,
                normalizedEmail,
                LoginStatusCodes.INVALID_CREDENTIALS,
                metadata
            );

            throw invalidCredentials();
        }

        boolean passwordMatched = passwordEncoder.matches(
            request.password(),
            user.getPassword()
        );

        if (!passwordMatched) {
            loginAuditService.recordFailure(
                user,
                normalizedEmail,
                LoginStatusCodes.INVALID_CREDENTIALS,
                metadata
            );

            throw invalidCredentials();
        }

        if (!user.isEmailVerified()) {
            loginAuditService.recordFailure(
                user,
                normalizedEmail,
                LoginStatusCodes.EMAIL_NOT_VERIFIED,
                metadata
            );

            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "請先完成 Email 驗證"
            );
        }

        if (!user.isSmsVerified()) {
            loginAuditService.recordFailure(
                user,
                normalizedEmail,
                LoginStatusCodes.SMS_NOT_VERIFIED,
                metadata
            );

            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "請先完成手機驗證"
            );
        }

        if (!user.isActive()) {
            loginAuditService.recordFailure(
                user,
                normalizedEmail,
                LoginStatusCodes.ACCOUNT_DISABLED,
                metadata
            );

            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "此帳號目前無法使用"
            );
        }

        String sessionId = UUID.randomUUID().toString();

        RefreshTokenResult refreshToken =
            refreshTokenService.create(user);

        AccessTokenResult accessToken =
            jwtService.generateAccessToken(
                user,
                sessionId
            );

        loginAuditService.recordSuccess(
            user,
            normalizedEmail,
            refreshToken.id(),
            sessionId,
            metadata
        );

        LoginUserResponse loginUser =
            new LoginUserResponse(
                user.getId(),
                user.getUserNumber(),
                user.getName(),
                user.getNickname(),
                user.getEmail()
            );

        return new LoginResponse(
            accessToken.token(),
            refreshToken.token(),
            "Bearer",
            accessToken.expiresIn(),
            refreshToken.expiresIn(),
            loginUser
        );
    }
    
    public TokenRefreshResponse refresh(
        RefreshTokenRequest request,
        LoginRequestMetadata metadata
    ) {
        RefreshTokenRotationResult rotationResult;

        try {
            rotationResult = refreshTokenService.rotate(
                request.refreshToken()
            );
        } catch (RefreshTokenException exception) {
            authEventAuditService.recordFailure(
                exception.getEventCode(),
                exception.getUserId(),
                exception.getRefreshTokenId(),
                exception.getMessage(),
                exception.getEmail(),
                exception.getSessionId(),
                metadata
            );

            throw new ResponseStatusException(
                exception.getStatus(),
                "Refresh Token 無效或已過期",
                exception
            );
        }

        String sessionId = loginAuditService.updateRefreshToken(
            rotationResult.oldRefreshTokenId(),
            rotationResult.newRefreshTokenId()
        );

        AccessTokenResult accessTokenResult =
            jwtService.generateAccessToken(
                rotationResult.user(),
                sessionId
            );

        authEventAuditService.recordSuccess(
            AuthEventCodes.TOKEN_REFRESH_SUCCESS,
            rotationResult.user().getId(),
            rotationResult.oldRefreshTokenId(),
            rotationResult.user().getEmail(),
            sessionId,
            metadata
        );

        return new TokenRefreshResponse(
            accessTokenResult.token(),
            rotationResult.refreshToken(),
            "Bearer",
            accessTokenResult.expiresIn(),
            rotationResult.refreshTokenExpiresIn()
        );
    }
    
    public void logout(
        LogoutRequest request,
        LoginRequestMetadata metadata
    ) {
        RefreshTokenRevocationResult result =
            refreshTokenService.revoke(
                request.refreshToken()
            );

        if (result.newlyRevoked()) {
            String sessionId =
                loginAuditService.recordLogout(
                    result.refreshTokenId()
                );

            authEventAuditService.recordSuccess(
                AuthEventCodes.LOGOUT_SUCCESS,
                result.userId(),
                result.refreshTokenId(),
                result.email(),
                sessionId,
                metadata
            );

            return;
        }

        String failureDetail;

        if (result.found()) {
            failureDetail =
                "登出使用的 Refresh Token 已經被撤銷";
        } else {
            failureDetail =
                "找不到登出使用的 Refresh Token";
        }

        authEventAuditService.recordFailure(
            AuthEventCodes.LOGOUT_INVALID_TOKEN,
            result.userId(),
            result.refreshTokenId(),
            failureDetail,
            result.email(),
            null,
            metadata
        );
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(
            HttpStatus.UNAUTHORIZED,
            "Email 或密碼錯誤"
        );
    }
}