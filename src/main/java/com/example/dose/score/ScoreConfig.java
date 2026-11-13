package com.example.dose.score;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.example.dose.score.Answers.BrushingFrequency;
import com.example.dose.score.Answers.FlossingHabit;
import com.example.dose.score.Answers.FamilyHealthHistory;
import com.example.dose.score.Riskband;
import com.example.dose.score.Answers.LastDentalVisit;
import com.example.dose.score.Answers.StreakCount;
import com.example.dose.score.Answers.LastBrush;



public record ScoreConfig(
        String version,
        Map<Answers.BrushingFrequency,Integer> brushingPoints,
        int sugarPointsPerItem,
        Map<Answers.FlossingHabit,Integer> flossingPoints,
        Map<Answers.LastDentalVisit, Integer> lastVisitPoints,
        Map<Answers.FamilyHealthHistory,Integer> familyPoints,
        Map<Answers.StreakCount,Integer> streakPoints,
        Map<Answers.LastBrush,Integer> lastBrushPoints,
        int minAge,
        int maxAge,
        List<Tier> ageTiers,
        Map<Riskband, Integer> bandUpperBounds

) {
    public record Tier(double min, int points) {
    }

    public ScoreConfig{
        Objects.requireNonNull(version, "version");
        brushingPoints = checked(brushingPoints, Answers.BrushingFrequency.class, ScoreEntry.BRUSHING_FREQUENCY);
        flossingPoints = checked(flossingPoints, Answers.FlossingHabit.class, ScoreEntry.FLOSSING_HABIT);
        lastVisitPoints = checked(lastVisitPoints, Answers.LastDentalVisit.class, ScoreEntry.LAST_DENTAL_VISIT);
        familyPoints = checked(familyPoints, Answers.FamilyHealthHistory.class, ScoreEntry.FAMILY_HEALTH_HISTORY);
        streakPoints = checked(streakPoints, Answers.StreakCount.class, ScoreEntry.STREAK_COUNT);
        lastBrushPoints = checked(lastBrushPoints, Answers.LastBrush.class, ScoreEntry.DAYS_SINCE_LAST_HABIT);
        if (sugarPointsPerItem < 0) {
            throw new IllegalArgumentException("sugarPointsPerItem must be >= 0");
        }
        if (minAge > maxAge) {
            throw new IllegalArgumentException("minAge must be <= maxAge");
        }
        ageTiers = checkedTiers(ageTiers);
        bandUpperBounds = checkedBands(bandUpperBounds);
    }


    /** Version 1 : reproduit le calculateur du Screen Atlas. */
    public static ScoreConfig defaultV1() {
        return new ScoreConfig(
                "v1",
                Map.of(BrushingFrequency.TWICE_A_DAY,0,
                        BrushingFrequency.ONCE_A_DAY,12,
                        BrushingFrequency.A_FEW_TIMES_A_WEEK,20,
                        BrushingFrequency.WHEN_I_REMEMBER,25),
                5,
                Map.of(FlossingHabit.EVERY_DAY,0,
                        FlossingHabit.SOMETIMES,8,
                        FlossingHabit.NEVER,15),
                Map.of(LastDentalVisit.UNDER_6_MONTHS,0,
                        LastDentalVisit.SIX_TO_12_MONTHS,5,
                        LastDentalVisit.OVER_12_MONTHS,9,
                        LastDentalVisit.NEVER,12),
                Map.of(FamilyHealthHistory.YES,10,
                        FamilyHealthHistory.NO,0,
                        FamilyHealthHistory.UNKNOWN,0),
                Map.of(StreakCount.SEVEN_OR_MORE,0,
                        StreakCount.ONE_TO_SIX,4,
                        StreakCount.NONE,8),
                Map.of(LastBrush.TODAY,0,
                        LastBrush.YESTERDAY,2,
                        LastBrush.TWO_TO_SIX_DAYS_AGO,5,
                        LastBrush.SEVEN_DAYS_OR_MORE,7),
                10,19,
                List.of(new Tier(18, 1), new Tier(16, 2), new Tier(10, 3)),
                Map.of(Riskband.VERY_LOW,20,
                        Riskband.LOW,40,
                        Riskband.MODERATE,60,
                        Riskband.HIGH,80,
                        Riskband.VERY_HIGH,100));

    }

    /** Points du premier palier atteint par {@code value}. */
    public static int pointsFor(List<Tier> tiers, double value) {
        for (Tier tier : tiers) {
            if (value >= tier.min()) {
                return tier.points();
            }
        }
        throw new IllegalArgumentException("No tier matches value " + value);
    }

    private static <E extends Enum<E>> Map<E, Integer> checked(Map<E, Integer> map, Class<E> type, ScoreEntry entry) {
        Objects.requireNonNull(map, entry.code());
        for (E constant : type.getEnumConstants()) {
            Integer points = map.get(constant);
            if (points == null) {
                throw new IllegalArgumentException(entry.code() + ": no points for " + constant);
            }
            if (points < 0 || points > entry.max()) {
                throw new IllegalArgumentException(
                        entry.code() + ": " + constant + " must be between 0 and " + entry.max());
            }
        }
        return Map.copyOf(map);
    }

    private static List<Tier> checkedTiers(List<Tier> tiers) {
        Objects.requireNonNull(tiers, "ageTiers");
        if (tiers.isEmpty()) {
            throw new IllegalArgumentException("ageTiers must not be empty");
        }
        double previousMin = Double.POSITIVE_INFINITY;
        for (Tier tier : tiers) {
            if (tier.min() >= previousMin) {
                throw new IllegalArgumentException("ageTiers must be sorted by strictly decreasing min");
            }
            if (tier.points() < 0 || tier.points() > ScoreEntry.AGE.max()) {
                throw new IllegalArgumentException("age points must be between 0 and " + ScoreEntry.AGE.max());
            }
            previousMin = tier.min();
        }
        return List.copyOf(tiers);
    }

    private static Map<Riskband, Integer> checkedBands(Map<Riskband, Integer> bounds) {
        Objects.requireNonNull(bounds, "bandUpperBounds");
        int previous = -1;
        for (Riskband band : Riskband.values()) {
            Integer bound = bounds.get(band);
            if (bound == null) {
                throw new IllegalArgumentException("No upper bound for band " + band);
            }
            if (bound <= previous) {
                throw new IllegalArgumentException("Band upper bounds must be strictly increasing");
            }
            previous = bound;
        }
        if (previous != 100) {
            throw new IllegalArgumentException("The last band must end at 100");
        }
        return Map.copyOf(bounds);
    }
}
