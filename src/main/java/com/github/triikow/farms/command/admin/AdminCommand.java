package com.github.triikow.farms.command.admin;

import com.github.triikow.farms.island.IslandService;
import com.github.triikow.farms.world.WorldService;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.plugin.java.JavaPlugin;

public final class AdminCommand {

    private AdminCommand() {}

    public static LiteralCommandNode<CommandSourceStack> create(JavaPlugin plugin, WorldService worldService, IslandService islandService) {
        return Commands.literal("admin")
                .requires(src -> src.getSender().hasPermission("farms.admin"))
                .then(new AdminReloadCommand(plugin).create())
                .then(new AdminTpCommand(plugin, worldService).create())
                .then(new AdminStatusCommand(plugin, worldService, islandService).create())
                .build();
    }
}
