package com.example.dose.controller;

import com.example.dose.dto.GuardianConfirmRequest;
import com.example.dose.dto.GuardianConfirmResponse;
import com.example.dose.service.GuardianService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/guardian")
public class GuardianController {

    private final GuardianService guardianService;

    public GuardianController(GuardianService guardianService) {
        this.guardianService = guardianService;
    }

    @PostMapping("/confirm")
    public GuardianConfirmResponse confirm(@Valid @RequestBody GuardianConfirmRequest request) {
        return guardianService.confirm(request);
    }
}
