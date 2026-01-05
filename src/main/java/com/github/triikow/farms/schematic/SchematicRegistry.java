package com.github.triikow.farms.schematic;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.*;

public final class SchematicRegistry {
    private final Map<String, SchematicOption> options = new LinkedHashMap<>();

    public void reload(org.bukkit.configuration.file.FileConfiguration config, File dataFolder, java.util.logging.Logger logger) {
        options.clear();

        ConfigurationSection section = config.getConfigurationSection("schematics");
        if (section == null) {
            logger.warning("No 'schematics' section found in config.yml");
            return;
        }

        File dir = new File(dataFolder, "schematics");

        for (String key : section.getKeys(false)) {
            String base = "schematics." + key + ".";
            String displayName = config.getString(base + "display-name", key);
            String itemName = config.getString(base + "item", "PAPER");
            String fileName = config.getString(base + "file", "");

            Material mat = Material.matchMaterial(itemName);
            if (mat == null) {
                logger.warning("Invalid material for schematic '" + key + "': " + itemName);
                continue;
            }
            if (fileName.isBlank()) {
                logger.warning("Missing file for schematic '" + key + "'");
                continue;
            }

            File f = new File(dir, fileName);
            if (!f.exists() || !f.isFile()) {
                logger.warning("Missing schematic file for '" + key + "': " + f.getPath());
                continue;
            }

            options.put(key, new SchematicOption(key, displayName, mat, fileName));
        }
    }

    public List<SchematicOption> list() { return List.copyOf(options.values()); }

    public SchematicOption get(String key) { return options.get(key); }
}
