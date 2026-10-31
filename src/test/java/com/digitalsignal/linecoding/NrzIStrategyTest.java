package com.digitalsignal.linecoding;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NrzIStrategyTest {

    private final NrzIStrategy strategy = new NrzIStrategy();

    @Test
    void encodesByInvertingLevelForOnes() {
        assertEquals(
                List.of(
                        1.0, 1.0,
                        1.0, 1.0,
                        -1.0, -1.0,
                        1.0, 1.0
                ),
                strategy.encode("1011")
        );
    }

    @Test
    void zeroDoesNotCauseTransitionFromInitialLevel() {
        assertEquals(
                List.of(
                        -1.0, -1.0,
                        -1.0, -1.0,
                        1.0, 1.0,
                        1.0, 1.0,
                        -1.0, -1.0
                ),
                strategy.encode("00101")
        );
    }

    @Test
    void decodesSignalCorrectly() {
        assertEquals(
                "1011",
                strategy.decode(strategy.encode("1011"))
        );
    }

    @Test
    void decodesSequenceBeginningWithZero() {
        assertEquals(
                "00101",
                strategy.decode(strategy.encode("00101"))
        );
    }

    @Test
    void supportsSingleBitZeroAndOne() {
        assertEquals("0", strategy.decode(strategy.encode("0")));
        assertEquals("1", strategy.decode(strategy.encode("1")));
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
