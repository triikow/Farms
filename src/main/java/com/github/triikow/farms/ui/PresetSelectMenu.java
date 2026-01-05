package com.github.triikow.farms.ui;

import com.github.triikow.farms.config.Messages;
import com.github.triikow.farms.preset.PresetDefinition;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class PresetSelectMenu {

    public Inventory create(Messages msg, List<PresetDefinition> presets) {
        int count = presets.size();
        int rows = Math.max(1, (int) Math.ceil(count / 9.0));
        rows = Math.min(rows, 6);
        int size = rows * 9;

        PresetSelectHolder holder = new PresetSelectHolder();
        Inventory inv = Bukkit.createInventory(holder, size, msg.ui("ui.select.title"));
        holder.bind(inv);

        Component clickLore = msg.ui("ui.select.lore_click");

        int index = 0;
        for (int row = 0; row < rows; row++) {
            int remaining = count - index;
            if (remaining <= 0) break;

            int itemsInRow = Math.min(9, remaining);
            int rowStart = row * 9;
            int startSlot = rowStart + (9 - itemsInRow) / 2;

            for (int col = 0; col < itemsInRow; col++) {
                PresetDefinition preset = presets.get(index++);
                int slot = startSlot + col;

                ItemStack item = new ItemStack(preset.icon());
                var meta = item.getItemMeta();

                // preset.name/description come from presets.yml (not messages.yml), but still MiniMessage content.
                meta.displayName(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(preset.name()));

                List<Component> lore = new ArrayList<>();
                for (String line : preset.description()) {
                    lore.add(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(line));
                }
                lore.add(Component.empty());
                lore.add(clickLore);

                meta.lore(lore);
                item.setItemMeta(meta);

                inv.setItem(slot, item);
                holder.map(slot, preset.key());
            }
        }

        return inv;
    }
}
