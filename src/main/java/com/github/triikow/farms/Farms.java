package com.github.triikow.farms;

import com.github.triikow.farms.command.FarmCommand;
import com.github.triikow.farms.island.IslandSchematicService;
import com.github.triikow.farms.island.IslandService;
import com.github.triikow.farms.world.WorldService;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class Farms extends JavaPlugin {

    private WorldService worldService;
    private IslandService islandService;
    private IslandSchematicService islandSchematicService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.worldService = new WorldService();
        this.islandService = new IslandService(this);
        this.islandSchematicService = new IslandSchematicService();

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(
                    FarmCommand.create(this, worldService, islandService, islandSchematicService),
                    "Farms root command"
            );
        });
    }
}
