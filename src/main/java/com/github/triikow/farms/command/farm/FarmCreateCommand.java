package com.github.triikow.farms.command.farm;

import com.github.triikow.farms.island.IslandSchematicService;
import com.github.triikow.farms.island.IslandService;
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
import org.bukkit.plugin.java.JavaPlugin;

public final class FarmCreateCommand {

    private final JavaPlugin plugin;
    private final IslandService islandService;
    private final IslandSchematicService islandSchematicService;

    public FarmCreateCommand(
            JavaPlugin plugin,
            IslandService islandService,
            IslandSchematicService islandSchematicService
    ) {
        this.plugin = plugin;
        this.islandService = islandService;
        this.islandSchematicService = islandSchematicService;
    }

    public LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("create")
                .requires(src -> src.getSender().hasPermission("farms.create"))
                .executes(this::execute)
                .build();
    }

    private int execute(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();

        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>Only players can use this command.</red>");
            return Command.SINGLE_SUCCESS;
        }

        String worldName = plugin.getConfig().getString("world.name", "farms");
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            player.sendRichMessage("<gray>World '<white>" + worldName + "</white>' does not exist.</gray>");
            return Command.SINGLE_SUCCESS;
        }

        IslandService.IslandPosition pos = islandService.allocateNext();
        Location spawn = islandSchematicService.pasteIsland(world, pos.x(), pos.z());

        player.teleport(spawn);
        player.sendRichMessage("<green>Island created at </green><white>" + pos.x() + ", " + pos.z() + "</white><green>.</green>");

        return Command.SINGLE_SUCCESS;
    }
}
