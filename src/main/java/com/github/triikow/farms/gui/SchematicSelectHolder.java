package com.github.triikow.farms.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;

public final class SchematicSelectHolder implements InventoryHolder {
    private final Map<Integer, String> slotToKey = new HashMap<>();
    private Inventory inv;

    void bind(Inventory inv) { this.inv = inv; }
    void map(int slot, String key) { slotToKey.put(slot, key); }

    public String keyForSlot(int slot) { return slotToKey.get(slot); }

    @Override public Inventory getInventory() { return inv; }
}
