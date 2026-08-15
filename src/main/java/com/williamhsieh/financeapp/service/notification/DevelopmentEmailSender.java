package com.williamhsieh.financeapp.service.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Profile("dev")
public class DevelopmentEmailSender implements EmailSender {

    private static final Logger log =
        LoggerFactory.getLogger(DevelopmentEmailSender.class);

    @Override
    public void sendVerificationEmail(
        String email,
        String verificationToken
    ) {
        String verificationUrl = UriComponentsBuilder
            .fromUriString("http://localhost:3000/verify-email")
            .queryParam("token", verificationToken)
            .build()
            .toUriString();

        log.info(
            "Development Email verification: email={}, url={}",
            email,
            verificationUrl
        );
    }
}
