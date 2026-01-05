package com.github.triikow.farms.ui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;

public final class PresetSelectHolder implements InventoryHolder {

    private final Map<Integer, String> slotToPreset = new HashMap<>();
    private Inventory inventory;

    void bind(Inventory inv) {
        this.inventory = inv;
    }

    void map(int slot, String presetKey) {
        slotToPreset.put(slot, presetKey);
    }

    public String presetForSlot(int slot) {
        return slotToPreset.get(slot);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
