
package com.example.dose.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 50)
    private String pseudonym;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private Role role = Role.TEEN;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private AccountStatus status= AccountStatus.PENDING_CONSENT;

    @Column(nullable = false)
    private int birthMonth;

    @Column(nullable = false)
    private int birthYear;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length =  20)
    private AgeBand ageBand;

    @Column(nullable = false, length = 10)
    private String preferredLanguage;

    @Column(nullable = false, length = 50)
    private String timeZone;

    @Column(nullable = false)
    private LocalTime bedtime = LocalTime.of(22, 0);

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public AppUser() {
    }

    @PrePersist
    public void beforeInsert() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public String getPseudonym() {
        return pseudonym;
    }

    public void setPseudonym(String pseudonym) {
        this.pseudonym = pseudonym;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role= role;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status= status;
    }

    public AgeBand getAgeBand() {
        return ageBand;
    }

    public void setAgeBand(AgeBand ageBand) {
        this.ageBand= ageBand;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone= timeZone;
    }

    public LocalTime getBedtime() {
        return bedtime;
    }

    public void setBedtime(LocalTime bedtime) {
        this.bedtime= bedtime;
    }

    public int getBirthMonth() {
        return birthMonth;
    }

    public void setBirthMonth(int birthMonth) {
        this.birthMonth = birthMonth;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
