package com.github.triikow.farms.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class SchematicConfirmGui {

    public static final int ACCEPT_SLOT = 11;
    public static final int DECLINE_SLOT = 15;

    public Inventory create(String schematicKey) {
        var holder = new SchematicConfirmHolder(schematicKey);
        Inventory inv = Bukkit.createInventory(holder, 27, "Confirm selection");
        holder.bind(inv);

        inv.setItem(ACCEPT_SLOT, named(Material.GREEN_STAINED_GLASS_PANE, "Confirm"));
        inv.setItem(DECLINE_SLOT, named(Material.RED_STAINED_GLASS_PANE, "Cancel"));
        inv.setItem(13, named(Material.PAPER, "Selected: " + schematicKey));

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
