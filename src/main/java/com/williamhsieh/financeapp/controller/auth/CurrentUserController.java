package com.williamhsieh.financeapp.controller.auth;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.williamhsieh.financeapp.dto.auth.CurrentUserResponse;
import com.williamhsieh.financeapp.service.auth.CurrentUserService;

@RestController
@RequestMapping("/api/v1/auth")
public class CurrentUserController {

    private final CurrentUserService currentUserService;

    public CurrentUserController(
        CurrentUserService currentUserService
    ) {
        this.currentUserService = currentUserService;
    }

    @GetMapping("/me")
    public CurrentUserResponse getCurrentUser(
        @AuthenticationPrincipal Jwt jwt
    ) {
        return currentUserService.getCurrentUser(
            jwt.getSubject()
        );
    }
}