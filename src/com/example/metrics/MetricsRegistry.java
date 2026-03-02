package com.example.metrics;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Global metrics registry implemented as a lazy, thread-safe singleton.
 */
public class MetricsRegistry implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static volatile MetricsRegistry INSTANCE;
    private static boolean constructorCalled = false;

    private final Map<String, Long> counters = new HashMap<>();

    private MetricsRegistry() {
        synchronized (MetricsRegistry.class) {
            if (constructorCalled) {
                throw new IllegalStateException("MetricsRegistry singleton already initialized");
            }
            constructorCalled = true;
        }
    }

    public static MetricsRegistry getInstance() {
        MetricsRegistry local = INSTANCE;
        if (local == null) {
            synchronized (MetricsRegistry.class) {
                local = INSTANCE;
                if (local == null) {
                    local = new MetricsRegistry();
                    INSTANCE = local;
                }
            }
        }
        return local;
    }

    public synchronized void setCount(String key, long value) {
        counters.put(key, value);
    }

    public synchronized void increment(String key) {
        counters.put(key, getCount(key) + 1);
    }

    public synchronized long getCount(String key) {
        return counters.getOrDefault(key, 0L);
    }

    public synchronized Map<String, Long> getAll() {
        return Collections.unmodifiableMap(new HashMap<>(counters));
    }

    @Serial
    private Object readResolve() {
        return getInstance();
    }
}
