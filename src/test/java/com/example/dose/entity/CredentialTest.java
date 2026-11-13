package com.example.dose.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CredentialTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 4, 10, 0);

    @Test
    void locksOnTheFifthFailure() {
        Credential c = Credential.of(new AppUser(), "hash");
        for (int i = 1; i <= 4; i++) {
            c.registerFailure(5, NOW.plusMinutes(15));
            assertFalse(c.isLocked(NOW), "pas verrouillé après " + i + " échec(s)");
        }
        c.registerFailure(5, NOW.plusMinutes(15));
        assertTrue(c.isLocked(NOW));
        assertEquals(0, c.getFailedAttempts());
    }

    @Test
    void lockEndsAtTheLimit() {
        Credential c = Credential.of(new AppUser(), "hash");
        for (int i = 0; i < 5; i++) {
            c.registerFailure(5, NOW.plusMinutes(15));
        }
        assertTrue(c.isLocked(NOW.plusMinutes(14)));
        assertFalse(c.isLocked(NOW.plusMinutes(15)));
    }

    @Test
    void resetClearsFailuresAndLock() {
        Credential c = Credential.of(new AppUser(), "hash");
        for (int i = 0; i < 5; i++) {
            c.registerFailure(5, NOW.plusMinutes(15));
        }
        c.resetFailures();
        assertFalse(c.isLocked(NOW));
        assertNull(c.getLockedUntil());
    }
}
