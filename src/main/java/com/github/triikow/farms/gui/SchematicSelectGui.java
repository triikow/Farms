package com.github.triikow.farms.gui;

import com.github.triikow.farms.schematic.SchematicOption;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class SchematicSelectGui {

    public Inventory create(List<SchematicOption> options, java.util.function.Predicate<SchematicOption> canUse) {
        var filtered = options.stream().filter(canUse).toList();

        int count = filtered.size();
        int rows = Math.max(1, (int) Math.ceil(count / 9.0));
        rows = Math.min(rows, 6);
        int size = rows * 9;

        var holder = new SchematicSelectHolder();
        Inventory inv = Bukkit.createInventory(holder, size, "Choose your island");
        holder.bind(inv);

        int index = 0;
        for (int row = 0; row < rows; row++) {
            int remaining = count - index;
            if (remaining <= 0) break;

            int itemsInRow = Math.min(9, remaining);
            int rowStart = row * 9;
            int startSlot = rowStart + (9 - itemsInRow) / 2;

            for (int col = 0; col < itemsInRow; col++) {
                var opt = filtered.get(index++);
                int slot = startSlot + col;

                ItemStack item = new ItemStack(opt.item());
                var meta = item.getItemMeta();
                meta.setDisplayName(opt.displayName());
                meta.setLore(java.util.List.of(
                        "<gray>Click to select</gray>"
                ));
                item.setItemMeta(meta);

                inv.setItem(slot, item);
                holder.map(slot, opt.key());
            }
        }

        return inv;
    }
}
