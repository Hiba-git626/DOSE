package com.example.dose.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class AgeBandTest {

    @ParameterizedTest
    @CsvSource({"10,AGE_10_15", "15,AGE_10_15", "16,AGE_16_17", "17,AGE_16_17", "18,AGE_18_19", "19,AGE_18_19"})
    void mapsAgeToBand(int age, AgeBand expected) {
        assertEquals(expected, AgeBand.fromAge(age));
    }

    @Test
    void rejectsAgesOutsideTheRange() {
        assertThrows(IllegalArgumentException.class, () -> AgeBand.fromAge(9));
        assertThrows(IllegalArgumentException.class, () -> AgeBand.fromAge(20));
    }
}
