package com.github.triikow.farms.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class YamlFile {

    private final JavaPlugin plugin;
    private final String fileName;
    private final File file;

    public YamlFile(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.file = new File(plugin.getDataFolder(), fileName);
    }

    public void ensureExists() {
        if (file.exists()) return;

        try {
            plugin.saveResource(fileName, false);
        } catch (IllegalArgumentException ex) {
            plugin.getLogger().severe("Missing default resource '" + fileName + "' in JAR.");
        }
    }

    public YamlConfiguration reload() {
        return YamlConfiguration.loadConfiguration(file);
    }

    public File file() {
        return file;
    }
}
