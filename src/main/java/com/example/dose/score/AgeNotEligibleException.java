package com.example.dose.score;

/** Âge hors de la tranche acceptée (10 à 19 ans) : l'inscription est refusée. */
public class AgeNotEligibleException extends RuntimeException {

    private final int age;

    public AgeNotEligibleException(int age, int minAge, int maxAge) {
        super("Age " + age + " is outside the accepted range " + minAge + "-" + maxAge);
        this.age = age;
    }

    public int age() {
        return age;
    }
}