
package com.digitalsignal.linecoding;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NrzIStrategyTest {
    private final NrzIStrategy strategy = new NrzIStrategy();

    @Test
    void encodesByInvertingLevelForSubsequentOnes() {
        assertEquals(
                List.of(1.0, 1.0, 1.0, 1.0,
                        -1.0, -1.0, 1.0, 1.0),
                strategy.encode("1011")
        );
    }

    @Test
    void decodesSignalCorrectly() {
        assertEquals(
                "1011",
                strategy.decode(List.of(
                        1.0, 1.0, 1.0, 1.0,
                        -1.0, -1.0, 1.0, 1.0
                ))
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
                () -> strategy.decode(List.of(1.0, 0.0)));
    }
}
