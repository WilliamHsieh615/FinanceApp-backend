package com.williamhsieh.financeapp.service.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevelopmentSmsSender implements SmsSender {

    private static final Logger log =
        LoggerFactory.getLogger(DevelopmentSmsSender.class);

    @Override
    public void sendVerificationCode(
        String phone,
        String verificationCode
    ) {
        log.info(
            "Development SMS verification: phone={}, code={}",
            phone,
            verificationCode
        );
    }
}
