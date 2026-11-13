package com.example.dose.dto;

import jakarta.validation.constraints.NotBlank;

public record GuardianConfirmRequest(
        @NotBlank String token,
        boolean granted) {
}
