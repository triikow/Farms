package com.github.triikow.farms.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.logging.Logger;

public final class MessagesLoader {

    private MessagesLoader() {}

    public static Messages load(YamlConfiguration yaml, Logger logger) {
        return new Messages(yaml, logger);
    }
}
