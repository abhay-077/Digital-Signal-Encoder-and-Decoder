package com.digitalsignal.linecoding;

import java.util.ArrayList;
import java.util.List;

public final class NrzIStrategy implements LineCodingStrategy {

    private static final double INITIAL_LEVEL = -1.0;

    @Override
    public List<Double> encode(String bits) {
        List<Double> signal = new ArrayList<>(bits.length() * 2);
        double level = INITIAL_LEVEL;

        for (char bit : bits.toCharArray()) {
            if (bit == '1') {
                level = -level;
            }

            signal.add(level);
            signal.add(level);
        }

        return signal;
    }

    @Override
    public String decode(List<Double> signal) {
        validate(signal);

        StringBuilder bits = new StringBuilder(signal.size() / 2);
        double previousLevel = INITIAL_LEVEL;

        for (int i = 0; i < signal.size(); i += 2) {
            double first = signal.get(i);
            double second = signal.get(i + 1);

            if (first != second || Math.abs(first) != 1.0) {
                throw new IllegalArgumentException("Invalid NRZ-I signal.");
            }

            bits.append(first != previousLevel ? '1' : '0');
            previousLevel = first;
        }

        return bits.toString();
    }

    private static void validate(List<Double> signal) {
        if (signal == null || signal.isEmpty() || signal.size() % 2 != 0) {
            throw new IllegalArgumentException("Invalid signal length.");
        }
    }
}
