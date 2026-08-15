package com.williamhsieh.financeapp.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.williamhsieh.financeapp.entity.user.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUserNumber(String userNumber);

    boolean existsByPhone(String phone);

    Optional<User> findByEmailIgnoreCaseAndDeletedDateIsNull(String email);

    Optional<User> findByPhoneAndDeletedDateIsNull(String phone);
}
