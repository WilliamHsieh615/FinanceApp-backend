package com.williamhsieh.financeapp.repository.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.williamhsieh.financeapp.entity.auth.UserLoginLog;

public interface UserLoginLogRepository
    extends JpaRepository<UserLoginLog, Long> {

    Optional<UserLoginLog> findBySessionId(
        String sessionId
    );

    Optional<UserLoginLog>
        findByRefreshToken_IdAndLogoutTimeIsNull(
            Long refreshTokenId
        );

    List<UserLoginLog>
        findAllByUser_IdOrderByLoginTimeDesc(
            Long userId
        );
}