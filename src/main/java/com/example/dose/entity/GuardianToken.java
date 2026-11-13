package com.example.dose.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Jeton de confirmation envoyé au tuteur. Seule l'empreinte SHA-256 du jeton est stockée :
 * le jeton lui-même n'existe que dans le message envoyé au tuteur.
 */
@Entity
@Table(name = "guardian_token")
@Getter
public class GuardianToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(nullable = false, length = 64, unique = true)
    private String tokenHash;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private ConsentChannel channel;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime usedAt;

    protected GuardianToken() {
    }

    public static GuardianToken create(AppUser user, String tokenHash, ConsentChannel channel,
                                       LocalDateTime expiresAt) {
        GuardianToken t = new GuardianToken();
        t.user = user;
        t.tokenHash = tokenHash;
        t.channel = channel;
        t.expiresAt = expiresAt;
        return t;
    }

    /**
     * Utilisable : jamais utilisé et pas encore expiré.
     */
    public boolean isUsable(LocalDateTime now) {
        return usedAt == null && now.isBefore(expiresAt);
    }

    public void markUsed(LocalDateTime now) {
        this.usedAt = now;
    }
}
