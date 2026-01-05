package com.github.triikow.farms.command.farm;

import com.github.triikow.farms.gui.FarmManageGui;
import com.github.triikow.farms.gui.SchematicSelectGui;
import com.github.triikow.farms.island.IslandService;
import com.github.triikow.farms.schematic.SchematicRegistry;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class FarmCommand {

    private final IslandService islandService;
    private final SchematicRegistry registry;

    private final SchematicSelectGui selectGui = new SchematicSelectGui();
    private final FarmManageGui manageGui = new FarmManageGui();

    public FarmCommand(IslandService islandService, SchematicRegistry registry) {
        this.islandService = islandService;
        this.registry = registry;
    }

    public int execute(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>Only players can use this command.</red>");
            return Command.SINGLE_SUCCESS;
        }

        var island = islandService.getIsland(player.getUniqueId());
        if (island != null) {
            player.openInventory(manageGui.create());
            return Command.SINGLE_SUCCESS;
        }

        var options = registry.list();
        if (options.isEmpty()) {
            player.sendRichMessage("<red>No schematics configured.</red>");
            return Command.SINGLE_SUCCESS;
        }

        player.openInventory(selectGui.create(options, opt ->
                player.hasPermission("farms.schematic." + opt.key()) || player.hasPermission("farms.schematic.*")
        ));
        return Command.SINGLE_SUCCESS;
    }
}
