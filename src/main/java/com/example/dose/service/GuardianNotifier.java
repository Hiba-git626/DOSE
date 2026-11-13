package com.example.dose.service;

import com.example.dose.dto.ContactType;

import java.util.UUID;

/**
 * Envoie au tuteur le lien de confirmation. L'implémentation actuelle écrit simplement le jeton dans
 * la console ; la vraie passerelle e-mail ou SMS (fournie par la porteuse) viendra la remplacer.
 */
public interface GuardianNotifier {

    void sendConfirmationLink(UUID userId, ContactType contactType, String token);
}
