package com.github.triikow.farms.island;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public final class IslandService {

    private static final int MIN_SPAWN_RADIUS = 12000;
    private static final int MIN_ISLAND_DISTANCE = 3000;

    private static final String FILE_NAME = "islands.yml";

    private final JavaPlugin plugin;
    private final File file;
    private final YamlConfiguration yaml;

    public IslandService(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), FILE_NAME);
        this.yaml = YamlConfiguration.loadConfiguration(file);
    }

    public synchronized PlayerIsland getOrAllocate(UUID uuid) {
        PlayerIsland existing = getIsland(uuid);
        if (existing != null) {
            return existing;
        }

        int index = yaml.getInt("nextIndex", 0);
        yaml.set("nextIndex", index + 1);

        IslandPosition pos = computePosition(index);

        String base = "players." + uuid;
        yaml.set(base + ".index", index);
        yaml.set(base + ".x", pos.x());
        yaml.set(base + ".z", pos.z());
        yaml.set(base + ".pasted", false);

        saveQuietly();

        return new PlayerIsland(uuid, index, pos, false);
    }

    public synchronized void markPasted(UUID uuid) {
        String base = "players." + uuid;
        if (!yaml.contains(base)) return;

        yaml.set(base + ".pasted", true);
        saveQuietly();
    }

    public synchronized PlayerIsland getIsland(UUID uuid) {
        String base = "players." + uuid;
        if (!yaml.contains(base + ".x") || !yaml.contains(base + ".z")) {
            return null;
        }

        int x = yaml.getInt(base + ".x");
        int z = yaml.getInt(base + ".z");
        int index = yaml.getInt(base + ".index", -1);
        boolean pasted = yaml.getBoolean(base + ".pasted", false);

        return new PlayerIsland(uuid, index, new IslandPosition(x, z), pasted);
    }

    private IslandPosition computePosition(int islandNumber) {
        final int spacing = MIN_ISLAND_DISTANCE;
        final int minSteps = (int) Math.ceil((double) MIN_SPAWN_RADIUS / spacing);

        int n = islandNumber;

        int r = minSteps;
        int ringCount = 8 * r;

        while (n >= ringCount) {
            n -= ringCount;
            r++;
            ringCount = 8 * r;
        }

        int i;
        int j;

        final int topLen = 2 * r + 1;

        if (n < topLen) {
            i = -r + n;
            j =  r;
        } else {
            n -= topLen;

            final int rightLen = 2 * r;
            if (n < rightLen) {
                i =  r;
                j =  r - 1 - n;
            } else {
                n -= rightLen;

                final int bottomLen = 2 * r;
                if (n < bottomLen) {
                    i =  r - 1 - n;
                    j = -r;
                } else {
                    n -= bottomLen;

                    final int leftLen = 2 * r - 1;

                    if (n < 0 || n >= leftLen) {
                        throw new IllegalStateException("Perimeter index out of bounds: n=" + n + " leftLen=" + leftLen + " r=" + r);
                    }

                    i = -r;
                    j = -r + 1 + n;
                }
            }
        }

        return new IslandPosition(i * spacing, j * spacing);
    }

    private void saveQuietly() {
        try {
            if (!plugin.getDataFolder().exists()) {
                //noinspection ResultOfMethodCallIgnored
                plugin.getDataFolder().mkdirs();
            }
            yaml.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save islands.yml: " + e.getMessage());
        }
    }

    public record IslandPosition(int x, int z) {}

    public record PlayerIsland(UUID uuid, int index, IslandPosition position, boolean pasted) {}
}
