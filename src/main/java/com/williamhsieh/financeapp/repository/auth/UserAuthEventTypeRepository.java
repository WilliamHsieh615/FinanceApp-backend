package com.williamhsieh.financeapp.repository.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.williamhsieh.financeapp.entity.auth.UserAuthEventType;

public interface UserAuthEventTypeRepository
    extends JpaRepository<UserAuthEventType, Long> {

    Optional<UserAuthEventType>
        findByCodeAndActiveTrueAndDeletedDateIsNull(String code);
}
