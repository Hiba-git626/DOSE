package com.example.dose.dto;

import java.util.List;

public record ConfigResponse(
        List<String> languages,
        int minAge,
        int maxAge,
        String consentTextVersion,
        String scoreVersion) {
}
