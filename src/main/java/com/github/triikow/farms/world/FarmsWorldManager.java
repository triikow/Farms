package com.github.triikow.farms.world;

import com.github.triikow.farms.config.ConfigManager;
import com.github.triikow.farms.config.RuntimeConfig;
import com.github.triikow.farms.domain.FarmCenter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Locale;

public final class FarmsWorldManager {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final VoidChunkGenerator generator = new VoidChunkGenerator();

    public FarmsWorldManager(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public World getOrLoadWorld() {
        RuntimeConfig cfg = configManager.runtime();
        String name = normalizeWorldName(cfg.worldName());

        World loaded = Bukkit.getWorld(name);
        if (loaded != null) return loaded;

        File folder = new File(Bukkit.getWorldContainer(), name);
        boolean existedOnDisk = folder.exists() && folder.isDirectory();

        WorldCreator creator = new WorldCreator(name)
                .type(WorldType.FLAT)
                .generator(generator)
                .generateStructures(false);

        World world = Bukkit.createWorld(creator);
        if (world == null) return null;

        // NOTE: setKeepSpawnInMemory is deprecated + no longer functional since 1.21.9 (spawn chunks removed).
        // We intentionally do not call it to avoid warnings and future removal issues.
        if (cfg.keepSpawnInMemory()) {
            plugin.getLogger().info("world.keep_spawn_in_memory is enabled, but is ignored on modern versions (spawn chunks removed).");
        }

        applyGameRules(world, cfg);

        if (!existedOnDisk) {
            generateSpawnPlatform(world, cfg);
        } else {
            // Ensure spawn location is consistent with config even when world exists.
            world.setSpawnLocation(cfg.spawnX(), cfg.spawnY(), cfg.spawnZ());
        }

        return world;
    }

    public Location farmsSpawn() {
        World w = getOrLoadWorld();
        if (w == null) return null;
        return w.getSpawnLocation().toCenterLocation();
    }

    public Location farmSpawnLocation(World world, FarmCenter center) {
        RuntimeConfig cfg = configManager.runtime();
        int highest = world.getHighestBlockYAt(center.x(), center.z());
        int y = Math.max(cfg.teleportFallbackY(), highest + 1);
        return new Location(world, center.x() + 0.5, y, center.z() + 0.5);
    }

    public boolean isWorldLoaded() {
        RuntimeConfig cfg = configManager.runtime();
        return Bukkit.getWorld(normalizeWorldName(cfg.worldName())) != null;
    }

    public String normalizeWorldName(String input) {
        return input == null ? "farms" : input.trim().toLowerCase(Locale.ROOT);
    }

    private void applyGameRules(World world, RuntimeConfig cfg) {
        // Use the new GameRules constants (old GameRule.DO_* are deprecated for removal).
        world.setGameRule(GameRules.SPAWN_MOBS, cfg.doMobSpawning());
        world.setGameRule(GameRules.ADVANCE_TIME, cfg.doDaylightCycle());
        world.setGameRule(GameRules.ADVANCE_WEATHER, cfg.doWeatherCycle());
    }

    private void generateSpawnPlatform(World world, RuntimeConfig cfg) {
        int cx = cfg.spawnX();
        int cz = cfg.spawnZ();
        int y = cfg.spawnPlatformY();
        int r = Math.max(0, cfg.spawnPlatformRadius());

        Material mat = Material.matchMaterial(cfg.spawnPlatformMaterial());
        if (mat == null) mat = Material.LIGHT_GRAY_STAINED_GLASS;

        for (int x = cx - r; x <= cx + r; x++) {
            for (int z = cz - r; z <= cz + r; z++) {
                Block b = world.getBlockAt(x, y, z);
                b.setType(mat, false);
            }
        }

        world.setSpawnLocation(cx, cfg.spawnY(), cz);
    }
}
