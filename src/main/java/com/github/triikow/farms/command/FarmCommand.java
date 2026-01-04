package com.github.triikow.farms.command;

import com.github.triikow.farms.command.admin.AdminCommand;
import com.github.triikow.farms.world.WorldService;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.plugin.java.JavaPlugin;

public final class FarmCommand {

    private FarmCommand() {}

    public static LiteralCommandNode<CommandSourceStack> create(JavaPlugin plugin, WorldService worldService) {
        return Commands.literal("farm")
                .then(AdminCommand.create(plugin, worldService))
                .build();
    }
}
