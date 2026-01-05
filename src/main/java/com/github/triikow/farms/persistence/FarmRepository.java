package com.github.triikow.farms.persistence;

import com.github.triikow.farms.domain.Farm;

import java.util.Optional;
import java.util.UUID;

public interface FarmRepository {

    Optional<Farm> findByOwner(UUID ownerId);

    void save(Farm farm);

    int allocateNextIndex();

    int getNextIndex();

    int count();

    void reload();
}
