package com.example.dose.service;

import com.example.dose.dto.GuardianConfirmRequest;
import com.example.dose.dto.GuardianConfirmResponse;
import com.example.dose.entity.AccountStatus;
import com.example.dose.entity.AppUser;
import com.example.dose.entity.Consent;
import com.example.dose.entity.ConsentType;
import com.example.dose.entity.GuardianToken;
import com.example.dose.exception.ApiException;
import com.example.dose.repository.AppUserRepository;
import com.example.dose.repository.ConsentRepository;
import com.example.dose.repository.GuardianTokenRepository;
import com.example.dose.security.TokenHasher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

/** Confirmation (ou refus) du tuteur via le lien reçu. Le jeton est à usage unique et expire. */
@Service
public class GuardianService {

    private final GuardianTokenRepository tokens;
    private final ConsentRepository consents;
    private final AppUserRepository users;
    private final Clock clock;
    private final String consentTextVersion;

    public GuardianService(GuardianTokenRepository tokens,
                           ConsentRepository consents,
                           AppUserRepository users,
                           Clock clock,
                           @Value("${dose.consent.text-version:v1}") String consentTextVersion) {
        this.tokens = tokens;
        this.consents = consents;
        this.users = users;
        this.clock = clock;
        this.consentTextVersion = consentTextVersion;
    }

    @Transactional
    public GuardianConfirmResponse confirm(GuardianConfirmRequest request) {
        GuardianToken token = tokens.findByTokenHash(TokenHasher.sha256Hex(request.token().trim()))
                .orElseThrow(GuardianService::invalidToken);
        LocalDateTime now = LocalDateTime.now(clock);
        if (!token.isUsable(now)) {
            throw invalidToken();
        }

        AppUser user = token.getUser();
        token.markUsed(now);
        tokens.save(token);

        Consent consent = new Consent();
        consent.setUser(user);
        consent.setType(ConsentType.GUARDIAN);
        consent.setChannel(token.getChannel());
        consent.setGranted(request.granted());
        consent.setTextVersion(consentTextVersion);
        consents.save(consent);

        if (request.granted()) {
            user.setStatus(AccountStatus.ACTIVE);
            users.save(user);
        }
        return new GuardianConfirmResponse(user.getStatus());
    }

    private static ApiException invalidToken() {
        return new ApiException(410, "GUARDIAN_TOKEN_INVALID", "Unknown, expired or already used link");
    }
}
