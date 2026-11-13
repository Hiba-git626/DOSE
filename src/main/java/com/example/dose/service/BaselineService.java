package com.example.dose.service;

import com.example.dose.dto.BaselineRequest;
import com.example.dose.dto.ContributionDto;
import com.example.dose.dto.ScoreResponse;
import com.example.dose.entity.AccountStatus;
import com.example.dose.entity.AppUser;
import com.example.dose.entity.BaselineAnswer;
import com.example.dose.entity.BaselineQuestion;
import com.example.dose.entity.RiskScore;
import com.example.dose.exception.ApiException;
import com.example.dose.repository.AppUserRepository;
import com.example.dose.repository.BaselineAnswerRepository;
import com.example.dose.repository.RiskScoreRepository;
import com.example.dose.score.BaselineAnswers;
import com.example.dose.score.Riskband;
import com.example.dose.score.ScoreCalculator;
import com.example.dose.score.ScoreEntry;
import com.example.dose.score.ScoreInputs;
import com.example.dose.score.ScoreResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Questionnaire de départ : enregistre les réponses, calcule le score initial et le conserve. */
@Service
public class BaselineService {

    private final AppUserRepository users;
    private final BaselineAnswerRepository answers;
    private final RiskScoreRepository riskScores;
    private final ScoreCalculator calculator;
    private final Clock clock;

    public BaselineService(AppUserRepository users,
                           BaselineAnswerRepository answers,
                           RiskScoreRepository riskScores,
                           ScoreCalculator calculator,
                           Clock clock) {
        this.users = users;
        this.answers = answers;
        this.riskScores = riskScores;
        this.calculator = calculator;
        this.clock = clock;
    }

    @Transactional
    public ScoreResponse submit(UUID userId, BaselineRequest request) {
        AppUser user = users.findById(userId)
                .orElseThrow(() -> new ApiException(401, "INVALID_CREDENTIALS", "Unknown user"));
        if (user.getStatus() != AccountStatus.ACTIVE) {
            throw new ApiException(403, "CONSENT_PENDING", "The guardian consent is still pending");
        }
        if (answers.existsByUser_Id(userId)) {
            throw new ApiException(409, "BASELINE_ALREADY_SUBMITTED", "The questionnaire was already submitted");
        }

        BaselineAnswers baseline = new BaselineAnswers(
                request.brushing(), request.flossing(), request.lastVisit(), request.family(), request.streak(),
                request.lastBrush(), request.sweetItemsPerDay(),

                user.getBirthYear(), user.getBirthMonth());
        ScoreResult result = calculator.computeBaseline(baseline, YearMonth.now(clock));

        if (request.bedtime() != null) {
            user.setBedtime(LocalTime.parse(request.bedtime()));
            users.save(user);
        }
        answers.save(BaselineAnswer.of(user, BaselineQuestion.BRUSHING_FREQUENCY, request.brushing().name()));
        answers.save(BaselineAnswer.of(user, BaselineQuestion.SWEET_ITEMS_PER_DAY,
                String.valueOf(request.sweetItemsPerDay())));
        answers.save(BaselineAnswer.of(user, BaselineQuestion.FLOSSING_HABIT, request.flossing().name()));
        answers.save(BaselineAnswer.of(user, BaselineQuestion.LAST_DENTAL_VISIT, request.lastVisit().name()));
        answers.save(BaselineAnswer.of(user, BaselineQuestion.FAMILY_HISTORY, request.family().name()));
        answers.save(BaselineAnswer.of(user, BaselineQuestion.STREAK, request.streak().name()));
        answers.save(BaselineAnswer.of(user, BaselineQuestion.LAST_BRUSH, request.lastBrush().name()));

        RiskScore saved = riskScores.save(RiskScore.from(user, result));
        return toResponse(saved, result);
    }

    /** Dernier score enregistré. Score, bande, version et date viennent de la base : l'historique n'est pas recalculé. */
    @Transactional(readOnly = true)
    public ScoreResponse latest(UUID userId) {
        RiskScore stored = riskScores.findFirstByUser_IdOrderByComputedAtDesc(userId)
                .orElseThrow(() -> new ApiException(404, "NO_SCORE", "No score computed yet"));
        ScoreInputs inputs = ScoreInputs.of(
                stored.getBrushingFrequency(), stored.getSugarIntakeScore(), stored.getFlossingHabit(),
                stored.getLastDentalVisit(), stored.getFamilyHealthHistory(), stored.getStreakCount(),
                stored.getDaysSinceLastHabit(), stored.getAge());
        return toResponse(stored, calculator.compute(inputs));
    }

    private static ScoreResponse toResponse(RiskScore stored, ScoreResult details) {
        Map<String, Integer> inputs = new LinkedHashMap<>();
        for (ScoreEntry entry : ScoreEntry.values()) {
            inputs.put(entry.code(), details.inputs().get(entry));
        }
        List<ContributionDto> top = details.topContributors().stream()
                .map(entry -> new ContributionDto(entry.code(), details.inputs().get(entry)))
                .toList();
        ContributionDto advice = details.priorityEntry()
                .map(entry -> new ContributionDto(entry.code(), details.inputs().get(entry)))
                .orElse(null);
        Riskband band = stored.getBand();
        return new ScoreResponse(stored.getScore(), band, band.intensity(), band.intensity().remindersPerWeek(),
                stored.getScoreVersion(), stored.getComputedAt(), inputs, top, advice);
    }
}
