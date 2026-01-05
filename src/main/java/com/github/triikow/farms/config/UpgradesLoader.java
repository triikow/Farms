package com.github.triikow.farms.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public final class UpgradesLoader {

    private UpgradesLoader() {}

    public static UpgradesRegistry load(YamlConfiguration yaml, Logger logger) {
        ConfigurationSection root = yaml.getConfigurationSection("upgrades");
        if (root == null) {
            logger.warning("upgrades.yml: missing top-level 'upgrades' section");
            return new UpgradesRegistry(Map.of());
        }

        Map<String, UpgradesRegistry.UpgradeDefinition> defs = new LinkedHashMap<>();

        for (String key : root.getKeys(false)) {
            String base = "upgrades." + key + ".";
            String displayName = yaml.getString(base + "display_name", key);
            int maxLevel = yaml.getInt(base + "max_level", 1);
            List<String> description = yaml.getStringList(base + "description");

            if (maxLevel < 1) {
                logger.warning("upgrades.yml: max_level must be >= 1 for '" + key + "'. Defaulting to 1.");
                maxLevel = 1;
            }

            defs.put(key, new UpgradesRegistry.UpgradeDefinition(key, displayName, maxLevel, description));
        }

        return new UpgradesRegistry(defs);
    }
}
