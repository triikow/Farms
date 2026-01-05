package com.github.triikow.farms.command.admin;

import com.github.triikow.farms.app.FarmService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public final class AdminReloadCommand {

    private final FarmService farmService;

    public AdminReloadCommand(FarmService farmService) {
        this.farmService = farmService;
    }

    public LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("reload")
                .requires(src -> src.getSender().hasPermission("farms.admin.reload"))
                .executes(this::execute)
                .build();
    }

    private int execute(CommandContext<CommandSourceStack> ctx) {
        farmService.reloadAll();
        farmService.messages().send(ctx.getSource().getSender(), "admin.reload.success");
        return Command.SINGLE_SUCCESS;
    }
}
