package com.github.triikow.farms.command.farm;

import com.github.triikow.farms.island.IslandSchematicService;
import com.github.triikow.farms.island.IslandService;
import com.github.triikow.farms.world.WorldService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class FarmCreateCommand {

    private final JavaPlugin plugin;
    private final WorldService worldService;
    private final IslandService islandService;
    private final IslandSchematicService islandSchematicService;

    public FarmCreateCommand(
            JavaPlugin plugin,
            WorldService worldService,
            IslandService islandService,
            IslandSchematicService islandSchematicService
    ) {
        this.plugin = plugin;
        this.worldService = worldService;
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
        World world = worldService.loadOrCreateVoidWorld(worldName);

        if (world == null) {
            player.sendRichMessage("<gray>World '<white>" + worldName + "</white>' does not exist.</gray>");
            return Command.SINGLE_SUCCESS;
        }

        var island = islandService.getOrAllocate(player.getUniqueId());

        Location spawn;
        if (!island.pasted()) {
            spawn = islandSchematicService.pasteIsland(world, island.position().x(), island.position().z());
            islandService.markPasted(player.getUniqueId());

            player.sendRichMessage("<green>Island created at </green><white>"
                    + island.position().x() + ", " + island.position().z()
                    + "</white><green>.</green>");
        } else {
            spawn = islandSchematicService.getIslandSpawn(world, island.position().x(), island.position().z());

            player.sendRichMessage("<yellow>You already have an island. Teleporting you there...</yellow>");
        }

        player.teleport(spawn);
        return Command.SINGLE_SUCCESS;
    }
}
