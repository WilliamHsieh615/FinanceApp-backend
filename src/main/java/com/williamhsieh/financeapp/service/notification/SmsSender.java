package com.williamhsieh.financeapp.service.notification;

public interface SmsSender {

    void sendVerificationCode(
        String phone,
        String verificationCode
    );
}
