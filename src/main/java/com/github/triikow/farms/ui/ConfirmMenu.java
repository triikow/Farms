package com.github.triikow.farms.ui;

import com.github.triikow.farms.config.Messages;
import com.github.triikow.farms.preset.PresetDefinition;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public final class ConfirmMenu {

    public static final int ACCEPT_SLOT = 11;
    public static final int DECLINE_SLOT = 15;
    public static final int SELECTED_SLOT = 13;

    public Inventory create(Messages msg, PresetDefinition preset) {
        ConfirmHolder holder = new ConfirmHolder(preset.key());
        Inventory inv = Bukkit.createInventory(holder, 27, msg.ui("ui.confirm.title"));
        holder.bind(inv);

        inv.setItem(ACCEPT_SLOT, item(msg, Material.GREEN_STAINED_GLASS_PANE, "ui.confirm.confirm_item", "ui.confirm.confirm_lore"));
        inv.setItem(DECLINE_SLOT, item(msg, Material.RED_STAINED_GLASS_PANE, "ui.confirm.cancel_item", "ui.confirm.cancel_lore"));

        ItemStack selected = new ItemStack(Material.PAPER);
        var meta = selected.getItemMeta();
        meta.displayName(msg.ui("ui.confirm.selected_item", Map.of("preset", preset.key())));
        selected.setItemMeta(meta);

        inv.setItem(SELECTED_SLOT, selected);
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
