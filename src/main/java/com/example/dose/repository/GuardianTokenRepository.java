package com.example.dose.repository;

import com.example.dose.entity.GuardianToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GuardianTokenRepository extends JpaRepository<GuardianToken, UUID> {

    Optional<GuardianToken> findByTokenHash(String tokenHash);
}
