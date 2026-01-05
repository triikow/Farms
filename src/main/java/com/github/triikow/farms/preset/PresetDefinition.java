package com.github.triikow.farms.preset;

import org.bukkit.Material;

import java.util.List;

public record PresetDefinition(
        String key,
        String name,
        Material icon,
        String schematicFile,
        List<String> description
) { }
