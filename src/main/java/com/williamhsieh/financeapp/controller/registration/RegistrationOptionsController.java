package com.williamhsieh.financeapp.controller.registration;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.williamhsieh.financeapp.dto.registration.RegistrationOptionsResponse;
import com.williamhsieh.financeapp.service.registration.RegistrationOptionsService;

@RestController
@RequestMapping("/api/v1/registration")
public class RegistrationOptionsController {

    private final RegistrationOptionsService registrationOptionsService;

    public RegistrationOptionsController(
        RegistrationOptionsService registrationOptionsService
    ) {
        this.registrationOptionsService = registrationOptionsService;
    }

    @GetMapping("/options")
    public RegistrationOptionsResponse getOptions() {
        return registrationOptionsService.getOptions();
    }
}
