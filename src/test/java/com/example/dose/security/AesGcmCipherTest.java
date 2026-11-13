package com.example.dose.security;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class AesGcmCipherTest {

    private static AesGcmCipher cipher(int seed) {
        byte[] key = new byte[32];
        for (int i = 0; i < key.length; i++) {
            key[i] = (byte) (i + seed);
        }
        return new AesGcmCipher(key);
    }

    @Test
    void roundTrip() {
        AesGcmCipher c = cipher(1);
        assertEquals("Yasmine El Idrissi", c.decrypt(c.encrypt("Yasmine El Idrissi"))); //on chiffre puis on déchiffre et on doit retrouver la même valeur
        assertEquals("نور@example.com", c.decrypt(c.encrypt("نور@example.com"))); // on s'assure qu'on peut mélanger les langues
    }

    @Test
    void sameTextGivesDifferentCiphertexts() { //assertNotEquals vérifie l’inverse : on chiffre deux fois le même texte et les résultats doivent être différents. C’est la preuve que le nombre aléatoire (IV) change à chaque fois, ce qui empêche de deviner que deux personnes ont le même e-mail.
        AesGcmCipher c = cipher(1);
        assertNotEquals(c.encrypt("a@b.ma"), c.encrypt("a@b.ma"));
    }

    @Test
    void ciphertextDoesNotContainThePlainText() {
        AesGcmCipher c = cipher(1);
        String encrypted = c.encrypt("secret@example.com");
        assertFalse(new String(Base64.getDecoder().decode(encrypted)).contains("secret")); //vérifie que ce texte ne contient pas le mot secret. Un chiffrement qui laisserait le texte original visible serait inutile.
    }

    @Test
    void wrongKeyFails() {
        String encrypted = cipher(1).encrypt("hello");
        assertThrows(IllegalStateException.class, () -> cipher(2).decrypt(encrypted)); //On chiffre avec la clé A, puis on essaie de déchiffrer avec la clé B. le déchiffrement ne doit pas se passer
    }

    @Test
    void tamperedDataFails() {
        AesGcmCipher c = cipher(1); //on décode le texte chiffré en octets
        byte[] raw = Base64.getDecoder().decode(c.encrypt("hello")); //raw[raw.length - 1] ^= 1 change un seul bit du dernier octet (^= est un XOR : il inverse le bit de poids faible)
        raw[raw.length - 1] ^= 1;
        String tampered = Base64.getEncoder().encodeToString(raw);
        assertThrows(IllegalStateException.class, () -> c.decrypt(tampered)); //on ré-encode en Base64, on vérifie que le déchiffrement échoue
    }

    @Test
    void rejectsBadKeys() {
        assertThrows(IllegalArgumentException.class, () -> new AesGcmCipher(new byte[16]));
        assertThrows(IllegalArgumentException.class, () -> AesGcmCipher.fromBase64(" ")); //Une clé de 16 octets au lieu de 32 doit être refusée, et une clé vide aussi. Ainsi, si tu oublies de définir DOSE_IDENTITY_KEY, l’application s’arrête au démarrage avec un message clair
    }
}
