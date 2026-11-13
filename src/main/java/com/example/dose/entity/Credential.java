package com.example.dose.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/** Mot de passe haché (bcrypt) et compteur d'échecs de connexion. Jamais de mot de passe en clair. */
@Entity
@Table(name = "credential")
@Getter
public class Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private AppUser user;

    @Column(nullable = false, length = 100)
    private String passwordHash;

    @Column(nullable = false)
    private int failedAttempts;

    private LocalDateTime lockedUntil;

    protected Credential() {
    }

    public static Credential of(AppUser user, String passwordHash) {
        Credential c = new Credential();
        c.user = user;
        c.passwordHash = passwordHash;
        return c;
    }

    public boolean isLocked(LocalDateTime now) {
        return lockedUntil != null && now.isBefore(lockedUntil);
    }

    /** Compte un échec ; au {@code maxAttempts}-ième, verrouille jusqu'à {@code lockUntil}. */
    public void registerFailure(int maxAttempts, LocalDateTime lockUntil) {
        failedAttempts++;
        if (failedAttempts >= maxAttempts) {
            lockedUntil = lockUntil;
            failedAttempts = 0;
        }
    }

    public void resetFailures() {
        failedAttempts = 0;
        lockedUntil = null;
    }


}
