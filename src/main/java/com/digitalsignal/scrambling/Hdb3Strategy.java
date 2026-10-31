package com.digitalsignal.scrambling;

import com.digitalsignal.linecoding.LineCodingStrategy;

import java.util.ArrayList;
import java.util.List;

public final class Hdb3Strategy implements LineCodingStrategy {

    private static final double INITIAL_POLARITY = -1.0;

    @Override
    public List<Double> encode(String bits) {
        validateBits(bits);

        List<Double> pulses = new ArrayList<>(bits.length());
        double last = INITIAL_POLARITY;
        int pulseCount = 0;
        int i = 0;

        while (i < bits.length()) {
            if (bits.startsWith("0000", i)) {
                if (pulseCount % 2 == 0) {
                    // B00V: balancing pulse followed by a violation.
                    double balancing = -last;

                    pulses.add(balancing);
                    pulses.add(0.0);
                    pulses.add(0.0);
                    pulses.add(balancing);

                    last = balancing;
                } else {
                    // 000V: violation repeats the previous pulse polarity.
                    pulses.add(0.0);
                    pulses.add(0.0);
                    pulses.add(0.0);
                    pulses.add(last);
                }

                pulseCount = 0;
                i += 4;
                continue;
            }

            double pulse = 0.0;

            if (bits.charAt(i) == '1') {
                pulse = -last;
                last = pulse;
                pulseCount++;
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
            if (isViolationPattern(pulses, i, last)) {
                bits.append("0000");
                last = pulses[i + 3];
                i += 4;
                continue;
            }

            double pulse = pulses[i++];

            if (pulse != 0.0) {
                if (pulse == last) {
                    throw new IllegalArgumentException(
                            "Invalid AMI polarity outside HDB3 substitution."
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

    private static boolean isViolationPattern(
            double[] pulses, int index, double last) {

        if (index + 4 > pulses.length) {
            return false;
        }

        // 000V: violation has the same polarity as the previous pulse.
        boolean zeroZeroZeroViolation =
                pulses[index] == 0.0
                        && pulses[index + 1] == 0.0
                        && pulses[index + 2] == 0.0
                        && pulses[index + 3] != 0.0
                        && pulses[index + 3] == last;

        // B00V: B has normal AMI polarity; V repeats B.
        boolean balancingZeroZeroViolation =
                pulses[index] == -last
                        && pulses[index] != 0.0
                        && pulses[index + 1] == 0.0
                        && pulses[index + 2] == 0.0
                        && pulses[index + 3] == pulses[index];

        return zeroZeroZeroViolation || balancingZeroZeroViolation;
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
            throw new IllegalArgumentException("Invalid HDB3 signal length.");
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
                throw new IllegalArgumentException("Invalid HDB3 pulse.");
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
