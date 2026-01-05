package com.github.triikow.farms;

import com.github.triikow.farms.command.FarmCommand;
import com.github.triikow.farms.island.IslandSchematicService;
import com.github.triikow.farms.island.IslandService;
import com.github.triikow.farms.world.WorldService;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Farms extends JavaPlugin {

    private WorldService worldService;
    private IslandService islandService;
    private IslandSchematicService islandSchematicService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        File schematicsDir = new File(getDataFolder(), "schematics");
        if (!schematicsDir.exists() && !schematicsDir.mkdirs()) {
            getLogger().severe("Could not create schematics directory");
            return;
        }

        File island = new File(schematicsDir, "island.schem");
        if (!island.exists()) {
            saveResource("schematics/island.schem", false);
            getLogger().info("Extracted island.schem");
        }

        this.worldService = new WorldService();
        this.islandService = new IslandService(this);
        this.islandSchematicService = new IslandSchematicService(schematicsDir);

        String worldName = getConfig().getString("world.name", "farms");
        World farmsWorld = worldService.loadOrCreateVoidWorld(worldName);

        if (farmsWorld == null) {
            getLogger().severe("Failed to load or create farms world: " + worldName);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("Farms world ready: " + farmsWorld.getName()
                + " | spawn=" + farmsWorld.getSpawnLocation().getBlockX()
                + "," + farmsWorld.getSpawnLocation().getBlockY()
                + "," + farmsWorld.getSpawnLocation().getBlockZ());

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(
                    FarmCommand.create(this, worldService, islandService, islandSchematicService),
                    "Farms root command"
            );
        });
    }
}
