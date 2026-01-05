package com.github.triikow.farms.preset;

import org.bukkit.World;

import java.io.File;

public interface SchematicPaster {

    void paste(World world, File schematicFile, int x, int y, int z, boolean ignoreAirBlocks);
}
