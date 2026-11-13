package com.example.dose.dto;

import com.example.dose.entity.AccountStatus;

import java.util.UUID;

public record RegisterResponse(
        UUID userId,
        AccountStatus status,
        String token,
        boolean guardianConfirmationRequired
) {
}
