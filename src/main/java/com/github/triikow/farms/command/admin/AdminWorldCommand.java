package com.github.triikow.farms.command.admin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class AdminWorldCommand {

    private AdminWorldCommand() {}

    public static LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("world")
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(AdminWorldCommand::execute)
                )
                .build();
    }

    @SuppressWarnings("SameReturnValue")
    private static int execute(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();

        if (!(sender instanceof Player)) {
            sender.sendRichMessage("<red>Only players can use this command.</red>");
            return Command.SINGLE_SUCCESS;
        }

        String name = StringArgumentType.getString(ctx, "name");

        sender.sendRichMessage(
                "<yellow>[Admin] World command received for '<white>" + name + "</white>'</yellow>"
        );

        return Command.SINGLE_SUCCESS;
    }
}
