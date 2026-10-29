package com.digitalsignal.scrambling;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class B8zsStrategyTest {

    private List<Double> pulses(String bits) {
        List<Double> signal = new B8zsStrategy().encode(bits);
        List<Double> result = new ArrayList<>();

        for (int i = 0; i < signal.size(); i += 2) {
            assertEquals(signal.get(i), signal.get(i + 1),
                    "Each bit must have two equal samples");
            result.add(signal.get(i));
        }

        return result;
    }

    @Test
    void substitutionAfterNegativePulse() {
        assertEquals(
                List.of(1.0, 0.0, 0.0, 0.0, 1.0, -1.0, 0.0, -1.0, 1.0),
                pulses("100000000")
        );
    }

    @Test
    void substitutionAfterPositivePulse() {
        assertEquals(
                List.of(1.0, -1.0, 0.0, 0.0, 0.0, -1.0, 1.0, 0.0, 1.0, -1.0),
                pulses("1100000000")
        );
    }

    @Test
    void roundTripsZeroRunsWithBothPolarities() {
        B8zsStrategy strategy = new B8zsStrategy();

        for (String bits : List.of(
                "100000000", "1100000000",
                "1000000001", "11000000001"
        )) {
            assertEquals(bits, strategy.decode(strategy.encode(bits)),
                    "B8ZS round-trip failed for " + bits);
        }
    }
}
