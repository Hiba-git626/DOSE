package com.example.dose.repository;

import com.example.dose.entity.RiskScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RiskScoreRepository extends JpaRepository<RiskScore, UUID> {

    Optional<RiskScore> findFirstByUser_IdOrderByComputedAtDesc(UUID userId);

    List<RiskScore> findByUser_IdOrderByComputedAtDesc(UUID userId);
}
