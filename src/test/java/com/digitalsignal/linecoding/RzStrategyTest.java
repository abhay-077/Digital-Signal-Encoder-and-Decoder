
package com.digitalsignal.linecoding;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RzStrategyTest {

    private final RzStrategy strategy = new RzStrategy();

    @Test
    void encodesBitsWithReturnToZero() {
        assertEquals(
                List.of(1.0, 0.0, -1.0, 0.0, 1.0, 0.0),
                strategy.encode("101")
        );
    }

    @Test
    void decodesSignalCorrectly() {
        assertEquals(
                "101",
                strategy.decode(List.of(
                        1.0, 0.0,
                        -1.0, 0.0,
                        1.0, 0.0
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
                () -> strategy.decode(List.of(1.0, 1.0)));
    }
}
