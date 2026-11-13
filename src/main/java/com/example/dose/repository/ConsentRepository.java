package com.example.dose.repository;

import com.example.dose.entity.Consent;
import com.example.dose.entity.ConsentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConsentRepository extends JpaRepository<Consent,UUID > {

    List<Consent> findByUser_Id(UUID userId);
    List<Consent> findByUser_IdAndType(UUID userId, ConsentType type);
}
