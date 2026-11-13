package com.example.dose.score;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/** Points des 8 entrées. Chaque valeur est comprise entre 0 et le maximum de l'entrée. */
public record ScoreInputs(Map<ScoreEntry, Integer> points) {

    public ScoreInputs {
        Objects.requireNonNull(points, "points");
        EnumMap<ScoreEntry, Integer> copy = new EnumMap<>(ScoreEntry.class);
        for (ScoreEntry entry : ScoreEntry.values()) {
            Integer value = points.get(entry);
            if (value == null) {
                throw new IllegalArgumentException("Missing entry: " + entry);
            }
            if (value < 0 || value > entry.max()) {
                throw new IllegalArgumentException(
                        entry + " must be between 0 and " + entry.max() + " but was " + value);
            }
            copy.put(entry, value);
        }
        points = Collections.unmodifiableMap(copy);
    }

    public static ScoreInputs of(int brushing, int sugar, int flossing, int visit,
                                 int family, int streak, int lastHabit, int age) {
        EnumMap<ScoreEntry, Integer> map = new EnumMap<>(ScoreEntry.class);
        map.put(ScoreEntry.BRUSHING_FREQUENCY, brushing);
        map.put(ScoreEntry.SUGAR_INTAKE_SCORE, sugar);
        map.put(ScoreEntry.FLOSSING_HABIT, flossing);
        map.put(ScoreEntry.LAST_DENTAL_VISIT, visit);
        map.put(ScoreEntry.FAMILY_HEALTH_HISTORY, family);
        map.put(ScoreEntry.STREAK_COUNT, streak);
        map.put(ScoreEntry.DAYS_SINCE_LAST_HABIT, lastHabit);
        map.put(ScoreEntry.AGE, age);
        return new ScoreInputs(map);
    }

    public int get(ScoreEntry entry) {
        return points.get(entry);
    }

    /** Somme des 8 entrées : toujours un entier entre 0 et 100. */
    public int total() {
        return points.values().stream().mapToInt(Integer::intValue).sum();
    }
}