package com.github.triikow.farms.preset;

import com.github.triikow.farms.config.ConfigManager;
import com.github.triikow.farms.config.RuntimeConfig;
import com.github.triikow.farms.domain.FarmCenter;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class PresetApplier {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final SchematicPaster paster;

    public PresetApplier(JavaPlugin plugin, ConfigManager configManager, SchematicPaster paster) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.paster = paster;
    }

    public void apply(World world, PresetDefinition preset, FarmCenter center) {
        RuntimeConfig cfg = configManager.runtime();

        File schematic = new File(plugin.getDataFolder(), preset.schematicFile());
        paster.paste(world, schematic, center.x(), cfg.pasteY(), center.z(), cfg.ignoreAir());
    }

    public void invalidateCaches() {
        if (paster instanceof WorldEditSchematicPaster we) {
            we.invalidateAll();
        }
    }
}
