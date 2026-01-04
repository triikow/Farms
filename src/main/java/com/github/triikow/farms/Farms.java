package com.github.triikow.farms;

import com.github.triikow.farms.command.FarmCommand;
import com.github.triikow.farms.world.WorldService;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class Farms extends JavaPlugin {

    private WorldService worldService;

    @Override
    public void onEnable() {
        this.worldService = new WorldService();

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(
                    FarmCommand.create(worldService),
                    "Farms root command"
            );
        });
    }
}
