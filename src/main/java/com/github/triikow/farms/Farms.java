package com.github.triikow.farms;

import com.github.triikow.farms.command.FarmCommand;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class Farms extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(FarmCommand.createCommand());
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
