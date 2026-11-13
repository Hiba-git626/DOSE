package com.example.dose.service;

import com.example.dose.dto.ContactDto;
import com.example.dose.exception.ApiException;

import java.util.regex.Pattern;

/** Vérifie le format d'un e-mail ou d'un numéro de téléphone. */
public final class ContactValidator {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9]{8,15}$");

    private ContactValidator() {
    }

    public static void validate(ContactDto contact) {
        String value = normalized(contact);
        boolean valid = switch (contact.type()) {
            case EMAIL -> EMAIL.matcher(value).matches();
            case PHONE -> PHONE.matcher(value).matches();
        };
        if (!valid) {
            throw new ApiException(400, "VALIDATION_ERROR", "Invalid " + contact.type() + " format");
        }
    }

    /** E-mail : sans espaces autour. Téléphone : sans aucun espace. */
    public static String normalized(ContactDto contact) {
        String value = contact.value().trim();
        return switch (contact.type()) {
            case EMAIL -> value;
            case PHONE -> value.replace(" ", "");
        };
    }
}
