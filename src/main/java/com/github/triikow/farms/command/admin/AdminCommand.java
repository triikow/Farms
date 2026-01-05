package com.github.triikow.farms.command.admin;

import com.github.triikow.farms.app.FarmService;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.plugin.java.JavaPlugin;

public final class AdminCommand {

    private AdminCommand() {}

    public static LiteralCommandNode<CommandSourceStack> create(JavaPlugin plugin, FarmService farmService) {
        return Commands.literal("admin")
                .requires(src -> src.getSender().hasPermission("farms.admin"))
                .then(new AdminReloadCommand(farmService).create())
                .then(new AdminStatusCommand(farmService).create())
                .then(new AdminTpCommand(farmService).create())
                .build();
    }
}
