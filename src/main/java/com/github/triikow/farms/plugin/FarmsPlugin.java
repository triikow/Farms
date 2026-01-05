package com.github.triikow.farms.plugin;

import com.github.triikow.farms.app.FarmAllocator;
import com.github.triikow.farms.app.FarmService;
import com.github.triikow.farms.command.FarmRootCommand;
import com.github.triikow.farms.config.ConfigManager;
import com.github.triikow.farms.listener.FarmGuiClickListener;
import com.github.triikow.farms.listener.FarmRespawnListener;
import com.github.triikow.farms.persistence.FarmRepository;
import com.github.triikow.farms.persistence.YamlFarmRepository;
import com.github.triikow.farms.preset.PresetApplier;
import com.github.triikow.farms.preset.WorldEditSchematicPaster;
import com.github.triikow.farms.world.FarmsWorldManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public final class FarmsPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private FarmsWorldManager worldManager;
    private FarmRepository farmRepository;
    private PresetApplier presetApplier;
    private FarmAllocator farmAllocator;
    private FarmService farmService;

    @Override
    public void onEnable() {
        if (!isPluginEnabled("WorldEdit")) {
            getLogger().severe("WorldEdit is required (depend) but was not found/enabled. Disabling Farms.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();

        this.configManager = new ConfigManager(this);
        this.configManager.loadAll();

        ensureFolder(new File(getDataFolder(), "schematics"));
        ensureFolder(new File(getDataFolder(), "data"));

        extractBundledSchematics(List.of(
                "schematics/island_basic.schem",
                "schematics/island_desert.schem"
        ));

        this.worldManager = new FarmsWorldManager(this, configManager);
        if (worldManager.getOrLoadWorld() == null) {
            getLogger().severe("Failed to load/create farms world. Disabling Farms.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.farmRepository = new YamlFarmRepository(this);
        this.presetApplier = new PresetApplier(this, configManager, new WorldEditSchematicPaster());
        this.farmAllocator = new FarmAllocator(configManager);
        this.farmService = new FarmService(this, configManager, worldManager, farmRepository, farmAllocator, presetApplier);

        Bukkit.getPluginManager().registerEvents(new FarmGuiClickListener(farmService), this);
        Bukkit.getPluginManager().registerEvents(new FarmRespawnListener(worldManager, farmRepository), this);

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(
                    FarmRootCommand.create(this, farmService),
                    "Farms command tree"
            );
        });

        getLogger().info("Farms enabled.");
    }

    private boolean isPluginEnabled(String name) {
        var p = Bukkit.getPluginManager().getPlugin(name);
        return p != null && p.isEnabled();
    }

    private void ensureFolder(File dir) {
        if (!dir.exists() && !dir.mkdirs()) {
            getLogger().warning("Could not create folder: " + dir.getAbsolutePath());
        }
    }

    private void extractBundledSchematics(List<String> resourcePaths) {
        for (String path : resourcePaths) {
            File out = new File(getDataFolder(), path);
            if (out.exists()) continue;

            try {
                saveResource(path, false);
                getLogger().info("Extracted " + path);
            } catch (IllegalArgumentException ex) {
                getLogger().severe("Missing bundled resource '" + path + "' in JAR. Add it under src/main/resources/" + path);
            }
        }
    }
}
