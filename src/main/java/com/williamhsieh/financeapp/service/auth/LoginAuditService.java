package com.williamhsieh.financeapp.service.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.williamhsieh.financeapp.constant.auth.LoginStatusCodes;
import com.williamhsieh.financeapp.entity.auth.UserLoginLog;
import com.williamhsieh.financeapp.entity.auth.UserLoginStatus;
import com.williamhsieh.financeapp.entity.user.User;
import com.williamhsieh.financeapp.entity.user.UserRefreshToken;
import com.williamhsieh.financeapp.repository.auth.UserLoginLogRepository;
import com.williamhsieh.financeapp.repository.auth.UserLoginStatusRepository;
import com.williamhsieh.financeapp.repository.user.UserRefreshTokenRepository;

@Service
public class LoginAuditService {

    private final UserLoginLogRepository loginLogRepository;
    private final UserLoginStatusRepository loginStatusRepository;
    private final UserRefreshTokenRepository refreshTokenRepository;

    public LoginAuditService(
        UserLoginLogRepository loginLogRepository,
        UserLoginStatusRepository loginStatusRepository,
        UserRefreshTokenRepository refreshTokenRepository
    ) {
        this.loginLogRepository = loginLogRepository;
        this.loginStatusRepository = loginStatusRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public UserLoginLog recordSuccess(
        User user,
        String email,
        Long refreshTokenId,
        String sessionId,
        LoginRequestMetadata metadata
    ) {
        UserLoginStatus status = findStatus(
            LoginStatusCodes.SUCCESS
        );

        UserRefreshToken refreshToken =
            refreshTokenRepository.getReferenceById(
                refreshTokenId
            );

        UserLoginLog loginLog = new UserLoginLog();

        loginLog.setUser(user);
        loginLog.setLoginStatus(status);
        loginLog.setRefreshToken(refreshToken);
        loginLog.setEmail(email);
        loginLog.setSessionId(sessionId);

        applyMetadata(loginLog, metadata);

        return loginLogRepository.save(loginLog);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailure(
        User user,
        String email,
        String statusCode,
        LoginRequestMetadata metadata
    ) {
        UserLoginStatus status = findStatus(statusCode);

        UserLoginLog loginLog = new UserLoginLog();

        loginLog.setUser(user);
        loginLog.setLoginStatus(status);
        loginLog.setRefreshToken(null);
        loginLog.setEmail(email);
        loginLog.setSessionId(null);

        applyMetadata(loginLog, metadata);

        loginLogRepository.save(loginLog);
    }

    @Transactional
    public String updateRefreshToken(
        Long oldRefreshTokenId,
        Long newRefreshTokenId
    ) {
        UserLoginLog loginLog = loginLogRepository
            .findByRefreshToken_IdAndLogoutTimeIsNull(
                oldRefreshTokenId
            )
            .orElseThrow(() -> new IllegalStateException(
                "找不到 Refresh Token 對應的登入紀錄"
            ));

        UserRefreshToken newRefreshToken =
            refreshTokenRepository.getReferenceById(
                newRefreshTokenId
            );

        loginLog.setRefreshToken(newRefreshToken);

        return loginLog.getSessionId();
    }

    @Transactional
    public void recordLogout(Long refreshTokenId) {
        if (refreshTokenId == null) {
            return;
        }

        loginLogRepository
            .findByRefreshToken_IdAndLogoutTimeIsNull(
                refreshTokenId
            )
            .ifPresent(loginLog ->
                loginLog.markAsLoggedOut()
            );
    }

    private UserLoginStatus findStatus(String statusCode) {
        return loginStatusRepository
            .findByCodeAndActiveTrueAndDeletedDateIsNull(
                statusCode
            )
            .orElseThrow(() -> new IllegalStateException(
                "找不到啟用的登入狀態：" + statusCode
            ));
    }

    private void applyMetadata(
        UserLoginLog loginLog,
        LoginRequestMetadata metadata
    ) {
        if (metadata == null) {
            return;
        }

        loginLog.setIpAddress(metadata.ipAddress());
        loginLog.setUserAgent(metadata.userAgent());
        loginLog.setDeviceName(metadata.deviceName());
        loginLog.setOsName(metadata.osName());
        loginLog.setBrowserName(metadata.browserName());
    }
}
