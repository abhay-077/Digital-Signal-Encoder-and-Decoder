package com.digitalsignal.analog;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnalogCodecTest {

    @Test
    void pcmProducesFixedWidthCodesAndDecodes() {
        List<Double> samples = List.of(-1.0, -0.25, 0.5, 1.0);

        var encoded = AnalogCodec.encodePcm(samples, 4);

        assertEquals(samples.size() * 4, encoded.bits().length());

        var decoded = AnalogCodec.decodePcm(
                encoded.bits(),
                4,
                encoded.min(),
                encoded.max()
        );

        assertEquals(samples.size(), decoded.size());
        assertEquals(-1.0, decoded.get(0), 1e-9);
        assertEquals(1.0, decoded.get(3), 1e-9);
    }

    @Test
    void pcmConstantSignalIsSupported() {
        var encoded = AnalogCodec.encodePcm(
                List.of(2.0, 2.0),
                3
        );

        assertEquals("000000", encoded.bits());

        assertEquals(
                List.of(2.0, 2.0),
                AnalogCodec.decodePcm(encoded.bits(), 3, 2.0, 2.0)
        );
    }

    @Test
    void pcmSupportsMinimumAndMaximumWidth() {
        assertEquals(
                2,
                AnalogCodec.encodePcm(List.of(0.0, 1.0), 2)
                        .bitsPerSample()
        );

        assertEquals(
                16,
                AnalogCodec.encodePcm(List.of(0.0, 1.0), 16)
                        .bitsPerSample()
        );
    }

    @Test
    void pcmRejectsInvalidWidthAndRange() {
        assertThrows(
                IllegalArgumentException.class,
                () -> AnalogCodec.encodePcm(List.of(1.0), 1)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> AnalogCodec.decodePcm("101", 2, 0.0, 1.0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> AnalogCodec.decodePcm("10", 2, 2.0, 1.0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> AnalogCodec.decodePcm("10", 2, -Double.MAX_VALUE, Double.MAX_VALUE)
        );
    }

    @Test
    void pcmRejectsUnrepresentableSampleRange() {
        assertThrows(
                IllegalArgumentException.class,
                () -> AnalogCodec.encodePcm(
                        List.of(-Double.MAX_VALUE, Double.MAX_VALUE),
                        4
                )
        );
    }

    @Test
    void deltaModulationDecodesToStaircase() {
        var encoded = AnalogCodec.encodeDelta(
                List.of(0.1, 0.8, 0.2, -0.5),
                0.25,
                0.0
        );

        var decoded = AnalogCodec.decodeDelta(
                encoded.bits(),
                0.25,
                0.0
        );

        assertEquals(encoded.bits(), decoded.bits());
        assertEquals(encoded.samples(), decoded.staircase());
        assertEquals(4, decoded.staircase().size());
    }

    @Test
    void deltaRejectsInvalidParametersAndBitstreams() {
        assertThrows(
                IllegalArgumentException.class,
                () -> AnalogCodec.encodeDelta(List.of(1.0), 0.0, 0.0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> AnalogCodec.decodeDelta("10x1", 0.25, 0.0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> AnalogCodec.decodeDelta("1", -0.25, 0.0)
        );
    }

    @Test
    void deltaRejectsNumericOverflow() {
        assertThrows(
                IllegalArgumentException.class,
                () -> AnalogCodec.encodeDelta(
                        List.of(Double.MAX_VALUE),
                        Double.MAX_VALUE,
                        Double.MAX_VALUE
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> AnalogCodec.decodeDelta(
                        "1",
                        Double.MAX_VALUE,
                        Double.MAX_VALUE
                )
        );
    }

    @Test
    void rejectsBadAnalogInput() {
        assertThrows(
                IllegalArgumentException.class,
                () -> AnalogCodec.parseSamples("1,foo,3")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> AnalogCodec.parseSamples("1,NaN,3")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> AnalogCodec.parseSamples("")
        );
    }
}
