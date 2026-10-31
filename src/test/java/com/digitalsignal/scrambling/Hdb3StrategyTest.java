package com.digitalsignal.scrambling;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Hdb3StrategyTest {

    @Test
    void encodesFourZerosAndRoundTrips() {
        Hdb3Strategy strategy = new Hdb3Strategy();

        for (String bits : List.of(
                "0000",
                "10000",
                "110000",
                "100000000",
                "1010000100000100000"
        )) {
            assertEquals(bits, strategy.decode(strategy.encode(bits)),
                    "HDB3 round-trip failed for " + bits);
        }
    }

    @Test
    void handlesLeadingFourZeros() {
        Hdb3Strategy strategy = new Hdb3Strategy();

        String bits = "0000";

        assertEquals(bits, strategy.decode(strategy.encode(bits)));
    }

    @Test
    void handlesConsecutiveFourZeroBlocks() {
        Hdb3Strategy strategy = new Hdb3Strategy();

        for (String bits : List.of(
                "00000000",
                "100000000",
                "1100000000",
                "101000000000"
        )) {
            assertEquals(bits, strategy.decode(strategy.encode(bits)),
                    "Consecutive HDB3 substitution failed for " + bits);
        }
    }

    @Test
    void testsBothHdb3SubstitutionPatterns() {
        Hdb3Strategy strategy = new Hdb3Strategy();

        // Odd number of normal pulses: 000V.
        List<Double> oddPattern = strategy.encode("10000");

        // Even number of normal pulses: B00V.
        List<Double> evenPattern = strategy.encode("110000");

        assertEquals(List.of(1.0, 0.0, 0.0, 0.0, 1.0),
                collapse(oddPattern));

        assertEquals(List.of(1.0, -1.0, 1.0, 0.0, 0.0, 1.0),
                collapse(evenPattern));
    }

    @Test
    void producesTwoSamplesPerBit() {
        Hdb3Strategy strategy = new Hdb3Strategy();

        String bits = "100000000";

        assertEquals(bits.length() * 2, strategy.encode(bits).size());
    }

    @Test
    void rejectsInvalidSignalValues() {
        Hdb3Strategy strategy = new Hdb3Strategy();

        assertThrows(IllegalArgumentException.class,
                () -> strategy.decode(List.of(0.5, 0.5)));

        assertThrows(IllegalArgumentException.class,
                () -> strategy.decode(List.of(1.0, -1.0)));

        assertThrows(IllegalArgumentException.class,
                () -> strategy.decode(List.of(0.0)));
    }

    @Test
    void rejectsInvalidBitStreams() {
        Hdb3Strategy strategy = new Hdb3Strategy();

        assertThrows(IllegalArgumentException.class,
                () -> strategy.encode("10201"));

        assertThrows(IllegalArgumentException.class,
                () -> strategy.encode(""));
    }

    private List<Double> collapse(List<Double> signal) {
        java.util.ArrayList<Double> result = new java.util.ArrayList<>();

        for (int i = 0; i < signal.size(); i += 2) {
            assertEquals(signal.get(i), signal.get(i + 1));
            result.add(signal.get(i));
        }

        return result;
    }
}
