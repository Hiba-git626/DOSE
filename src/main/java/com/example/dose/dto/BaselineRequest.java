package com.example.dose.dto;

import com.example.dose.score.Answers.BrushingFrequency;
import com.example.dose.score.Answers.FamilyHealthHistory;
import com.example.dose.score.Answers.FlossingHabit;
import com.example.dose.score.Answers.LastBrush;
import com.example.dose.score.Answers.LastDentalVisit;
import com.example.dose.score.Answers.StreakCount;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record BaselineRequest(
        @NotNull BrushingFrequency brushing,
        @Min(0) @Max(4) int sweetItemsPerDay,
        @NotNull FlossingHabit flossing,
        @NotNull LastDentalVisit lastVisit,
        @NotNull FamilyHealthHistory family,
        @NotNull StreakCount streak,
        @NotNull LastBrush lastBrush,
        @Pattern(regexp = "([01]\\d|2[0-3]):[0-5]\\d") String bedtime
) {
}
