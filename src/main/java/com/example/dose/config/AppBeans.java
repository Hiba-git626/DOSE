package com.example.dose.config;

import com.example.dose.score.ScoreCalculator;
import com.example.dose.score.ScoreConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;

/** Objets partagés : horloge, moteur de score, hachage des mots de passe. */
@Configuration
public class AppBeans {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public ScoreConfig scoreConfig() {
        return ScoreConfig.defaultV1();
    }

    @Bean
    public ScoreCalculator scoreCalculator(ScoreConfig scoreConfig) {
        return new ScoreCalculator(scoreConfig);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
