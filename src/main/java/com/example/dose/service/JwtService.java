package com.example.dose.service;

import com.example.dose.entity.AppUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/** Émet les jetons d'authentification (JWT signé HS256). Le jeton contient l'identifiant et le rôle. */
@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final long ttlMinutes;
    private final Clock clock;

    public JwtService(JwtEncoder encoder,
                      @Value("${dose.security.jwt-ttl-minutes:60}") long ttlMinutes,
                      Clock clock) {
        this.encoder = encoder;
        this.ttlMinutes = ttlMinutes;
        this.clock = clock;
    }

    public String issue(AppUser user) {
        Instant now = clock.instant();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("dose")
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plus(ttlMinutes, ChronoUnit.MINUTES))
                .claim("role", user.getRole().name())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long ttlSeconds() {
        return ttlMinutes * 60;
    }
}
