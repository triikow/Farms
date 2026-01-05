package com.github.triikow.farms.island;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

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

    public IslandPosition allocateNext() {
        int nextIndex = yaml.getInt("nextIndex", 0);

        IslandPosition pos = computePosition(nextIndex);

        yaml.set("nextIndex", nextIndex + 1);
        yaml.set("islands." + nextIndex + ".x", pos.x());
        yaml.set("islands." + nextIndex + ".z", pos.z());
        saveQuietly();

        return pos;
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
}
