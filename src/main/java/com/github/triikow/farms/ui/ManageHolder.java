package com.github.triikow.farms.ui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class ManageHolder implements InventoryHolder {

    private Inventory inventory;

    void bind(Inventory inv) {
        this.inventory = inv;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
