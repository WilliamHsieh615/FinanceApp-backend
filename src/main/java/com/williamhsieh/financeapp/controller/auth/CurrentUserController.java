package com.williamhsieh.financeapp.controller.auth;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class CurrentUserController {

    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(
        @AuthenticationPrincipal Jwt jwt
    ) {
        return Map.of(
            "userId", jwt.getSubject(),
            "userNumber", jwt.getClaimAsString("userNumber"),
            "email", jwt.getClaimAsString("email"),
            "sessionId", jwt.getClaimAsString("sid"),
            "issuer", jwt.getIssuer().toString(),
            "issuedAt", jwt.getIssuedAt().toString(),
            "expiresAt", jwt.getExpiresAt().toString()
        );
    }
}
