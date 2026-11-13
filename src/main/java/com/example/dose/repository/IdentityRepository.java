package com.example.dose.repository;

import com.example.dose.entity.Identity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IdentityRepository extends JpaRepository<Identity, UUID> {

    Optional<Identity> findByUser_Id(UUID userId);
}
