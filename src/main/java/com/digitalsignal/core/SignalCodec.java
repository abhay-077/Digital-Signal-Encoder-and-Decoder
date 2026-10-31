package com.digitalsignal.core;

import com.digitalsignal.linecoding.LineCodingRegistry;
import com.digitalsignal.linecoding.LineCodingStrategy;

import java.util.List;

/**
 * Facade for encoding and decoding digital signals.
 *
 * Validation and strategy selection are centralized here.
 * Individual algorithms remain inside their respective strategy classes.
 */
public final class SignalCodec {

    /**
     * Supported schemes, maintained in registry registration order.
     */
    public static final List<String> SCHEMES =
            List.copyOf(LineCodingRegistry.schemes());

    private SignalCodec() {
    }

    /**
     * Encodes a binary sequence using the selected strategy.
     *
     * @param bits binary input
     * @param type encoding scheme name
     * @return encoded signal
     */
    public static List<Double> encode(String bits, String type) {
        validateBits(bits);

        return findStrategy(type).encode(bits);
    }

    /**
     * Decodes a signal using the selected strategy.
     *
     * @param signal encoded signal
     * @param type encoding scheme name
     * @return decoded binary sequence
     */
    public static String decode(List<Double> signal, String type) {
        validateSignal(signal);

        return findStrategy(type).decode(signal);
    }

    /**
     * Retrieves the requested strategy from the registry.
     */
    private static LineCodingStrategy findStrategy(String type) {
        return LineCodingRegistry.find(type)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported encoding scheme: " + type
                ));
    }

    /**
     * Ensures the input contains only binary digits.
     */
    private static void validateBits(String bits) {
        if (bits == null || bits.isEmpty() || !bits.matches("[01]+")) {
            throw new IllegalArgumentException(
                    "Bit stream must contain only 0 and 1."
            );
        }
    }

    /**
     * Ensures the signal contains two valid samples per bit.
     */
    private static void validateSignal(List<Double> signal) {
        if (signal == null || signal.isEmpty() || signal.size() % 2 != 0) {
            throw new IllegalArgumentException(
                    "Signal must contain two samples per bit."
            );
        }

        for (Double value : signal) {
            if (value == null
                    || !Double.isFinite(value)
                    || (value != -1.0 && value != 0.0 && value != 1.0)) {

                throw new IllegalArgumentException(
                        "Signal samples must be -1, 0, or +1."
                );
            }
        }
    }
}
