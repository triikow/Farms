package com.github.triikow.farms.command.admin;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public final class AdminCommand {

    private AdminCommand() {}

    public static LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("admin")
                .then(AdminWorldCommand.create())
                .build();
    }
}