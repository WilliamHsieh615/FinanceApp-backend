package com.williamhsieh.financeapp.service.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.williamhsieh.financeapp.entity.auth.UserAuthEventLog;
import com.williamhsieh.financeapp.entity.auth.UserAuthEventType;
import com.williamhsieh.financeapp.entity.user.UserRefreshToken;
import com.williamhsieh.financeapp.entity.user.User;
import com.williamhsieh.financeapp.repository.auth.UserAuthEventLogRepository;
import com.williamhsieh.financeapp.repository.auth.UserAuthEventTypeRepository;
import com.williamhsieh.financeapp.repository.user.UserRefreshTokenRepository;
import com.williamhsieh.financeapp.repository.user.UserRepository;

@Service
public class AuthEventAuditService {

    private final UserAuthEventLogRepository authEventLogRepository;
    private final UserAuthEventTypeRepository authEventTypeRepository;
    private final UserRefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public AuthEventAuditService(
        UserAuthEventLogRepository authEventLogRepository,
        UserAuthEventTypeRepository authEventTypeRepository,
        UserRefreshTokenRepository refreshTokenRepository,
        UserRepository userRepository
    ) {
        this.authEventLogRepository = authEventLogRepository;
        this.authEventTypeRepository = authEventTypeRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordSuccess(
        String eventCode,
        Long userId,
        Long refreshTokenId,
        String email,
        String sessionId,
        LoginRequestMetadata metadata
    ) {
        saveEvent(
            eventCode,
            userId,
            refreshTokenId,
            true,
            null,
            email,
            sessionId,
            metadata
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailure(
        String eventCode,
        Long userId,
        Long refreshTokenId,
        String failureDetail,
        String email,
        String sessionId,
        LoginRequestMetadata metadata
    ) {
        saveEvent(
            eventCode,
            userId,
            refreshTokenId,
            false,
            failureDetail,
            email,
            sessionId,
            metadata
        );
    }

    private void saveEvent(
        String eventCode,
        Long userId,
        Long refreshTokenId,
        boolean success,
        String failureDetail,
        String email,
        String sessionId,
        LoginRequestMetadata metadata
    ) {
        UserAuthEventType eventType = authEventTypeRepository
            .findByCodeAndActiveTrueAndDeletedDateIsNull(eventCode)
            .orElseThrow(() -> new IllegalStateException(
                "找不到有效的授權事件類型：" + eventCode
            ));

        User user = findUser(userId);
        UserRefreshToken refreshToken =
            findRefreshToken(refreshTokenId);

        String ipAddress =
            metadata == null ? null : metadata.ipAddress();

        String userAgent =
            metadata == null ? null : metadata.userAgent();

        UserAuthEventLog eventLog = new UserAuthEventLog(
            user,
            eventType,
            refreshToken,
            success,
            limitLength(failureDetail, 255),
            limitLength(email, 255),
            limitLength(sessionId, 36),
            limitLength(ipAddress, 45),
            limitLength(userAgent, 500)
        );

        authEventLogRepository.save(eventLog);
    }

    private User findUser(Long userId) {
        if (userId == null) {
            return null;
        }

        return userRepository.findById(userId).orElse(null);
    }

    private UserRefreshToken findRefreshToken(
        Long refreshTokenId
    ) {
        if (refreshTokenId == null) {
            return null;
        }

        return refreshTokenRepository
            .findById(refreshTokenId)
            .orElse(null);
    }

    private String limitLength(String value, int maximumLength) {
        if (value == null) {
            return null;
        }

        if (value.length() <= maximumLength) {
            return value;
        }

        return value.substring(0, maximumLength);
    }
}
