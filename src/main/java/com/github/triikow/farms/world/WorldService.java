package com.github.triikow.farms.world;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public final class WorldService {

    private final VoidChunkGenerator voidChunkGenerator = new VoidChunkGenerator();

    public @NotNull String normalizeWorldName(@NotNull String input) {
        return input.trim().toLowerCase(Locale.ROOT);
    }

    public @Nullable World createOrLoadVoidWorld(@NotNull String worldName) {
        String name = normalizeWorldName(worldName);

        World existing = Bukkit.getWorld(name);
        if (existing != null) {
            return existing;
        }

        WorldCreator creator = new WorldCreator(name)
                .type(WorldType.FLAT)
                .generator(voidChunkGenerator)
                .generateStructures(false);

        World world = Bukkit.createWorld(creator);
        if (world == null) {
            return null;
        }

        world.setSpawnLocation(0, 120, 0);

        return world;
    }
}
