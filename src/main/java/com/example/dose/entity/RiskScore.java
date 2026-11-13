package com.example.dose.entity;

import com.example.dose.score.Riskband;
import com.example.dose.score.ScoreEntry;
import com.example.dose.score.ScoreResult;
import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Un score calculé, avec ses 8 entrées et la version de la formule.
 * Entité immuable : l'historique n'est jamais réécrit, un nouveau calcul crée une nouvelle ligne.
 */
@Entity
@Immutable
@Table(name = "risk_score")
public class RiskScore {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(nullable = false)
    private LocalDateTime computedAt;

    @Column(nullable = false)
    private int brushingFrequency;

    @Column(nullable = false)
    private int sugarIntakeScore;

    @Column(nullable = false)
    private int flossingHabit;

    @Column(nullable = false)
    private int lastDentalVisit;

    @Column(nullable = false)
    private int familyHealthHistory;

    @Column(nullable = false)
    private int streakCount;

    @Column(nullable = false)
    private int daysSinceLastHabit;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false)
    private int score;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private Riskband band;

    @Column(nullable = false, length = 30)
    private String scoreVersion;

    protected RiskScore (){
    }

    public static RiskScore from(AppUser user, ScoreResult result){
        RiskScore s = new RiskScore();
        s.user=user;
        s.brushingFrequency=result.inputs().get(ScoreEntry.BRUSHING_FREQUENCY);
        s.flossingHabit=result.inputs().get(ScoreEntry.FLOSSING_HABIT);
        s.familyHealthHistory=result.inputs().get(ScoreEntry.FAMILY_HEALTH_HISTORY);
        s.lastDentalVisit=result.inputs().get(ScoreEntry.LAST_DENTAL_VISIT);
        s.streakCount=result.inputs().get(ScoreEntry.STREAK_COUNT);
        s.age=result.inputs().get(ScoreEntry.AGE);
        s.daysSinceLastHabit=result.inputs().get(ScoreEntry.DAYS_SINCE_LAST_HABIT);
        s.band=result.band();
        s.score= result.score();
        s.scoreVersion= result.scoreVersion();
        return s;
    }

    @PrePersist
    void beforInsert(){
        if (computedAt == null){
            computedAt = LocalDateTime.now();
        }
    }

    public UUID getId() {
        return id;
    }
    public AppUser getUser() {
        return user;
    }
    public LocalDateTime getComputedAt() {
        return computedAt;
    }
    public int getBrushingFrequency() {
        return brushingFrequency;
    }
    public int getSugarIntakeScore() {
        return sugarIntakeScore;
    }
    public int getFlossingHabit() {
        return flossingHabit;
    }
    public int getLastDentalVisit() {
        return lastDentalVisit;
    }
    public int getFamilyHealthHistory() {
        return familyHealthHistory;
    }
    public int getStreakCount() {
        return streakCount;
    }
    public int getDaysSinceLastHabit() {
        return daysSinceLastHabit;
    }
    public int getAge() {
        return age;
    }
    public int getScore() {
        return score;
    }
    public Riskband getBand() {
        return band;
    }
    public String getScoreVersion() {
        return scoreVersion;
    }

}
