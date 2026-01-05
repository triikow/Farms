package com.github.triikow.farms.command.admin;

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
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class AdminStatusCommand {

    private final JavaPlugin plugin;
    private final WorldService worldService;
    private final IslandService islandService;

    public AdminStatusCommand(JavaPlugin plugin, WorldService worldService, IslandService islandService) {
        this.plugin = plugin;
        this.worldService = worldService;
        this.islandService = islandService;
    }

    public LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("status")
                .requires(src -> src.getSender().hasPermission("farms.admin.status"))
                .executes(this::execute)
                .build();
    }

    private int execute(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();

        String prefix = plugin.getConfig().getString("messages.prefix", "");
        String rawName = plugin.getConfig().getString("world.name", "farms");
        String worldName = worldService.normalizeWorldName(rawName);

        // world exists on disk
        File folder = new File(Bukkit.getWorldContainer(), worldName);
        boolean existsOnDisk = folder.exists() && folder.isDirectory();

        // world loaded
        World world = Bukkit.getWorld(worldName);
        boolean loaded = (world != null);

        Location spawn = loaded ? world.getSpawnLocation() : null;

        int allocatedIslands = islandService.getNextIndex();
        int playerIslandEntries = islandService.getPlayerIslandCount();

        boolean worldEditPresent = isPluginEnabled("WorldEdit") || isPluginEnabled("FastAsyncWorldEdit");

        sender.sendRichMessage(prefix + "<dark_gray>Status</dark_gray>");
        sender.sendRichMessage("<gray>World:</gray> <white>" + worldName + "</white>");
        sender.sendRichMessage("  <gray><yellow>•</yellow> Exists on disk:</gray> " + yesNo(existsOnDisk));
        sender.sendRichMessage("  <gray><yellow>•</yellow> Loaded:</gray> " + yesNo(loaded));

        if (spawn != null) {
            sender.sendRichMessage("  <gray><yellow>•</yellow> Spawn:</gray> <white>"
                    + spawn.getBlockX() + ", " + spawn.getBlockY() + ", " + spawn.getBlockZ()
                    + "</white>");
            sender.sendRichMessage("  <gray><yellow>•</yellow> Loaded chunks:</gray> <white>" + world.getLoadedChunks().length + "</white>");
        } else {
            sender.sendRichMessage("  <gray><yellow>•</yellow> Spawn:</gray> <dark_gray>(world not loaded)</dark_gray>");
        }

        sender.sendRichMessage("<gray>Islands:</gray>");
        sender.sendRichMessage("  <gray><yellow>•</yellow> Allocated (nextIndex):</gray> <white>" + allocatedIslands + "</white>");
        sender.sendRichMessage("  <gray><yellow>•</yellow> Player entries:</gray> <white>" + playerIslandEntries + "</white>");

        sender.sendRichMessage("<gray>Integrations:</gray>");
        sender.sendRichMessage("  <gray><yellow>•</yellow> WorldEdit/FAWE:</gray> " + yesNo(worldEditPresent));
        sender.sendRichMessage("  <gray><yellow>•</yellow> Schematic present:</gray> " + yesNo(schematicExists()));

        return Command.SINGLE_SUCCESS;
    }

    private String yesNo(boolean value) {
        return value ? "<green>Yes</green>" : "<red>No</red>";
    }

    private boolean isPluginEnabled(String name) {
        Plugin p = Bukkit.getPluginManager().getPlugin(name);
        return p != null && p.isEnabled();
    }

    private boolean schematicExists() {
        File schem = new File(plugin.getDataFolder(), "schematics/island.schem");
        return schem.exists() && schem.isFile();
    }
}
