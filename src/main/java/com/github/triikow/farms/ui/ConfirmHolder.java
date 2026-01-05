package com.github.triikow.farms.ui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class ConfirmHolder implements InventoryHolder {

    private final String presetKey;
    private Inventory inventory;

    public ConfirmHolder(String presetKey) {
        this.presetKey = presetKey;
    }

    void bind(Inventory inv) {
        this.inventory = inv;
    }

    public String presetKey() {
        return presetKey;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
