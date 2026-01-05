package com.github.triikow.farms.config;

import com.github.triikow.farms.preset.PresetCatalog;
import com.github.triikow.farms.preset.PresetDefinition;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public final class PresetsLoader {

    private PresetsLoader() {}

    public static PresetCatalog load(YamlConfiguration yaml, File dataFolder, Logger logger) {
        ConfigurationSection root = yaml.getConfigurationSection("presets");
        if (root == null) {
            logger.warning("presets.yml: missing top-level 'presets' section");
            return new PresetCatalog(Map.of());
        }

        Map<String, PresetDefinition> defs = new LinkedHashMap<>();

        for (String key : root.getKeys(false)) {
            String base = "presets." + key + ".";
            String name = yaml.getString(base + "name", key);
            String iconName = yaml.getString(base + "icon", "PAPER");
            String schematicFile = yaml.getString(base + "schematic_file", "").trim();
            List<String> description = yaml.getStringList(base + "description");

            Material icon = Material.matchMaterial(iconName);
            if (icon == null) {
                logger.warning("presets.yml: invalid icon material for preset '" + key + "': " + iconName);
                continue;
            }
            if (schematicFile.isBlank()) {
                logger.warning("presets.yml: missing schematic_file for preset '" + key + "'");
                continue;
            }

            File schematicOnDisk = new File(dataFolder, schematicFile);
            if (!schematicOnDisk.exists() || !schematicOnDisk.isFile()) {
                logger.warning("presets.yml: schematic_file does not exist for preset '" + key + "': " + schematicFile);
                // Still load the preset (so admins can see it), but paste will fail until file exists.
            }

            defs.put(key, new PresetDefinition(key, name, icon, schematicFile, description));
        }

        return new PresetCatalog(defs);
    }
}
