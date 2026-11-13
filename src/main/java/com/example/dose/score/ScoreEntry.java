package com.example.dose.score;

public enum ScoreEntry {
    BRUSHING_FREQUENCY("brushing_frequency", 25,true),
    SUGAR_INTAKE_SCORE("sugar_intake_score", 20,true),
    FLOSSING_HABIT("flossing_habit", 15,true),
    LAST_DENTAL_VISIT("last_dental_visit", 12, true),
    FAMILY_HEALTH_HISTORY("family_health_history", 10, false),
    STREAK_COUNT("streak_count ", 8, true),
    DAYS_SINCE_LAST_HABIT("days_since_last_habit", 7, true),
    AGE("age", 3, false);

    private final String code;
    private final int max;
    private final boolean modifiable;

    ScoreEntry(String code, int max, boolean modifiable){
        this.code = code;
        this.max=max;
        this.modifiable=modifiable;
    }

    /** Nom technique de l'entrée (celui du Dossier technique). */
    public String code() {
        return code;
    }

    /** Points maximum de l'entrée. La somme des maxima vaut 100. */
    public int max() {
        return max;
    }

    /** Faux pour les antécédents familiaux et l'âge : jamais un objectif. */
    public boolean modifiable() {
        return modifiable;
    }
}
