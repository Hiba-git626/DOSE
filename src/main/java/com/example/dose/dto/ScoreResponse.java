package com.example.dose.dto;

import com.example.dose.score.Riskband;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Score tel que l'écran E04 l'affiche. L'API ne renvoie que des codes : l'application les traduit.
 *
 * @param topContributors les 3 entrées qui pèsent le plus
 * @param advice          l'entrée modifiable qui pèse le plus (null si toutes sont à 0)
 */
public record ScoreResponse(
        int score,
        Riskband band,
        Riskband.Intensity intensity,
        int remindersPerWeek,
        String scoreVersion,
        LocalDateTime computedAt,
        Map<String, Integer> inputs,
        List<ContributionDto> topContributors,
        ContributionDto advice) {
}
