package com.github.triikow.farms.domain;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public record Farm(
        UUID ownerId,
        int index,
        FarmCenter center,
        String presetKey,
        Instant createdAt,
        boolean pasted,
        FarmHome home,
        TeamState team,
        Map<String, Integer> upgrades
) {
    public Farm {
        upgrades = Collections.unmodifiableMap(new LinkedHashMap<>(upgrades));
        if (team == null) team = TeamState.empty();
        if (createdAt == null) createdAt = Instant.now();
    }

    public Farm markPasted() {
        return new Farm(ownerId, index, center, presetKey, createdAt, true, home, team, upgrades);
    }

    public Farm withHome(FarmHome newHome) {
        return new Farm(ownerId, index, center, presetKey, createdAt, pasted, newHome, team, upgrades);
    }
}
