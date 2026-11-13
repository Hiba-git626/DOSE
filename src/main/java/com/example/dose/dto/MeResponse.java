package com.example.dose.dto;

import com.example.dose.entity.AccountStatus;
import com.example.dose.entity.AgeBand;
import com.example.dose.entity.Role;

import java.time.LocalTime;
import java.util.UUID;

public record MeResponse(
        UUID userId,
        String pseudonym,
        Role role,
        AccountStatus status,
        AgeBand ageBand,
        String preferredLanguage,
        String timeZone,
        LocalTime bedtime,
        boolean baselineCompleted
) {
}
