package com.github.triikow.farms.command;

import com.github.triikow.farms.app.FarmService;
import com.github.triikow.farms.command.admin.AdminCommand;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class FarmRootCommand {

    private FarmRootCommand() {}

    public static LiteralCommandNode<CommandSourceStack> create(JavaPlugin plugin, FarmService farmService) {
        return Commands.literal("farm")
                .requires(src -> src.getSender().hasPermission("farms.farm"))
                .executes(ctx -> {
                    var sender = ctx.getSource().getSender();
                    if (!(sender instanceof Player player)) {
                        farmService.messages().send(sender, "common.only_players");
                        return 1;
                    }
                    farmService.handleFarmCommand(player);
                    return 1;
                })
                .then(AdminCommand.create(plugin, farmService))
                .build();
    }
}
