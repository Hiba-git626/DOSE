package com.example.dose.service;

import com.example.dose.dto.ContactType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** Version de développement : écrit le jeton du tuteur dans la console au lieu d'envoyer un message. */
@Component
public class LoggingGuardianNotifier implements GuardianNotifier {

    private static final Logger log = LoggerFactory.getLogger(LoggingGuardianNotifier.class);

    @Override
    public void sendConfirmationLink(UUID userId, ContactType contactType, String token) {
        log.info("[DEV] Confirmation tuteur pour l'utilisateur {} (canal {}). "
                + "Jeton à envoyer à POST /api/v1/guardian/confirm : {}", userId, contactType, token);
    }
}
