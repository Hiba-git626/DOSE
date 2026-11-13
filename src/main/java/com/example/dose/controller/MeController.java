package com.example.dose.controller;

import com.example.dose.dto.BaselineRequest;
import com.example.dose.dto.MeResponse;
import com.example.dose.dto.ScoreResponse;
import com.example.dose.service.BaselineService;
import com.example.dose.service.MeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class MeController {

    private final MeService meService;
    private final BaselineService baselineService;


    public MeController(MeService meService, BaselineService baselineService) {

        this.meService = meService;
        this.baselineService = baselineService;
    }

    /** L'identifiant vient du jeton (champ « sub »), jamais de l'URL : un ado ne voit que son compte. */
    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal Jwt jwt) {

        return meService.get(UUID.fromString(jwt.getSubject()));
    }

    @PostMapping("/me/baseline")
    @ResponseStatus(HttpStatus.CREATED)
    public ScoreResponse submitBaseline(@AuthenticationPrincipal Jwt jwt,
                                        @Valid @RequestBody BaselineRequest request) {
        return baselineService.submit(userId(jwt), request);
    }

    @GetMapping("/me/score")
    public ScoreResponse latestScore(@AuthenticationPrincipal Jwt jwt) {
        return baselineService.latest(userId(jwt));
    }

    private static UUID userId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
