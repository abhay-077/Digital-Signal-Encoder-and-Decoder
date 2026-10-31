package com.digitalsignal.linecoding;

import java.util.List;

/**
 * Common contract for all digital line-coding and scrambling strategies.
 *
 * Each implementation is responsible for its own encoding and decoding
 * algorithm. SignalCodec provides the common validation and dispatch layer.
 */
public interface LineCodingStrategy {

    /**
     * Encodes a binary bit stream into a digital signal.
     *
     * @param bits input binary sequence
     * @return encoded signal samples
     */
    List<Double> encode(String bits);

    /**
     * Decodes a digital signal into its original binary sequence.
     *
     * @param signal encoded signal samples
     * @return decoded binary sequence
     */
    String decode(List<Double> signal);
}
