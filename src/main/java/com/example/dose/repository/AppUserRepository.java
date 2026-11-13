package com.example.dose.repository;

import com.example.dose.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByPseudonym(String pseudonym);

    boolean existsByPseudonym(String pseudonym);
}
