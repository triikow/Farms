package com.github.triikow.farms.persistence;

import com.github.triikow.farms.domain.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

public final class YamlFarmRepository implements FarmRepository {

    private final JavaPlugin plugin;
    private final File file;

    private final Object lock = new Object();
    private YamlConfiguration yaml;

    public YamlFarmRepository(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(new File(plugin.getDataFolder(), "data"), "farms.yml");
        reload();
    }

    @Override
    public Optional<Farm> findByOwner(UUID ownerId) {
        synchronized (lock) {
            String base = "farms." + ownerId + ".";
            if (!yaml.contains(base + "center.x") || !yaml.contains(base + "center.z")) {
                return Optional.empty();
            }

            int index = yaml.getInt(base + "index", -1);
            int x = yaml.getInt(base + "center.x");
            int z = yaml.getInt(base + "center.z");
            String presetKey = yaml.getString(base + "preset", "basic");
            long createdAtMs = yaml.getLong(base + "created_at", System.currentTimeMillis());
            boolean pasted = yaml.getBoolean(base + "pasted", false);

            FarmHome home = null;
            if (yaml.contains(base + "home.x")) {
                home = new FarmHome(
                        yaml.getDouble(base + "home.x"),
                        yaml.getDouble(base + "home.y"),
                        yaml.getDouble(base + "home.z"),
                        (float) yaml.getDouble(base + "home.yaw"),
                        (float) yaml.getDouble(base + "home.pitch")
                );
            }

            Set<UUID> members = new LinkedHashSet<>();
            for (String s : yaml.getStringList(base + "team.members")) {
                try { members.add(UUID.fromString(s)); } catch (IllegalArgumentException ignored) {}
            }
            TeamState team = new TeamState(members);

            Map<String, Integer> upgrades = new LinkedHashMap<>();
            ConfigurationSection upSec = yaml.getConfigurationSection(base + "upgrades");
            if (upSec != null) {
                for (String k : upSec.getKeys(false)) {
                    upgrades.put(k, upSec.getInt(k, 0));
                }
            }

            Farm farm = new Farm(
                    ownerId,
                    index,
                    new FarmCenter(x, z),
                    presetKey,
                    Instant.ofEpochMilli(createdAtMs),
                    pasted,
                    home,
                    team,
                    upgrades
            );
            return Optional.of(farm);
        }
    }

    @Override
    public void save(Farm farm) {
        synchronized (lock) {
            String base = "farms." + farm.ownerId() + ".";
            yaml.set(base + "index", farm.index());
            yaml.set(base + "center.x", farm.center().x());
            yaml.set(base + "center.z", farm.center().z());
            yaml.set(base + "preset", farm.presetKey());
            yaml.set(base + "created_at", farm.createdAt().toEpochMilli());
            yaml.set(base + "pasted", farm.pasted());

            if (farm.home() != null) {
                yaml.set(base + "home.x", farm.home().x());
                yaml.set(base + "home.y", farm.home().y());
                yaml.set(base + "home.z", farm.home().z());
                yaml.set(base + "home.yaw", farm.home().yaw());
                yaml.set(base + "home.pitch", farm.home().pitch());
            } else {
                yaml.set(base + "home", null);
            }

            yaml.set(base + "team.members", farm.team().members().stream().map(UUID::toString).toList());

            yaml.set(base + "upgrades", null);
            for (var e : farm.upgrades().entrySet()) {
                yaml.set(base + "upgrades." + e.getKey(), e.getValue());
            }

            saveQuietly();
        }
    }

    @Override
    public int allocateNextIndex() {
        synchronized (lock) {
            int next = yaml.getInt("next_index", 0);
            yaml.set("next_index", next + 1);
            saveQuietly();
            return next;
        }
    }

    @Override
    public int getNextIndex() {
        synchronized (lock) {
            return yaml.getInt("next_index", 0);
        }
    }

    @Override
    public int count() {
        synchronized (lock) {
            ConfigurationSection sec = yaml.getConfigurationSection("farms");
            return sec == null ? 0 : sec.getKeys(false).size();
        }
    }

    @Override
    public void reload() {
        synchronized (lock) {
            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                plugin.getLogger().warning("Could not create data directory: " + file.getParentFile().getAbsolutePath());
            }
            this.yaml = YamlConfiguration.loadConfiguration(file);
            if (!yaml.contains("next_index")) {
                yaml.set("next_index", 0);
                saveQuietly();
            }
        }
    }

    private void saveQuietly() {
        try {
            yaml.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save data/farms.yml: " + e.getMessage());
        }
    }
}
