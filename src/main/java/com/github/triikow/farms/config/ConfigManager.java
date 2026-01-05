package com.github.triikow.farms.config;

import com.github.triikow.farms.preset.PresetCatalog;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class ConfigManager {

    private final JavaPlugin plugin;

    private final YamlFile presetsFile;
    private final YamlFile messagesFile;
    private final YamlFile upgradesFile;

    private RuntimeConfig runtimeConfig;
    private PresetCatalog presetCatalog;
    private Messages messages;
    private UpgradesRegistry upgradesRegistry;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.presetsFile = new YamlFile(plugin, "presets.yml");
        this.messagesFile = new YamlFile(plugin, "messages.yml");
        this.upgradesFile = new YamlFile(plugin, "upgrades.yml");
    }

    public void loadAll() {
        presetsFile.ensureExists();
        messagesFile.ensureExists();
        upgradesFile.ensureExists();
        reloadAll();
    }

    public void reloadAll() {
        plugin.reloadConfig();

        this.runtimeConfig = RuntimeConfigLoader.load(plugin.getConfig(), plugin.getLogger());

        YamlConfiguration presetsYaml = presetsFile.reload();
        this.presetCatalog = PresetsLoader.load(presetsYaml, plugin.getDataFolder(), plugin.getLogger());

        YamlConfiguration messagesYaml = messagesFile.reload();
        this.messages = MessagesLoader.load(messagesYaml, plugin.getLogger());

        YamlConfiguration upgradesYaml = upgradesFile.reload();
        this.upgradesRegistry = UpgradesLoader.load(upgradesYaml, plugin.getLogger());
    }

    public RuntimeConfig runtime() {
        return runtimeConfig;
    }

    public PresetCatalog presets() {
        return presetCatalog;
    }

    public Messages messages() {
        return messages;
    }

    public UpgradesRegistry upgrades() {
        return upgradesRegistry;
    }
}
