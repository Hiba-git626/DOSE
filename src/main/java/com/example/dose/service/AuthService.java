package com.example.dose.service;

import com.example.dose.dto.LoginRequest;
import com.example.dose.dto.LoginResponse;
import com.example.dose.entity.AppUser;
import com.example.dose.entity.Credential;
import com.example.dose.exception.ApiException;
import com.example.dose.repository.AppUserRepository;
import com.example.dose.repository.CredentialRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

/** Connexion par pseudonyme et mot de passe, avec verrouillage après 5 échecs consécutifs. */
@Service
public class AuthService {

    static final int MAX_FAILED_ATTEMPTS = 5;
    static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private final AppUserRepository users;
    private final CredentialRepository credentials;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final Clock clock;
    /** Sert à dépenser le même temps de calcul quand le pseudonyme n'existe pas. */
    private final String dummyHash;

    public AuthService(AppUserRepository users,
                       CredentialRepository credentials,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       Clock clock) {
        this.users = users;
        this.credentials = credentials;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.clock = clock;
        this.dummyHash = passwordEncoder.encode("dummy-password-for-timing");
    }

    /**
     * noRollbackFor : quand le mot de passe est faux, on lève une exception, mais le compteur d'échecs
     * doit quand même être enregistré. Sans cela, la transaction serait annulée et le verrouillage
     * ne fonctionnerait jamais.
     */
    @Transactional(noRollbackFor = ApiException.class)
    public LoginResponse login(LoginRequest request) {
        AppUser user = users.findByPseudonym(request.pseudonym().trim()).orElse(null);
        Credential credential = user == null ? null : credentials.findByUser_Id(user.getId()).orElse(null);
        if (credential == null) {
            passwordEncoder.matches(request.password(), dummyHash);
            throw invalidCredentials();
        }

        LocalDateTime now = LocalDateTime.now(clock);
        if (credential.isLocked(now)) {
            throw new ApiException(423, "ACCOUNT_LOCKED", "Too many failed attempts, try again later");
        }
        if (!passwordEncoder.matches(request.password(), credential.getPasswordHash())) {
            credential.registerFailure(MAX_FAILED_ATTEMPTS, now.plus(LOCK_DURATION));
            credentials.save(credential);
            throw invalidCredentials();
        }

        credential.resetFailures();
        credentials.save(credential);
        return new LoginResponse(jwtService.issue(user), jwtService.ttlSeconds(), user.getStatus(), user.getRole());
    }

    private static ApiException invalidCredentials() {
        return new ApiException(401, "INVALID_CREDENTIALS", "Wrong pseudonym or password");
    }
}
