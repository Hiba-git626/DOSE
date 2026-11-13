package com.example.dose.controller;

import com.example.dose.dto.ConfigResponse;
import com.example.dose.score.ScoreConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ConfigController {

    private final ScoreConfig scoreConfig;
    private final String consentTextVersion;

    public ConfigController(ScoreConfig scoreConfig,
                            @Value("${dose.consent.text-version:v1}") String consentTextVersion) {
        this.scoreConfig = scoreConfig;
        this.consentTextVersion = consentTextVersion;
    }

    @GetMapping("/config")
    public ConfigResponse config() {
        return new ConfigResponse(List.of("ar", "fr", "en"), scoreConfig.minAge(), scoreConfig.maxAge(),
                consentTextVersion, scoreConfig.version());
    }
}
