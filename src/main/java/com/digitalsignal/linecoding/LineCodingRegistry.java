package com.digitalsignal.linecoding;

import com.digitalsignal.scrambling.B8zsStrategy;
import com.digitalsignal.scrambling.Hdb3Strategy;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Central registry for all supported line-coding strategies.
 *
 * New strategies can be registered without modifying SignalCodec.
 */
public final class LineCodingRegistry {

    private static final Map<String, Supplier<LineCodingStrategy>> FACTORIES;

    static {
        Map<String, Supplier<LineCodingStrategy>> factories =
                new LinkedHashMap<>();

        factories.put("NRZ-L", NrzLStrategy::new);
        factories.put("NRZ-I", NrzIStrategy::new);
        factories.put("Manchester", ManchesterStrategy::new);
        factories.put(
                "Differential Manchester",
                DifferentialManchesterStrategy::new
        );
        factories.put("AMI", AmiStrategy::new);
        factories.put("B8ZS", B8zsStrategy::new);
        factories.put("HDB3", Hdb3Strategy::new);
        factories.put("RZ", RzStrategy::new);

        FACTORIES = Collections.unmodifiableMap(factories);
    }

    private LineCodingRegistry() {
    }

    /**
     * Finds and creates a fresh strategy instance.
     *
     * @param name registered scheme name
     * @return strategy if the scheme exists
     */
    public static Optional<LineCodingStrategy> find(String name) {
        if (name == null) {
            return Optional.empty();
        }

        Supplier<LineCodingStrategy> factory = FACTORIES.get(name);

        return factory == null
                ? Optional.empty()
                : Optional.of(factory.get());
    }

    /**
     * Returns supported scheme names in registration order.
     */
    public static Set<String> schemes() {
        return FACTORIES.keySet();
    }
}
