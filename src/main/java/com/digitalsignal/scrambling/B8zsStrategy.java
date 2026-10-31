package com.digitalsignal.scrambling;

import com.digitalsignal.linecoding.LineCodingStrategy;

import java.util.ArrayList;
import java.util.List;

public final class B8zsStrategy implements LineCodingStrategy {

    private static final double INITIAL_POLARITY = -1.0;
    private static final String ZERO_PATTERN = "00000000";

    @Override
    public List<Double> encode(String bits) {
        validateBits(bits);

        List<Double> pulses = new ArrayList<>(bits.length());
        double last = INITIAL_POLARITY;
        int i = 0;

        while (i < bits.length()) {
            if (bits.startsWith(ZERO_PATTERN, i)) {
                double violation = last;

                double[] pattern = {
                        0.0, 0.0, 0.0, violation,
                        -violation, 0.0, -violation, violation
                };

                for (double pulse : pattern) {
                    pulses.add(pulse);
                }

                last = violation;
                i += 8;
                continue;
            }

            double pulse = 0.0;

            if (bits.charAt(i) == '1') {
                pulse = -last;
                last = pulse;
            }

            pulses.add(pulse);
            i++;
        }

        return expand(pulses);
    }

    @Override
    public String decode(List<Double> signal) {
        double[] pulses = collapse(signal);

        StringBuilder bits = new StringBuilder(pulses.length);
        double last = INITIAL_POLARITY;
        int i = 0;

        while (i < pulses.length) {
            if (isB8zsPattern(pulses, i, last)) {
                bits.append(ZERO_PATTERN);
                last = pulses[i + 7];
                i += 8;
                continue;
            }

            double pulse = pulses[i++];

            if (pulse != 0.0) {
                if (pulse == last) {
                    throw new IllegalArgumentException(
                            "Invalid AMI polarity outside B8ZS substitution."
                    );
                }

                last = pulse;
                bits.append('1');
            } else {
                bits.append('0');
            }
        }

        return bits.toString();
    }

    private static boolean isB8zsPattern(
            double[] pulses, int index, double last) {

        return index + 8 <= pulses.length
                && pulses[index] == 0.0
                && pulses[index + 1] == 0.0
                && pulses[index + 2] == 0.0
                && pulses[index + 3] == last
                && pulses[index + 4] == -last
                && pulses[index + 5] == 0.0
                && pulses[index + 6] == -last
                && pulses[index + 7] == last;
    }

    private static List<Double> expand(List<Double> pulses) {
        List<Double> signal = new ArrayList<>(pulses.size() * 2);

        for (double pulse : pulses) {
            signal.add(pulse);
            signal.add(pulse);
        }

        return signal;
    }

    private static double[] collapse(List<Double> signal) {
        if (signal == null || signal.isEmpty() || signal.size() % 2 != 0) {
            throw new IllegalArgumentException("Invalid B8ZS signal length.");
        }

        double[] pulses = new double[signal.size() / 2];

        for (int i = 0; i < pulses.length; i++) {
            Double first = signal.get(i * 2);
            Double second = signal.get(i * 2 + 1);

            if (first == null || second == null
                    || !Double.isFinite(first)
                    || !Double.isFinite(second)
                    || (first != -1.0 && first != 0.0 && first != 1.0)
                    || !first.equals(second)) {
                throw new IllegalArgumentException("Invalid B8ZS pulse.");
            }

            pulses[i] = first;
        }

        return pulses;
    }

    private static void validateBits(String bits) {
        if (bits == null || bits.isEmpty() || !bits.matches("[01]+")) {
            throw new IllegalArgumentException(
                    "Bit stream must contain only 0 and 1."
            );
        }
    }
}
