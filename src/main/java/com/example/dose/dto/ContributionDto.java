package com.example.dose.dto;

/** Une entrée du score et ses points (clé technique : brushing_frequency, sugar_intake_score, ...). */
public record ContributionDto(String entry, int points) {
}
