package com.github.triikow.farms.domain;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public record TeamState(Set<UUID> members) {

    public static TeamState empty() {
        return new TeamState(Set.of());
    }

    public TeamState {
        members = Collections.unmodifiableSet(new LinkedHashSet<>(members));
    }
}
