package com.example.dose.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 50) String pseudonym,
        @NotBlank @Size(min = 8, max = 72) String password,
        @Min(1) @Max(12) int birthMonth,
        @Min(1900) @Max(2100) int birthYear,
        @NotBlank @Pattern(regexp = "ar|fr|en") String preferredLanguage,
        @NotBlank String timeZone,
        @NotNull @Valid ContactDto contact,
        @Valid ContactDto guardianContact,
        @NotEmpty List<@Valid ConsentDto> consents
) {
}
