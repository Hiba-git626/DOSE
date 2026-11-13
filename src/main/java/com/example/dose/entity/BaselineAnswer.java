package com.example.dose.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Une réponse du questionnaire de départ (E03). La réponse est stockée sous forme de texte :
 * le nom de la valeur de l'enum (ex. ONCE_A_DAY) ou, pour les sucres, le nombre (ex. "2").
 */
@Entity
@Table(name = "baseline_answer")
public class BaselineAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // LAZY: récupère l'objet juste si on en a besoin, éviter d'utuliser @Data avec LAZY
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 30)
    private BaselineQuestion question;

    @Column(nullable = false, length = 40)
    private String answer;

    @Column(nullable = false)
    private LocalDateTime answeredAt;

    public BaselineAnswer() {
    }

    public static BaselineAnswer of(AppUser user, BaselineQuestion question, String answer) {
        BaselineAnswer a = new BaselineAnswer();
        a.user = user;
        a.question = question;
        a.answer = answer;
        return a;
    }

    @PrePersist
    public void beforeInsert() {
        if (answeredAt == null) {
            answeredAt = LocalDateTime.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public AppUser getUser() {
        return user;
    }

    public BaselineQuestion getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public LocalDateTime getAnsweredAt() {
        return answeredAt;
    }
}
