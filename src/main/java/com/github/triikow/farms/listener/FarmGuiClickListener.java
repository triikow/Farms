package com.github.triikow.farms.listener;

import com.github.triikow.farms.app.FarmService;
import com.github.triikow.farms.ui.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class FarmGuiClickListener implements Listener {

    private final FarmService farmService;

    public FarmGuiClickListener(FarmService farmService) {
        this.farmService = farmService;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        if (e.getInventory().getHolder() instanceof PresetSelectHolder holder) {
            e.setCancelled(true);
            int slot = e.getRawSlot();
            if (slot < 0 || slot >= e.getInventory().getSize()) return;

            String preset = holder.presetForSlot(slot);
            if (preset == null) return;

            farmService.openConfirm(player, preset);
            return;
        }

        if (e.getInventory().getHolder() instanceof ConfirmHolder holder) {
            e.setCancelled(true);
            int slot = e.getRawSlot();

            if (slot == ConfirmMenu.DECLINE_SLOT) {
                farmService.openPresetSelect(player);
                return;
            }

            if (slot == ConfirmMenu.ACCEPT_SLOT) {
                farmService.createFarmFromPreset(player, holder.presetKey());
            }
            return;
        }

        if (e.getInventory().getHolder() instanceof ManageHolder) {
            e.setCancelled(true);
            int slot = e.getRawSlot();

            if (slot == ManageMenu.HOME_SLOT) {
                farmService.teleportHome(player);
                return;
            }

            if (slot == ManageMenu.UPGRADES_SLOT || slot == ManageMenu.TEAM_SLOT) {
                farmService.sendComingSoon(player);
            }
        }
    }
}
