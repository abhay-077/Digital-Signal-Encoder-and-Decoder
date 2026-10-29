package com.digitalsignal.scrambling;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Hdb3StrategyTest {

    @Test
    void encodesFourZerosAndRoundTrips() {
        Hdb3Strategy strategy = new Hdb3Strategy();

        for (String bits : java.util.List.of(
                "0000", "10000", "110000", "100000000",
                "1010000100000100000"
        )) {
            assertEquals(bits, strategy.decode(strategy.encode(bits)),
                    "HDB3 round-trip failed for " + bits);
        }
    }

    @Test
    void producesTwoSamplesPerBit() {
        Hdb3Strategy strategy = new Hdb3Strategy();
        String bits = "100000000";

        assertEquals(bits.length() * 2, strategy.encode(bits).size());
    }
}
