package com.github.triikow.farms.command.admin;

import com.github.triikow.farms.app.FarmService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public final class AdminTpCommand {

    private final FarmService farmService;

    public AdminTpCommand(FarmService farmService) {
        this.farmService = farmService;
    }

    public LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("tp")
                .requires(src -> src.getSender().hasPermission("farms.admin.tp"))
                .executes(this::execute)
                .build();
    }

    private int execute(CommandContext<CommandSourceStack> ctx) {
        var sender = ctx.getSource().getSender();
        if (!(sender instanceof Player player)) {
            farmService.messages().send(sender, "common.only_players");
            return Command.SINGLE_SUCCESS;
        }

        farmService.teleportAdminToFarmsSpawn(player);
        return Command.SINGLE_SUCCESS;
    }
}
