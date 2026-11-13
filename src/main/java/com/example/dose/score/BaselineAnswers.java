package com.example.dose.score;
import com.example.dose.score.Answers.BrushingFrequency;
import com.example.dose.score.Answers.FlossingHabit;
import com.example.dose.score.Answers.FamilyHealthHistory;
import com.example.dose.score.Answers.LastDentalVisit;
import com.example.dose.score.Answers.StreakCount;
import com.example.dose.score.Answers.LastBrush;

import java.util.Objects;

public record BaselineAnswers (
        BrushingFrequency brushing,
        FlossingHabit flossing,
        LastDentalVisit lastVisit,
        FamilyHealthHistory family,
        StreakCount streak,
        LastBrush lastBrush,
        int birthYear,
        int birthMonth,
        int sweetItemsPerDay

        ){

    public BaselineAnswers {
        Objects.requireNonNull(brushing, "brushing");
        Objects.requireNonNull(flossing, "flossing");
        Objects.requireNonNull(lastVisit, "lastVisit");
        Objects.requireNonNull(family, "family");
        Objects.requireNonNull(streak, "streak");
        Objects.requireNonNull(lastBrush, "lastBrush");
        if (sweetItemsPerDay < 0) {
            throw new IllegalArgumentException("sweetItemsPerDay must be >= 0");
        }
        if (birthMonth < 1 || birthMonth > 12) {
            throw new IllegalArgumentException("birthMonth must be between 1 and 12");
        }
    }


}
