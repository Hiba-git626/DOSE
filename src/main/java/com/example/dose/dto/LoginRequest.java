package com.example.dose.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String pseudonym,
        @NotBlank String password)
{
}
