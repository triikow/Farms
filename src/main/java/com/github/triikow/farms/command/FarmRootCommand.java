package com.github.triikow.farms.command;

import com.github.triikow.farms.command.admin.AdminCommand;
import com.github.triikow.farms.command.farm.FarmCommand;
import com.github.triikow.farms.island.IslandSchematicService;
import com.github.triikow.farms.island.IslandService;
import com.github.triikow.farms.schematic.SchematicRegistry;
import com.github.triikow.farms.world.WorldService;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.plugin.java.JavaPlugin;

public final class FarmRootCommand {

    private FarmRootCommand() {}

    public static LiteralCommandNode<CommandSourceStack> create(
            JavaPlugin plugin,
            WorldService worldService,
            IslandService islandService,
            IslandSchematicService islandSchematicService,
            SchematicRegistry registry
    ) {
        return Commands.literal("farm")
                .requires(src -> src.getSender().hasPermission("farms.farm"))
                .executes(new FarmCommand(islandService, registry)::execute)
                .then(AdminCommand.create(plugin, worldService, islandService))
                .build();
    }
}
