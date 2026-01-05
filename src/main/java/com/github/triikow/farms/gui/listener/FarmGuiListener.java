package com.github.triikow.farms.gui.listener;

import com.github.triikow.farms.gui.*;
import com.github.triikow.farms.island.IslandSchematicService;
import com.github.triikow.farms.island.IslandService;
import com.github.triikow.farms.schematic.SchematicRegistry;
import com.github.triikow.farms.world.WorldService;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class FarmGuiListener implements Listener {

    private final JavaPlugin plugin;
    private final WorldService worldService;
    private final IslandService islandService;
    private final IslandSchematicService schematicService;
    private final SchematicRegistry registry;

    private final SchematicConfirmGui confirmGui = new SchematicConfirmGui();
    private final FarmManageGui manageGui = new FarmManageGui();

    public FarmGuiListener(
            JavaPlugin plugin,
            WorldService worldService,
            IslandService islandService,
            IslandSchematicService schematicService,
            SchematicRegistry registry
    ) {
        this.plugin = plugin;
        this.worldService = worldService;
        this.islandService = islandService;
        this.schematicService = schematicService;
        this.registry = registry;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        // Select GUI
        if (e.getInventory().getHolder() instanceof SchematicSelectHolder holder) {
            e.setCancelled(true);

            int slot = e.getRawSlot();
            if (slot < 0 || slot >= e.getInventory().getSize()) return;

            String key = holder.keyForSlot(slot);
            if (key == null) return;

            // open confirm GUI
            player.openInventory(confirmGui.create(key));
            return;
        }

        // Confirm GUI
        if (e.getInventory().getHolder() instanceof SchematicConfirmHolder holder) {
            e.setCancelled(true);

            int slot = e.getRawSlot();
            if (slot == SchematicConfirmGui.DECLINE_SLOT) {
                player.closeInventory();
                return;
            }
            if (slot != SchematicConfirmGui.ACCEPT_SLOT) return;

            String key = holder.schematicKey();

            // permission check per schematic
            if (!player.hasPermission("farms.schematic." + key) && !player.hasPermission("farms.schematic.*")) {
                player.sendRichMessage("<red>You do not have permission to select this island.</red>");
                player.closeInventory();
                return;
            }

            // if already has island: just open manage
            var existing = islandService.getIsland(player.getUniqueId());
            if (existing != null) {
                player.openInventory(manageGui.create());
                return;
            }

            var opt = registry.get(key);
            if (opt == null) {
                player.sendRichMessage("<red>That schematic is not available.</red>");
                player.closeInventory();
                return;
            }

            String worldName = plugin.getConfig().getString("world.name", "farms");
            World world = worldService.loadOrCreateVoidWorld(worldName);
            if (world == null) {
                player.sendRichMessage("<red>World not available.</red>");
                player.closeInventory();
                return;
            }

            // allocate with schematic key
            var island = islandService.getOrAllocate(player.getUniqueId(), key);

            // paste only if not pasted
            var spawn = island.pasted()
                    ? schematicService.getIslandSpawn(world, island.position().x(), island.position().z())
                    : schematicService.pasteIsland(world, island.position().x(), island.position().z(), opt.file());

            if (!island.pasted()) {
                islandService.markPasted(player.getUniqueId());
            }

            player.closeInventory();
            player.teleport(spawn);
            player.sendRichMessage("<green>Teleported to your island.</green>");
            return;
        }

        // Manage GUI
        if (e.getInventory().getHolder() instanceof FarmManageHolder) {
            e.setCancelled(true);

            int slot = e.getRawSlot();
            if (slot != FarmManageGui.HOME_SLOT) return;

            var island = islandService.getIsland(player.getUniqueId());
            if (island == null) {
                player.sendRichMessage("<red>You do not have an island yet.</red>");
                player.closeInventory();
                return;
            }

            String worldName = plugin.getConfig().getString("world.name", "farms");
            World world = Bukkit.getWorld(worldService.normalizeWorldName(worldName));
            if (world == null) {
                world = worldService.loadOrCreateVoidWorld(worldName);
            }
            if (world == null) {
                player.sendRichMessage("<red>World not available.</red>");
                return;
            }

            var home = schematicService.getIslandSpawn(world, island.position().x(), island.position().z());
            player.closeInventory();
            player.teleport(home);
        }
    }
}
