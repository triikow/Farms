package com.github.triikow.farms.world;

import org.bukkit.*;

import java.io.File;
import java.util.Locale;

public final class WorldService {


    private static final int SPAWN_PLATFORM_RADIUS = 3;
    private static final int SPAWN_PLATFORM_X = 0;
    private static final int SPAWN_PLATFORM_Y = 60;
    private static final int SPAWN_PLATFORM_Z = 0;

    private final VoidChunkGenerator voidChunkGenerator = new VoidChunkGenerator();

    public World loadOrCreateVoidWorld(String worldName) {
        String name = normalizeWorldName(worldName);

        World loaded = Bukkit.getWorld(name);
        if (loaded != null) {
            return loaded;
        }

        File folder = new File(Bukkit.getWorldContainer(), name);
        boolean existedOnDisk = folder.exists();

        WorldCreator creator = new WorldCreator(name)
                .type(WorldType.FLAT)
                .generator(voidChunkGenerator)
                .generateStructures(false);

        World world = Bukkit.createWorld(creator);
        if (world == null) {
            return null;
        }

        if (!existedOnDisk) {
            generateSpawnPlatform(world);
        }

        return world;
    }

    public String normalizeWorldName(String input) {
        return input.trim().toLowerCase(Locale.ROOT);
    }

    private void generateSpawnPlatform(World world) {
        for (int x = SPAWN_PLATFORM_X - SPAWN_PLATFORM_RADIUS; x <= SPAWN_PLATFORM_X + SPAWN_PLATFORM_RADIUS; x++) {
            for (int z = SPAWN_PLATFORM_Z - SPAWN_PLATFORM_RADIUS; z <= SPAWN_PLATFORM_Z + SPAWN_PLATFORM_RADIUS; z++) {
                world.getBlockAt(x, SPAWN_PLATFORM_Y, z).setType(Material.LIGHT_GRAY_STAINED_GLASS, false);
            }
        }
        world.setSpawnLocation(SPAWN_PLATFORM_X, SPAWN_PLATFORM_Y + 1, SPAWN_PLATFORM_Z);
    }

}
