package com.example.dose.service;

import com.example.dose.dto.ConsentDto;
import com.example.dose.dto.ContactDto;
import com.example.dose.dto.ContactType;
import com.example.dose.dto.RegisterRequest;
import com.example.dose.dto.RegisterResponse;
import com.example.dose.entity.AccountStatus;
import com.example.dose.entity.AgeBand;
import com.example.dose.entity.AppUser;
import com.example.dose.entity.Consent;
import com.example.dose.entity.ConsentChannel;
import com.example.dose.entity.ConsentType;
import com.example.dose.entity.Credential;
import com.example.dose.entity.GuardianToken;
import com.example.dose.entity.Identity;
import com.example.dose.entity.Role;
import com.example.dose.exception.ApiException;
import com.example.dose.repository.AppUserRepository;
import com.example.dose.repository.ConsentRepository;
import com.example.dose.repository.CredentialRepository;
import com.example.dose.repository.GuardianTokenRepository;
import com.example.dose.repository.IdentityRepository;
import com.example.dose.score.AgeNotEligibleException;
import com.example.dose.score.ScoreCalculator;
import com.example.dose.score.ScoreConfig;
import com.example.dose.security.TokenHasher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/** Inscription : crée le compte, l'identité chiffrée, les consentements et, sous 18 ans, le jeton du tuteur. */
@Service
public class RegistrationService {

    private static final int ADULT_AGE = 18;
    private static final Set<ConsentType> NOT_ACCEPTED_AT_REGISTRATION =
            EnumSet.of(ConsentType.GUARDIAN, ConsentType.PHOTO);

    private final AppUserRepository users;
    private final CredentialRepository credentials;
    private final IdentityRepository identities;
    private final ConsentRepository consents;
    private final GuardianTokenRepository guardianTokens;
    private final PasswordEncoder passwordEncoder;
    private final ScoreConfig scoreConfig;
    private final Clock clock;
    private final JwtService jwtService;
    private final GuardianNotifier notifier;
    private final long guardianTokenTtlHours;

    public RegistrationService(AppUserRepository users,
                               CredentialRepository credentials,
                               IdentityRepository identities,
                               ConsentRepository consents,
                               GuardianTokenRepository guardianTokens,
                               PasswordEncoder passwordEncoder,
                               ScoreConfig scoreConfig,
                               Clock clock,
                               JwtService jwtService,
                               GuardianNotifier notifier,
                               @Value("${dose.guardian.token-ttl-hours:168}") long guardianTokenTtlHours) {
        this.users = users;
        this.credentials = credentials;
        this.identities = identities;
        this.consents = consents;
        this.guardianTokens = guardianTokens;
        this.passwordEncoder = passwordEncoder;
        this.scoreConfig = scoreConfig;
        this.clock = clock;
        this.jwtService = jwtService;
        this.notifier = notifier;
        this.guardianTokenTtlHours = guardianTokenTtlHours;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        int age = ScoreCalculator.ageAt(request.birthYear(), request.birthMonth(), YearMonth.now(clock));
        if (age < scoreConfig.minAge() || age > scoreConfig.maxAge()) {
            throw new AgeNotEligibleException(age, scoreConfig.minAge(), scoreConfig.maxAge());
        }
        validateConsents(request.consents());

        boolean minor = age < ADULT_AGE;
        ContactDto guardianContact = request.guardianContact();
        if (minor && guardianContact == null) {
            throw new ApiException(422, "GUARDIAN_CONTACT_REQUIRED", "A guardian contact is required under 18");
        }
        ContactValidator.validate(request.contact());
        if (guardianContact != null) {
            ContactValidator.validate(guardianContact);
        }
        validatePassword(request.password());
        validateTimeZone(request.timeZone());

        String pseudonym = request.pseudonym().trim();
        if (pseudonym.length() < 3) {
            throw new ApiException(400, "VALIDATION_ERROR", "Pseudonym must have at least 3 characters");
        }
        if (users.existsByPseudonym(pseudonym)) {
            throw new ApiException(409, "PSEUDONYM_TAKEN", "This pseudonym is already used");
        }

        AppUser user = new AppUser();
        user.setPseudonym(pseudonym);
        user.setRole(Role.TEEN);
        user.setStatus(minor ? AccountStatus.PENDING_CONSENT : AccountStatus.ACTIVE);
        user.setBirthMonth(request.birthMonth());
        user.setBirthYear(request.birthYear());
        user.setAgeBand(AgeBand.fromAge(age));
        user.setPreferredLanguage(request.preferredLanguage());
        user.setTimeZone(request.timeZone());
        try {
            users.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            throw new ApiException(409, "PSEUDONYM_TAKEN", "This pseudonym is already used");
        }

        credentials.save(Credential.of(user, passwordEncoder.encode(request.password())));
        identities.save(buildIdentity(user, request.contact(), guardianContact));
        for (ConsentDto dto : request.consents()) {
            Consent consent = new Consent();
            consent.setUser(user);
            consent.setType(dto.type());
            consent.setChannel(ConsentChannel.APP);
            consent.setGranted(dto.granted());
            consent.setTextVersion(dto.textVersion());
            consents.save(consent);
        }

        if (minor) {
            sendGuardianLink(user, guardianContact);
        }
        return new RegisterResponse(user.getId(), user.getStatus(), jwtService.issue(user), minor);
    }

    private void validateConsents(List<ConsentDto> list) {
        Set<ConsentType> seen = EnumSet.noneOf(ConsentType.class);
        boolean appUseGranted = false;
        for (ConsentDto dto : list) {
            if (NOT_ACCEPTED_AT_REGISTRATION.contains(dto.type())) {
                throw new ApiException(400, "VALIDATION_ERROR",
                        "Consent type " + dto.type() + " cannot be set at registration");
            }
            if (!seen.add(dto.type())) {
                throw new ApiException(400, "VALIDATION_ERROR", "Duplicate consent type " + dto.type());
            }
            if (dto.type() == ConsentType.APP_USE && dto.granted()) {
                appUseGranted = true;
            }
        }
        if (!appUseGranted) {
            throw new ApiException(422, "CONSENT_REQUIRED", "The APP_USE consent must be granted");
        }
    }

    private void validatePassword(String password) {
        // bcrypt ne lit que les 72 premiers octets : au-delà, on refuse plutôt que de tronquer en silence
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ApiException(400, "VALIDATION_ERROR", "Password must not exceed 72 bytes");
        }
    }

    private void validateTimeZone(String timeZone) {
        try {
            ZoneId.of(timeZone);
        } catch (DateTimeException e) {
            throw new ApiException(400, "VALIDATION_ERROR", "Unknown time zone");
        }
    }

    private Identity buildIdentity(AppUser user, ContactDto contact, ContactDto guardianContact) {
        Identity identity = new Identity();
        identity.setUser(user);
        if (contact.type() == ContactType.EMAIL) {
            identity.setEmail(ContactValidator.normalized(contact));
        } else {
            identity.setPhone(ContactValidator.normalized(contact));
        }
        if (guardianContact != null) {
            identity.setGuardianContact(ContactValidator.normalized(guardianContact));
        }
        return identity;
    }

    private void sendGuardianLink(AppUser user, ContactDto guardianContact) {
        String token = TokenHasher.newToken();
        ConsentChannel channel = guardianContact.type() == ContactType.EMAIL
                ? ConsentChannel.EMAIL_LINK : ConsentChannel.SMS_LINK;
        LocalDateTime expiresAt = LocalDateTime.now(clock).plusHours(guardianTokenTtlHours);
        guardianTokens.save(GuardianToken.create(user, TokenHasher.sha256Hex(token), channel, expiresAt));
        notifier.sendConfirmationLink(user.getId(), guardianContact.type(), token);
    }
}
