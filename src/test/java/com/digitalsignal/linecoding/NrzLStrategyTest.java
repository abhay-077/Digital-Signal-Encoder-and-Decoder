package com.digitalsignal.linecoding;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NrzLStrategyTest {

    private final NrzLStrategy strategy = new NrzLStrategy();

    @Test
    void encodesBitsUsingPositiveAndNegativeLevels() {
        assertEquals(
                List.of(1.0, 1.0, -1.0, -1.0, 1.0, 1.0),
                strategy.encode("101")
        );
    }

    @Test
    void decodesSignalCorrectly() {
        assertEquals(
                "101",
                strategy.decode(List.of(1.0, 1.0, -1.0, -1.0, 1.0, 1.0))
        );
    }

    @Test
    void rejectsInvalidSignals() {
        assertThrows(IllegalArgumentException.class,
                () -> strategy.decode(null));
        assertThrows(IllegalArgumentException.class,
                () -> strategy.decode(List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> strategy.decode(List.of(1.0)));
        assertThrows(IllegalArgumentException.class,
                () -> strategy.decode(List.of(1.0, -1.0)));
    }
}
