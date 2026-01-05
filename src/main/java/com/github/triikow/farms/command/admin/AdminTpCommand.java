package com.github.triikow.farms.command.admin;

import com.github.triikow.farms.world.WorldService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class AdminTpCommand {

    private final JavaPlugin plugin;
    private final WorldService worldService;

    public AdminTpCommand(JavaPlugin plugin, WorldService worldService) {
        this.plugin = plugin;
        this.worldService = worldService;
    }

    public LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("tp")
                .requires(src -> src.getSender().hasPermission("farms.admin.tp"))
                .executes(this::execute)
                .build();
    }

    private int execute(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();

        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>Only players can use this command.</red>");
            return Command.SINGLE_SUCCESS;
        }

        String prefix = plugin.getConfig().getString("messages.prefix", "");
        String worldName = plugin.getConfig().getString("world.name", "farms");

        World world = worldService.loadOrCreateVoidWorld(worldName);
        if (world == null) {
            player.sendRichMessage(prefix + "<red>Failed to load world '<white>" + worldName + "</white>'.</red>");
            return Command.SINGLE_SUCCESS;
        }

        player.teleport(world.getSpawnLocation().toCenterLocation());
        player.sendRichMessage(prefix + "<gray>Teleported to '<white>" + world.getName() + "</white>'.</gray>");
        return Command.SINGLE_SUCCESS;
    }
}
