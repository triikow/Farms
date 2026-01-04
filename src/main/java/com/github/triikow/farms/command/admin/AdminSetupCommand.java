package com.github.triikow.farms.command.admin;

import com.github.triikow.farms.world.WorldService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class AdminSetupCommand {

    private final JavaPlugin plugin;
    private final WorldService worldService;

    public AdminSetupCommand(JavaPlugin plugin, WorldService worldService) {
        this.plugin = plugin;
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
        String worldName = plugin.getConfig().getString("world.name", "farms");
        String messagePrefix = plugin.getConfig().getString("messages.prefix", "");
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            String creating = plugin.getConfig().getString(
                    "messages.admin.setup.creating-world",
                    "Creating 'farms' void world..."
            ).replace("%world%", worldName);;
            sender.sendRichMessage(messagePrefix + creating);

            world = worldService.createVoidWorld(worldName);
        } else {
            String exists = plugin.getConfig().getString(
                    "messages.admin.setup.already-exists",
                    "World 'farms' already exists."
            ).replace("%world%", worldName);

            sender.sendRichMessage(messagePrefix + exists);
            return Command.SINGLE_SUCCESS;
        }

        if (world == null) {
            String failed = plugin.getConfig().getString(
                    "messages.admin.setup.failed-create",
                    "<red>Failed to create 'farms' world.</red>"
            ).replace("%world%", worldName);
            sender.sendRichMessage(messagePrefix + failed);
            return Command.SINGLE_SUCCESS;
        }

        String created = plugin.getConfig().getString(
                "messages.admin.setup.created",
                "<green>'farms' void world created.</green>"
        ).replace("%world%", worldName);
        sender.sendRichMessage(messagePrefix + created);

        if (sender instanceof Player player) {
            String teleporting = plugin.getConfig().getString(
                    "messages.admin.setup.teleporting",
                    "Teleporting you to the 'farms' spawn..."
            ).replace("%world%", worldName);;
            sender.sendRichMessage(messagePrefix + teleporting);

            player.teleport(world.getSpawnLocation().toCenterLocation());
        }

        return Command.SINGLE_SUCCESS;
    }
}
