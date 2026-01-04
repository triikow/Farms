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
        final int spacing = MIN_ISLAND_DISTANCE; // 3000
        final int minSteps = (int) Math.ceil((double) MIN_SPAWN_RADIUS / spacing);

        int accepted = 0;
        int stepIndex = 0;

        while (true) {
            GridPos p = spiral(stepIndex++);

            int ring = Math.max(Math.abs(p.i), Math.abs(p.j));

            if (ring < minSteps) {
                continue;
            }

            if (accepted == islandNumber) {
                int x = p.i * spacing;
                int z = p.j * spacing;
                return new IslandPosition(x, z);
            }

            accepted++;
        }
    }

    private GridPos spiral(int n) {
        if (n == 0) return new GridPos(0, 0);

        int i = 0;
        int j = 0;

        int steps = 1;
        int index = 0;

        while (true) {
            for (int s = 0; s < steps; s++) {
                i++;
                index++;
                if (index == n) return new GridPos(i, j);
            }

            for (int s = 0; s < steps; s++) {
                j++;
                index++;
                if (index == n) return new GridPos(i, j);
            }

            steps++;

            for (int s = 0; s < steps; s++) {
                i--;
                index++;
                if (index == n) return new GridPos(i, j);
            }

            for (int s = 0; s < steps; s++) {
                j--;
                index++;
                if (index == n) return new GridPos(i, j);
            }

            steps++;
        }
    }

    private record GridPos(int i, int j) {}

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
