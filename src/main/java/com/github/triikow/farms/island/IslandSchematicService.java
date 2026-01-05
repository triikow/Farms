package com.github.triikow.farms.island;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;

public final class IslandSchematicService {

    private static final int Y = 60;
    private final File schematicsDir;

    public IslandSchematicService(File schematicsDir) {
        this.schematicsDir = schematicsDir;
    }

    public Location pasteIsland(World world, int centerX, int centerZ, String schematicFileName) {
        File file = new File(schematicsDir, schematicFileName);
        if (!file.exists()) {
            throw new IllegalStateException("Missing schematic: " + file.getAbsolutePath());
        }

        Clipboard clipboard = loadClipboard(file);

        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);
        BlockVector3 pasteTo = BlockVector3.at(centerX, Y, centerZ);

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
            Operation op = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(pasteTo)
                    .ignoreAirBlocks(true)
                    .build();

            Operations.complete(op);
        } catch (WorldEditException e) {
            throw new RuntimeException("Failed to paste schematic: " + file.getName(), e);
        }

        return new Location(world, centerX + 0.5, Y + 1, centerZ + 0.5);
    }

    private Clipboard loadClipboard(File file) {
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        if (format == null) {
            throw new IllegalArgumentException("Unknown schematic format: " + file.getName());
        }

        try (ClipboardReader reader = format.getReader(new java.io.FileInputStream(file))) {
            return reader.read();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load schematic: " + file.getName(), e);
        }
    }

    public Location getIslandSpawn(World world, int centerX, int centerZ) {
        return new Location(world, centerX + 0.5, Y + 1, centerZ + 0.5);
    }
}

