package com.github.triikow.farms.preset;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public final class WorldEditSchematicPaster implements SchematicPaster {

    private static final class Cached {
        final long lastModified;
        final Clipboard clipboard;
        Cached(long lastModified, Clipboard clipboard) {
            this.lastModified = lastModified;
            this.clipboard = clipboard;
        }
    }

    private final Object lock = new Object();
    private final Map<String, Cached> cache = new HashMap<>();

    @Override
    public void paste(World world, File schematicFile, int x, int y, int z, boolean ignoreAirBlocks) {
        if (!schematicFile.exists() || !schematicFile.isFile()) {
            throw new IllegalStateException("Schematic not found: " + schematicFile.getAbsolutePath());
        }

        Clipboard clipboard = loadClipboardCached(schematicFile);

        var weWorld = BukkitAdapter.adapt(world);
        BlockVector3 to = BlockVector3.at(x, y, z);

        try (EditSession session = WorldEdit.getInstance().newEditSession(weWorld)) {
            var op = new ClipboardHolder(clipboard)
                    .createPaste(session)
                    .to(to)
                    .ignoreAirBlocks(ignoreAirBlocks)
                    .build();

            Operations.complete(op);
        } catch (WorldEditException e) {
            throw new RuntimeException("WorldEdit paste failed: " + schematicFile.getName(), e);
        }
    }

    public void invalidateAll() {
        synchronized (lock) {
            cache.clear();
        }
    }

    private Clipboard loadClipboardCached(File file) {
        String key = file.getAbsolutePath();
        long lm = file.lastModified();

        synchronized (lock) {
            Cached existing = cache.get(key);
            if (existing != null && existing.lastModified == lm) {
                return existing.clipboard;
            }

            Clipboard fresh = loadClipboard(file);
            cache.put(key, new Cached(lm, fresh));
            return fresh;
        }
    }

    private Clipboard loadClipboard(File file) {
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        if (format == null) {
            throw new IllegalArgumentException("Unknown schematic format: " + file.getName());
        }

        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            return reader.read();
        } catch (Exception e) {
            throw new RuntimeException("Failed reading schematic: " + file.getName(), e);
        }
    }
}
