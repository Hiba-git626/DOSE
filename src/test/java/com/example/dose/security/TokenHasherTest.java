package com.example.dose.security;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TokenHasherTest {

    @Test
    void tokenHas43UrlSafeCharacters() {
        String token = TokenHasher.newToken();
        assertEquals(43, token.length());               // 32 octets en Base64 URL, sans padding
        assertTrue(token.matches("[A-Za-z0-9_-]+"));
    }

    @Test
    void tokensAreUnique() {
        Set<String> tokens = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            tokens.add(TokenHasher.newToken());
        }
        assertEquals(1000, tokens.size());
    }

    @Test
    void sha256MatchesKnownVector() {
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
                TokenHasher.sha256Hex("abc"));
    }

    @Test
    void hashIsDeterministicAndHas64Characters() {
        String hash = TokenHasher.sha256Hex("mon-jeton");
        assertEquals(hash, TokenHasher.sha256Hex("mon-jeton"));
        assertEquals(64, hash.length());
        assertNotEquals(hash, TokenHasher.sha256Hex("mon-jeton2"));
    }
}
