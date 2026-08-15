package com.williamhsieh.financeapp.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.williamhsieh.financeapp.entity.user.UserEmailVerification;

public interface UserEmailVerificationRepository
    extends JpaRepository<UserEmailVerification, Long> {

    Optional<UserEmailVerification>
        findByTokenHashAndVerifiedDateIsNull(String tokenHash);

    Optional<UserEmailVerification>
        findFirstByUser_IdAndVerifiedDateIsNullOrderByCreatedDateDesc(
            Long userId
        );
}