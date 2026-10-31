package com.digitalsignal.analog;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides Pulse Code Modulation (PCM) and Delta Modulation (DM)
 * encoding and decoding for sampled analog signals.
 */
public final class AnalogCodec {

    private AnalogCodec() {
    }

    public record PcmResult(
            List<Double> samples,
            String bits,
            int bitsPerSample,
            double min,
            double max
    ) {
        public PcmResult {
            samples = List.copyOf(samples);
        }
    }

    public record DmResult(
            List<Double> samples,
            String bits,
            double stepSize,
            double initialLevel
    ) {
        public DmResult {
            samples = List.copyOf(samples);
        }
    }

    public record DmDecoded(List<Double> staircase, String bits) {
        public DmDecoded {
            staircase = List.copyOf(staircase);
        }
    }

    /**
     * Encodes samples using uniform PCM quantization.
     * Each sample is represented by a fixed-width unsigned code.
     */
    public static PcmResult encodePcm(
            List<Double> samples,
            int bitsPerSample
    ) {
        validateSamples(samples);
        validatePcmWidth(bitsPerSample);

        double min = samples.stream()
                .mapToDouble(Double::doubleValue)
                .min()
                .orElseThrow();

        double max = samples.stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElseThrow();

        double range = max - min;

        if (!Double.isFinite(range)) {
            throw new IllegalArgumentException(
                    "PCM sample range is too large to represent."
            );
        }

        int levels = 1 << bitsPerSample;

        StringBuilder bits =
                new StringBuilder(samples.size() * bitsPerSample);

        List<Double> reconstructed =
                new ArrayList<>(samples.size());

        for (double sample : samples) {
            int code;

            if (range == 0.0) {
                code = 0;
            } else {
                double normalized = (sample - min) / range;

                code = (int) Math.round(
                        normalized * (levels - 1)
                );
            }

            code = Math.max(0, Math.min(levels - 1, code));

            appendFixedWidth(bits, code, bitsPerSample);

            double value = range == 0.0
                    ? min
                    : min + range * code / (levels - 1);

            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException(
                        "PCM reconstruction produced an invalid value."
                );
            }

            reconstructed.add(value);
        }

        return new PcmResult(
                reconstructed,
                bits.toString(),
                bitsPerSample,
                min,
                max
        );
    }

    /**
     * Decodes PCM bits using the original quantization range.
     */
    public static List<Double> decodePcm(
            String bits,
            int bitsPerSample,
            double min,
            double max
    ) {
        validateBits(bits);
        validatePcmWidth(bitsPerSample);

        if (bits.length() % bitsPerSample != 0) {
            throw new IllegalArgumentException(
                    "PCM bit stream length must be divisible by bits per sample."
            );
        }

        if (!Double.isFinite(min)
                || !Double.isFinite(max)
                || min > max
                || !Double.isFinite(max - min)) {

            throw new IllegalArgumentException(
                    "Invalid PCM quantization range."
            );
        }

        int levels = 1 << bitsPerSample;
        double range = max - min;

        List<Double> result =
                new ArrayList<>(bits.length() / bitsPerSample);

        for (int i = 0; i < bits.length(); i += bitsPerSample) {
            int code = Integer.parseInt(
                    bits.substring(i, i + bitsPerSample),
                    2
            );

            double value = range == 0.0
                    ? min
                    : min + range * code / (levels - 1);

            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException(
                        "PCM decoding produced an invalid value."
                );
            }

            result.add(value);
        }

        return List.copyOf(result);
    }

    /**
     * First-order delta modulation.
     * Each input sample is compared with the previous reconstructed level.
     */
    public static DmResult encodeDelta(
            List<Double> samples,
            double stepSize,
            double initialLevel
    ) {
        validateSamples(samples);
        validateDeltaParameters(stepSize, initialLevel);

        StringBuilder bits = new StringBuilder(samples.size());
        List<Double> reconstructed =
                new ArrayList<>(samples.size());

        double level = initialLevel;

        for (double sample : samples) {
            double nextLevel = sample >= level
                    ? level + stepSize
                    : level - stepSize;

            validateLevel(nextLevel);

            bits.append(sample >= level ? '1' : '0');
            level = nextLevel;
            reconstructed.add(level);
        }

        return new DmResult(
                reconstructed,
                bits.toString(),
                stepSize,
                initialLevel
        );
    }

    /**
     * Reconstructs the staircase signal from a DM bitstream.
     */
    public static DmDecoded decodeDelta(
            String bits,
            double stepSize,
            double initialLevel
    ) {
        validateBits(bits);
        validateDeltaParameters(stepSize, initialLevel);

        List<Double> staircase = new ArrayList<>(bits.length());
        double level = initialLevel;

        for (char bit : bits.toCharArray()) {
            double nextLevel = bit == '1'
                    ? level + stepSize
                    : level - stepSize;

            validateLevel(nextLevel);

            level = nextLevel;
            staircase.add(level);
        }

        return new DmDecoded(staircase, bits);
    }

    /**
     * Parses comma-separated or whitespace-separated analog samples.
     */
    public static List<Double> parseSamples(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(
                    "Enter comma-separated analog samples."
            );
        }

        String[] parts = text.trim().split("[,\\s]+");
        List<Double> values = new ArrayList<>(parts.length);

        try {
            for (String part : parts) {
                double value = Double.parseDouble(part);

                if (!Double.isFinite(value)) {
                    throw new NumberFormatException();
                }

                values.add(value);
            }
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Samples must be finite numbers separated by commas or spaces."
            );
        }

        validateSamples(values);
        return List.copyOf(values);
    }

    private static void appendFixedWidth(
            StringBuilder builder,
            int value,
            int width
    ) {
        for (int bit = width - 1; bit >= 0; bit--) {
            builder.append(
                    ((value >>> bit) & 1) == 1 ? '1' : '0'
            );
        }
    }

    private static void validateBits(String bits) {
        if (bits == null
                || bits.isEmpty()
                || !bits.matches("[01]+")) {

            throw new IllegalArgumentException(
                    "Bit stream must contain only 0s and 1s."
            );
        }
    }

    private static void validateSamples(List<Double> samples) {
        if (samples == null || samples.isEmpty()) {
            throw new IllegalArgumentException(
                    "Provide at least one analog sample."
            );
        }

        for (Double value : samples) {
            if (value == null || !Double.isFinite(value)) {
                throw new IllegalArgumentException(
                        "Analog samples must be finite numbers."
                );
            }
        }
    }

    private static void validatePcmWidth(int bitsPerSample) {
        if (bitsPerSample < 2 || bitsPerSample > 16) {
            throw new IllegalArgumentException(
                    "PCM bits per sample must be between 2 and 16."
            );
        }
    }

    private static void validateDeltaParameters(
            double stepSize,
            double initialLevel
    ) {
        if (!Double.isFinite(stepSize) || stepSize <= 0.0) {
            throw new IllegalArgumentException(
                    "Delta step size must be positive and finite."
            );
        }

        if (!Double.isFinite(initialLevel)) {
            throw new IllegalArgumentException(
                    "Initial level must be finite."
            );
        }
    }

    private static void validateLevel(double level) {
        if (!Double.isFinite(level)) {
            throw new IllegalArgumentException(
                    "Delta modulation level overflowed."
            );
        }
    }
}
