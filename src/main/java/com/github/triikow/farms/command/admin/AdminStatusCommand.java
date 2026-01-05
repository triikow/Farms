package com.github.triikow.farms.command.admin;

import com.github.triikow.farms.app.FarmService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public final class AdminStatusCommand {

    private final FarmService farmService;

    public AdminStatusCommand(FarmService farmService) {
        this.farmService = farmService;
    }

    public LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("status")
                .requires(src -> src.getSender().hasPermission("farms.admin.status"))
                .executes(this::execute)
                .build();
    }

    private int execute(CommandContext<CommandSourceStack> ctx) {
        farmService.sendAdminStatus(ctx.getSource().getSender());
        return Command.SINGLE_SUCCESS;
    }
}
