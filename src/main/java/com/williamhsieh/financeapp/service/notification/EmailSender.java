package com.williamhsieh.financeapp.service.notification;

public interface EmailSender {

    void sendVerificationEmail(
        String email,
        String verificationToken
    );
}
