package com.github.triikow.farms.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class UpgradesRegistry {

    public record UpgradeDefinition(
            String key,
            String displayName,
            int maxLevel,
            java.util.List<String> description
    ) { }

    private final Map<String, UpgradeDefinition> upgrades;

    public UpgradesRegistry(Map<String, UpgradeDefinition> upgrades) {
        this.upgrades = Collections.unmodifiableMap(new LinkedHashMap<>(upgrades));
    }

    public Map<String, UpgradeDefinition> all() {
        return upgrades;
    }

    public int size() {
        return upgrades.size();
    }
}
