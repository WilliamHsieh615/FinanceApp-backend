package com.williamhsieh.financeapp.repository.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.williamhsieh.financeapp.entity.auth.UserLoginStatus;

public interface UserLoginStatusRepository
    extends JpaRepository<UserLoginStatus, Long> {

    Optional<UserLoginStatus>
        findByCodeAndActiveTrueAndDeletedDateIsNull(
            String code
        );
}