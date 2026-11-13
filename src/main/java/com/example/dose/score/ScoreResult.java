package com.example.dose.score;

import java.util.List;
import java.util.Optional;

/**
 * Résultat d'un calcul. Chaque score garde ses entrées et la version de la formule :
 * un changement de version ne recalcule jamais l'historique.
 *
 * @param topContributors les 3 entrées qui pèsent le plus (E04), hors entrées à 0 point
 * @param priorityEntry   l'entrée modifiable qui pèse le plus : conseil de E04 et objectif du mois (E12) ;
 *                        vide si toutes les entrées modifiables sont à 0
 */
public record ScoreResult(
        int score,
        Riskband band,
        Riskband.Intensity intensity,
        String scoreVersion,
        ScoreInputs inputs,
        List<ScoreEntry> topContributors,
        Optional<ScoreEntry> priorityEntry) {

    /** Points que l'ado peut gagner en améliorant l'entrée prioritaire (0 s'il n'y en a pas). */
    public int priorityPoints() {
        return priorityEntry.map(inputs::get).orElse(0);
    }
}