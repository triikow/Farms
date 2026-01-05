package com.github.triikow.farms.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class FarmManageGui {

    public static final int HOME_SLOT = 13;

    public Inventory create() {
        var holder = new FarmManageHolder();
        Inventory inv = Bukkit.createInventory(holder, 27, "Your Farm");
        holder.bind(inv);

        inv.setItem(HOME_SLOT, named(Material.RED_BED, "Home"));

        return inv;
    }

    private ItemStack named(Material mat, String name) {
        ItemStack is = new ItemStack(mat);
        var meta = is.getItemMeta();
        meta.setDisplayName(name);
        is.setItemMeta(meta);
        return is;
    }
}
