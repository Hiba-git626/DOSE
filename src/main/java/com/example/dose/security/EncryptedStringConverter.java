package com.example.dose.security;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Chiffre une chaîne avant l'écriture en base et la déchiffre à la lecture.
 * La clé vient de la propriété dose.security.identity-key (variable d'environnement DOSE_IDENTITY_KEY).
 */
@Converter
@Component
public class EncryptedStringConverter implements AttributeConverter<String, String> {

    private final AesGcmCipher cipher;

    public EncryptedStringConverter(@Value("${dose.security.identity-key}") String base64Key) {
        this.cipher = AesGcmCipher.fromBase64(base64Key);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute == null ? null : cipher.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData == null ? null : cipher.decrypt(dbData);
    }
}