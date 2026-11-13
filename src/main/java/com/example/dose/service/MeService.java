package com.example.dose.service;

import com.example.dose.dto.MeResponse;
import com.example.dose.entity.AppUser;
import com.example.dose.exception.ApiException;
import com.example.dose.repository.AppUserRepository;
import com.example.dose.repository.BaselineAnswerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MeService {

    private final AppUserRepository users;
    private final BaselineAnswerRepository baselineAnswers;

    public MeService(AppUserRepository users, BaselineAnswerRepository baselineAnswers) {
        this.users = users;
        this.baselineAnswers = baselineAnswers;
    }

    @Transactional(readOnly = true)
    public MeResponse get(UUID userId) {
        AppUser user = users.findById(userId)
                .orElseThrow(() -> new ApiException(401, "INVALID_CREDENTIALS", "Unknown user"));
        return new MeResponse(
                user.getId(),
                user.getPseudonym(),
                user.getRole(),
                user.getStatus(),
                user.getAgeBand(),
                user.getPreferredLanguage(),
                user.getTimeZone(),
                user.getBedtime(),
                baselineAnswers.existsByUser_Id(userId));
    }
}
