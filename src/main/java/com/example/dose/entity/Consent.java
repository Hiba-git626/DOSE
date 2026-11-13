
package com.example.dose.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "consent")
public class Consent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ConsentType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ConsentChannel channel;


    @Column(nullable = false, length = 30)
    private String textVersion;

    @Column(nullable = false)
    private boolean granted;

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    private LocalDateTime withdrawnAt;

    public Consent() {
    }

    @PrePersist
    public void beforeInsert() {
        if (recordedAt == null) {
            recordedAt = LocalDateTime.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public ConsentType getType() {
        return type;
    }

    public ConsentChannel getChannel(){
        return channel;
    }

    public void setChannel(ConsentChannel channel) {
        this.channel = channel;
    }

    public void setType(ConsentType type) {
        this.type = type;
    }

    public boolean isGranted() {
        return granted;
    }

    public void setGranted(boolean granted) {
        this.granted = granted;
    }

    public String getTextVersion() {
        return textVersion;
    }

    public void setTextVersion(String textVersion) {
        this.textVersion = textVersion;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public LocalDateTime getWithdrawnAt() {
        return withdrawnAt;
    }

    public void setWithdrawnAt(LocalDateTime withdrawnAt) {
        this.withdrawnAt = withdrawnAt;
    }
}
