package com.github.triikow.farms.command;

import com.github.triikow.farms.manager.IslandPositionManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FarmCommand  {

    private static final IslandPositionManager positionManager = new IslandPositionManager();

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("farm")
                .then(Commands.literal("create")
                        .executes(FarmCommand::runCreateFarmLogic)
                )
                .build();
    }

    private static int runCreateFarmLogic(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        String worldName = "farms";
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            WorldCreator creator = new WorldCreator(worldName);

            sender.sendRichMessage("<yellow>Creating world '" + worldName + "'");
            creator.type(WorldType.FLAT);
            creator.generatorSettings("{\"layers\":[{\"block\":\"air\",\"height\":1}],\"biome\":\"the_void\",\"features\":true}");
            world = creator.createWorld();
            sender.sendRichMessage("<green>World '" + worldName + "' created.");
        }

        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>Only players can use this command.");
        }
        else {
            Location center = positionManager.getNextIslandLocation(world, 0);
            generateIsland(center, sender);
            player.teleport(center);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static void generateIsland(Location center, CommandSender sender) {
        World world = center.getWorld();

        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                world.getBlockAt(
                        center.getBlockX() + x,
                        center.getBlockY() -2,
                        center.getBlockZ() + z
                ).setType(Material.DIRT);

                world.getBlockAt(
                        center.getBlockX() + x,
                        center.getBlockY() -1,
                        center.getBlockZ() + z
                ).setType(Material.GRASS_BLOCK);
            }
        }

        sender.sendRichMessage("<green>Island created at: (" + center.getBlockX() + ", " + center.getBlockY() + ", " + center.blockZ() + ")");
    }
}
