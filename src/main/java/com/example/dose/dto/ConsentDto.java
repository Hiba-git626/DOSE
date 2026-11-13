package com.example.dose.dto;

import com.example.dose.entity.ConsentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ConsentDto(
        @NotNull ConsentType type,
        boolean granted,
        @NotBlank @Size(max = 30) String textVersion) {
}
