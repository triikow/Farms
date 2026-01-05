package com.github.triikow.farms.config;

public record RuntimeConfig(
        String worldName,

        int spawnX,
        int spawnY,
        int spawnZ,

        int spawnPlatformY,
        int spawnPlatformRadius,
        String spawnPlatformMaterial,

        boolean keepSpawnInMemory,
        boolean doMobSpawning,
        boolean doDaylightCycle,
        boolean doWeatherCycle,

        int spacing,
        int spawnBuffer,

        int pasteY,
        boolean ignoreAir,

        int teleportFallbackY
) { }
