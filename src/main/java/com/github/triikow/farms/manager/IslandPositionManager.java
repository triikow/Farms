package com.github.triikow.farms.manager;

import org.bukkit.Location;
import org.bukkit.World;


public class IslandPositionManager {

    private static final int ISLAND_SPACING = 5000;

    private int x = 0;
    private int z = 0;

    private int dx = 0;
    private int dz = -1;

    public Location getNextIslandLocation(World world, int y) {
        Location location = new Location(
                world,
                x * ISLAND_SPACING,
                y,
                z * ISLAND_SPACING
        );

        // Spiral turn condition
        if (x == z || (x < 0 && x == -z) || (x > 0 && x == 1 - z)) {
            int temp = dx;
            dx = -dz;
            dz = temp;
        }

        x += dx;
        z += dz;

        return location;
    }
}
