package com.github.triikow.farms.preset;

import java.util.*;

public final class PresetCatalog {

    private final Map<String, PresetDefinition> presets;

    public PresetCatalog(Map<String, PresetDefinition> presets) {
        this.presets = Collections.unmodifiableMap(new LinkedHashMap<>(presets));
    }

    public List<PresetDefinition> list() {
        return List.copyOf(presets.values());
    }

    public PresetDefinition get(String key) {
        return presets.get(key);
    }

    public int size() {
        return presets.size();
    }
}
