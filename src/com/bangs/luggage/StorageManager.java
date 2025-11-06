package com.bangs.luggage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StorageManager {
    private final Map<String, String> byLuggage = new HashMap<>();
    private final Logger logger;

    public StorageManager(Logger logger) {
        this.logger = logger;
    }

    public void place(String luggageId, String location) {
        byLuggage.put(luggageId, location);
        // Log as system event for proof
        logger.info(new Equipment("system", "System", Equipment.Type.SYSTEM),
                "Placed luggage " + luggageId + " at " + location);
    }

    public String locate(String luggageId) {
        return byLuggage.get(luggageId);
    }

    public Map<String, String> snapshot() {
        return Collections.unmodifiableMap(byLuggage);
    }
}
