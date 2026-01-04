package com.github.triikow.farms.command.admin;

import com.github.triikow.farms.world.WorldService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class AdminWorldCommand {

    private final WorldService worldService;

    public AdminWorldCommand(@NotNull WorldService worldService) {
        this.worldService = worldService;
    }

    public LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("world")
                .requires(src -> src.getSender().hasPermission("farms.admin.world"))
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(this::execute)
                )
                .build();
    }

    private int execute(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        String name = StringArgumentType.getString(ctx, "name");

        sender.sendRichMessage("<yellow>[Farms]</yellow> Creating/loading void world <white>" + name + "</white>...");

        World world = worldService.createOrLoadVoidWorld(name);
        if (world == null) {
            sender.sendRichMessage("<red>[Farms]</red> Failed to create/load world <white>" + name + "</white>.");
            return Command.SINGLE_SUCCESS;
        }

        sender.sendRichMessage("<green>[Farms]</green> World ready: <white>" + world.getName() + "</white>.");
        return Command.SINGLE_SUCCESS;
    }
}
