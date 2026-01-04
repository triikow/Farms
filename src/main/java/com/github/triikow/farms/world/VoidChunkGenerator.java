package com.github.triikow.farms.world;

import org.bukkit.Location;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import java.util.Random;

@SuppressWarnings("NullableProblems")
public final class VoidChunkGenerator extends ChunkGenerator {

    @Override
    public void generateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {}

    @Override
    public Location getFixedSpawnLocation(org.bukkit.World world, Random random) {
        return new Location(world, 0.5, 120, 0.5);
    }
}
