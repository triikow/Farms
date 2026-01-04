package com.github.triikow.farms.command.admin;

import com.github.triikow.farms.world.WorldService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class AdminSetupCommand {

    private static final String FARMS_WORLD_NAME = "farms";

    private final WorldService worldService;

    public AdminSetupCommand(@NotNull WorldService worldService) {
        this.worldService = worldService;
    }

    public LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("setup")
                .requires(src -> src.getSender().hasPermission("farms.admin.setup"))
                .executes(this::execute)
                .build();
    }

    private int execute(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        World world = Bukkit.getWorld(FARMS_WORLD_NAME);

        if (world == null) {
            sender.sendRichMessage("<yellow>[Farms]</yellow> Creating farms void world...");
            world = worldService.createVoidWorld(FARMS_WORLD_NAME);
        } else {
            sender.sendRichMessage("<yellow>[Farms]</yellow> '" + FARMS_WORLD_NAME + "' already exists...");
            return Command.SINGLE_SUCCESS;
        }

        if (world == null) {
            sender.sendRichMessage("<red>[Farms]</red> Failed to create farms world.");
            return Command.SINGLE_SUCCESS;
        }

        sender.sendRichMessage("<green>[Farms]</green> Farms void world created.");

        if (sender instanceof Player player) {
            player.teleport(world.getSpawnLocation().toCenterLocation());
        }

        return Command.SINGLE_SUCCESS;
    }
}
