package com.github.triikow.farms.command.admin;

import com.github.triikow.farms.world.WorldService;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;

public final class AdminCommand {

    private AdminCommand() {}

    public static LiteralCommandNode<CommandSourceStack> create(@NotNull WorldService worldService) {
        return Commands.literal("admin")
                .then(new AdminWorldCommand(worldService).create())
                .build();
    }
}
