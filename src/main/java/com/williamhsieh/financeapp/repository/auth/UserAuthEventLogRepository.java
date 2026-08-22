package com.williamhsieh.financeapp.repository.auth;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.williamhsieh.financeapp.entity.auth.UserAuthEventLog;

public interface UserAuthEventLogRepository
    extends JpaRepository<UserAuthEventLog, Long> {

    List<UserAuthEventLog>
        findAllByUser_IdOrderByEventTimeDesc(Long userId);

    List<UserAuthEventLog>
        findAllBySessionIdOrderByEventTimeDesc(String sessionId);
}
