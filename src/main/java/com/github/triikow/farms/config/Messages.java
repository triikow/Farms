package com.github.triikow.farms.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public final class Messages {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final YamlConfiguration yaml;
    private final Logger logger;
    private final Component prefix;

    public Messages(YamlConfiguration yaml, Logger logger) {
        this.yaml = yaml;
        this.logger = logger;
        this.prefix = deserializeRaw(getString("common.prefix"), Map.of());
    }

    public Component yes() {
        return deserializeRaw(getString("common.yes"), Map.of());
    }

    public Component no() {
        return deserializeRaw(getString("common.no"), Map.of());
    }

    public Component yesNo(boolean value) {
        return value ? yes() : no();
    }

    public void send(CommandSender sender, String key) {
        send(sender, key, Map.of());
    }

    public void send(CommandSender sender, String key, Map<String, Object> placeholders) {
        Component msg = deserialize(key, placeholders);
        sender.sendMessage(prefix.append(msg));
    }

    public void sendRaw(CommandSender sender, String key) {
        sendRaw(sender, key, Map.of());
    }

    public void sendRaw(CommandSender sender, String key, Map<String, Object> placeholders) {
        sender.sendMessage(deserializeRaw(getString(key), placeholders));
    }

    public Component ui(String key) {
        return ui(key, Map.of());
    }

    public Component ui(String key, Map<String, Object> placeholders) {
        return deserializeRaw(getString(key), placeholders);
    }

    public List<Component> uiList(String key) {
        return uiList(key, Map.of());
    }

    public List<Component> uiList(String key, Map<String, Object> placeholders) {
        List<String> lines = getStringList(key);
        if (lines.isEmpty()) return List.of();

        TagResolver resolver = toResolver(placeholders);

        List<Component> out = new ArrayList<>(lines.size());
        for (String line : lines) {
            String template = (line == null) ? "" : applyCurlyPlaceholders(line, placeholders);
            out.add(MM.deserialize(template, resolver));
        }
        return List.copyOf(out);
    }

    private Component deserialize(String key, Map<String, Object> placeholders) {
        String template = getString(key);
        return deserializeRaw(template, placeholders);
    }

    private Component deserializeRaw(String template, Map<String, Object> placeholders) {
        if (template == null) template = "";
        template = applyCurlyPlaceholders(template, placeholders);

        TagResolver resolver = toResolver(placeholders);
        return MM.deserialize(template, resolver);
    }

    /**
     * Your YAML uses {world}, {count}, etc.
     * MiniMessage placeholders are <world>, <count>...
     * This translates only known placeholders to MiniMessage tags.
     */
    private String applyCurlyPlaceholders(String template, Map<String, Object> placeholders) {
        if (placeholders == null || placeholders.isEmpty()) return template;

        String out = template;
        for (String key : placeholders.keySet()) {
            out = out.replace("{" + key + "}", "<" + key + ">");
        }
        return out;
    }

    private TagResolver toResolver(Map<String, Object> placeholders) {
        if (placeholders == null || placeholders.isEmpty()) return TagResolver.empty();

        List<TagResolver> resolvers = new ArrayList<>(placeholders.size());
        for (var entry : placeholders.entrySet()) {
            String k = entry.getKey();
            Object v = entry.getValue();

            if (v instanceof Component c) {
                resolvers.add(Placeholder.component(k, c));
            } else {
                // unparsed prevents placeholder values from injecting MiniMessage tags
                resolvers.add(Placeholder.unparsed(k, String.valueOf(v)));
            }
        }
        return TagResolver.resolver(resolvers);
    }

    private String getString(String key) {
        String s = yaml.getString(key);
        if (s == null) {
            logger.warning("Missing messages.yml key: " + key);
            return "";
        }
        return s;
    }

    private List<String> getStringList(String key) {
        if (!yaml.contains(key)) {
            logger.warning("Missing messages.yml key (list): " + key);
            return List.of();
        }
        return yaml.getStringList(key);
    }
}
