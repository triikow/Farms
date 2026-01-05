package com.github.triikow.farms.schematic;

import org.bukkit.Material;

public record SchematicOption(
        String key,
        String displayName,
        Material item,
        String file
) {}
