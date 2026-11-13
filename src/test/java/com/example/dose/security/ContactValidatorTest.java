package com.example.dose.service;

import com.example.dose.dto.ContactDto;
import com.example.dose.dto.ContactType;
import com.example.dose.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ContactValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {"nour@example.com", "a.b+c@mail.ma", " nour@example.com "})
    void acceptsValidEmails(String value) {
        assertDoesNotThrow(() -> ContactValidator.validate(new ContactDto(ContactType.EMAIL, value)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"pas-un-mail", "a@b", "a b@c.d", "@c.d", "a@.d"})
    void rejectsInvalidEmails(String value) {
        ApiException e = assertThrows(ApiException.class,
                () -> ContactValidator.validate(new ContactDto(ContactType.EMAIL, value)));
        assertEquals("VALIDATION_ERROR", e.code());
    }

    @ParameterizedTest
    @ValueSource(strings = {"+212600000000", "0600000000", "+212 600 000 000"})
    void acceptsValidPhones(String value) {
        assertDoesNotThrow(() -> ContactValidator.validate(new ContactDto(ContactType.PHONE, value)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"12ab", "123", "+21260000000000000", "06-00-00-00-00"})
    void rejectsInvalidPhones(String value) {
        assertThrows(ApiException.class,
                () -> ContactValidator.validate(new ContactDto(ContactType.PHONE, value)));
    }

    @Test
    void normalizesPhonesAndEmails() {
        assertEquals("+212600000000",
                ContactValidator.normalized(new ContactDto(ContactType.PHONE, " +212 600 000 000 ")));
        assertEquals("nour@example.com",
                ContactValidator.normalized(new ContactDto(ContactType.EMAIL, " nour@example.com ")));
    }
}
