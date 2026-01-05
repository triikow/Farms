package com.github.triikow.farms.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class FarmManageHolder implements InventoryHolder {
    private Inventory inv;

    void bind(Inventory inv) { this.inv = inv; }
    @Override public Inventory getInventory() { return inv; }
}
