package com.example.dose.repository;

import com.example.dose.entity.BaselineAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BaselineAnswerRepository extends JpaRepository<BaselineAnswer, UUID> {

    List<BaselineAnswer> findByUser_Id(UUID userId);

    boolean existsByUser_Id(UUID userId);
}
