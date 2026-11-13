package com.example.dose.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ContactDto(
        @NotNull ContactType type,
        @NotBlank @Size(max = 200) String value) {
}
