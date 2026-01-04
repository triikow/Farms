package com.github.triikow.farms.island;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public final class IslandSchematicService {

    private static final int PLATFORM_RADIUS = 1;
    private static final int PLATFORM_Y = 60;

    public @NotNull Location createIslandPlatform(@NotNull World world, int centerX, int centerZ) {
        for (int x = centerX - PLATFORM_RADIUS; x <= centerX + PLATFORM_RADIUS; x++) {
            for (int z = centerZ - PLATFORM_RADIUS; z <= centerZ + PLATFORM_RADIUS; z++) {
                world.getBlockAt(x, PLATFORM_Y, z).setType(Material.GRASS_BLOCK, false);
            }
        }

        return new Location(world, centerX + 0.5, PLATFORM_Y + 1, centerZ + 0.5);
    }
}
