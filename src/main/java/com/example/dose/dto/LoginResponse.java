package com.example.dose.dto;

import com.example.dose.entity.AccountStatus;
import com.example.dose.entity.Role;

public record LoginResponse(
        String token,
        long expiresInSeconds,
        AccountStatus status,
        Role role
) {
}
