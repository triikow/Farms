package com.github.triikow.farms.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class SchematicConfirmHolder implements InventoryHolder {
    private final String schematicKey;
    private Inventory inv;

    public SchematicConfirmHolder(String schematicKey) {
        this.schematicKey = schematicKey;
    }

    void bind(Inventory inv) { this.inv = inv; }
    public String schematicKey() { return schematicKey; }

    @Override public Inventory getInventory() { return inv; }
}
