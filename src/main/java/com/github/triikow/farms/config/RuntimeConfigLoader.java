package com.github.triikow.farms.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Logger;

public final class RuntimeConfigLoader {

    private RuntimeConfigLoader() {}

    public static RuntimeConfig load(FileConfiguration cfg, Logger logger) {
        String worldName = cfg.getString("world.name", "farms");

        int spawnX = cfg.getInt("world.spawn.x", 0);
        int spawnY = cfg.getInt("world.spawn.y", 61);
        int spawnZ = cfg.getInt("world.spawn.z", 0);

        int platformY = cfg.getInt("world.spawn.platform.y", 60);
        int platformRadius = cfg.getInt("world.spawn.platform.radius", 3);
        String platformMaterial = cfg.getString("world.spawn.platform.material", "LIGHT_GRAY_STAINED_GLASS");

        boolean keepSpawnInMemory = cfg.getBoolean("world.keep_spawn_in_memory", false);

        boolean doMobSpawning = cfg.getBoolean("world.gamerules.do_mob_spawning", false);
        boolean doDaylightCycle = cfg.getBoolean("world.gamerules.do_daylight_cycle", false);
        boolean doWeatherCycle = cfg.getBoolean("world.gamerules.do_weather_cycle", false);

        int spacing = cfg.getInt("placement.spacing", 3000);
        int spawnBuffer = cfg.getInt("placement.spawn_buffer", 12000);

        if (spacing <= 0) {
            logger.warning("placement.spacing must be > 0; defaulting to 3000");
            spacing = 3000;
        }
        if (spawnBuffer < 0) {
            logger.warning("placement.spawn_buffer must be >= 0; defaulting to 12000");
            spawnBuffer = 12000;
        }

        int pasteY = cfg.getInt("schematics.paste_y", 60);
        boolean ignoreAir = cfg.getBoolean("schematics.ignore_air", true);

        int teleportFallbackY = cfg.getInt("teleport.fallback_y", 61);

        return new RuntimeConfig(
                worldName,
                spawnX, spawnY, spawnZ,
                platformY, platformRadius, platformMaterial,
                keepSpawnInMemory,
                doMobSpawning, doDaylightCycle, doWeatherCycle,
                spacing, spawnBuffer,
                pasteY, ignoreAir,
                teleportFallbackY
        );
    }
}
