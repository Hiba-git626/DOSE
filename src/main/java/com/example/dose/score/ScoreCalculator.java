package com.example.dose.score;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Stream;

public class ScoreCalculator {

    private final ScoreConfig config;

    public ScoreCalculator(ScoreConfig config) {
        this.config = Objects.requireNonNull(config, "config");
    }

    public ScoreInputs baselineInputs(BaselineAnswers a, YearMonth now) {
        int age = ageAt(a.birthYear(), a.birthMonth(), now);
        if (age < config.minAge() || age > config.maxAge()) {
            throw new AgeNotEligibleException(age, config.minAge(), config.maxAge());
        }
        long sugar = (long) a.sweetItemsPerDay() * config.sugarPointsPerItem();

        Map<ScoreEntry, Integer> p = new EnumMap<>(ScoreEntry.class);
        p.put(ScoreEntry.BRUSHING_FREQUENCY, config.brushingPoints().get(a.brushing()));
        p.put(ScoreEntry.SUGAR_INTAKE_SCORE, (int) Math.min(ScoreEntry.SUGAR_INTAKE_SCORE.max(), sugar));
        p.put(ScoreEntry.FLOSSING_HABIT, config.flossingPoints().get(a.flossing()));
        p.put(ScoreEntry.LAST_DENTAL_VISIT, config.lastVisitPoints().get(a.lastVisit()));
        p.put(ScoreEntry.FAMILY_HEALTH_HISTORY, config.familyPoints().get(a.family()));
        p.put(ScoreEntry.STREAK_COUNT, config.streakPoints().get(a.streak()));
        p.put(ScoreEntry.DAYS_SINCE_LAST_HABIT, config.lastBrushPoints().get(a.lastBrush()));
        p.put(ScoreEntry.AGE, ScoreConfig.pointsFor(config.ageTiers(), age));
        return new ScoreInputs(p);
    }

    /** Étapes 1 et 2 : du questionnaire au résultat complet. */
    public ScoreResult computeBaseline(BaselineAnswers answers, YearMonth now) {
        return compute(baselineInputs(answers, now));
    }

    /** Étape 2 : score, bande, intensité, 3 plus gros contributeurs et entrée prioritaire. */
    public ScoreResult compute(ScoreInputs inputs) {
        int score = inputs.total();
        Riskband band = bandFor(score);

        Comparator<ScoreEntry> heaviestFirst = Comparator
                .comparingInt((ScoreEntry e) -> inputs.get(e)).reversed()
                .thenComparingInt(Enum::ordinal);

        List<ScoreEntry> top = Stream.of(ScoreEntry.values())
                .filter(e -> inputs.get(e) > 0)
                .sorted(heaviestFirst)
                .limit(3)
                .toList();

        Optional<ScoreEntry> priority = Stream.of(ScoreEntry.values())
                .filter(ScoreEntry::modifiable)
                .filter(e -> inputs.get(e) > 0)
                .min(heaviestFirst);

        return new ScoreResult(score, band, band.intensity(), config.version(), inputs, top, priority);
    }

    public Riskband bandFor(int score) {
        for (Riskband band : Riskband.values()) {
            if (score <= config.bandUpperBounds().get(band)) {
                return band;
            }
        }
        throw new IllegalArgumentException("Score out of range: " + score);
    }

    /** Âge en années révolues, au mois près (seuls le mois et l'année de naissance sont connus). */
    public static int ageAt(int birthYear, int birthMonth, YearMonth now) {
        int age = now.getYear() - birthYear;
        if (now.getMonthValue() < birthMonth) {
            age--;
        }
        return age;
    }
}
