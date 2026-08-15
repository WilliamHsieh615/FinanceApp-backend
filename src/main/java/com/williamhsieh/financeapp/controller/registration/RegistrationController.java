package com.williamhsieh.financeapp.controller.registration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.williamhsieh.financeapp.dto.registration.RegisterUserRequest;
import com.williamhsieh.financeapp.dto.registration.RegisterUserResponse;
import com.williamhsieh.financeapp.service.registration.RegistrationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/registration")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(
        RegistrationService registrationService
    ) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<RegisterUserResponse> register(
        @Valid @RequestBody RegisterUserRequest request
    ) {
        RegisterUserResponse response =
            registrationService.register(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }
}
