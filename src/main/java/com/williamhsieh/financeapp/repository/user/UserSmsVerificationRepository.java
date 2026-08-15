package com.williamhsieh.financeapp.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.williamhsieh.financeapp.entity.user.UserSmsVerification;

public interface UserSmsVerificationRepository
    extends JpaRepository<UserSmsVerification, Long> {

    Optional<UserSmsVerification>
        findFirstByUser_IdAndPhoneAndVerifiedDateIsNullOrderByCreatedDateDesc(
            Long userId,
            String phone
        );

    Optional<UserSmsVerification>
        findFirstByPhoneAndVerifiedDateIsNullOrderByCreatedDateDesc(
            String phone
        );
}
