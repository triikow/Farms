package com.github.triikow.farms.ui;

import com.github.triikow.farms.config.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class ManageMenu {

    public static final int HOME_SLOT = 11;
    public static final int UPGRADES_SLOT = 13;
    public static final int TEAM_SLOT = 15;

    public Inventory create(Messages msg) {
        ManageHolder holder = new ManageHolder();
        Inventory inv = Bukkit.createInventory(holder, 27, msg.ui("ui.manage.title"));
        holder.bind(inv);

        inv.setItem(HOME_SLOT, item(msg, Material.RED_BED, "ui.manage.home_item", "ui.manage.home_lore"));
        inv.setItem(UPGRADES_SLOT, item(msg, Material.NETHER_STAR, "ui.manage.upgrades_item", "ui.manage.upgrades_lore"));
        inv.setItem(TEAM_SLOT, item(msg, Material.PLAYER_HEAD, "ui.manage.team_item", "ui.manage.team_lore"));

        return inv;
    }

    private ItemStack item(Messages msg, Material mat, String nameKey, String loreKey) {
        ItemStack is = new ItemStack(mat);
        var meta = is.getItemMeta();
        meta.displayName(msg.ui(nameKey));
        meta.lore(msg.uiList(loreKey));
        is.setItemMeta(meta);
        return is;
    }
}
