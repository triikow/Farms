package com.github.triikow.farms.app;

import com.github.triikow.farms.config.ConfigManager;
import com.github.triikow.farms.config.Messages;
import com.github.triikow.farms.domain.Farm;
import com.github.triikow.farms.domain.FarmCenter;
import com.github.triikow.farms.domain.TeamState;
import com.github.triikow.farms.persistence.FarmRepository;
import com.github.triikow.farms.preset.PresetApplier;
import com.github.triikow.farms.preset.PresetDefinition;
import com.github.triikow.farms.ui.ConfirmMenu;
import com.github.triikow.farms.ui.ManageMenu;
import com.github.triikow.farms.ui.PresetSelectMenu;
import com.github.triikow.farms.world.FarmsWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class FarmService {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final FarmsWorldManager worldManager;
    private final FarmRepository repo;
    private final FarmAllocator allocator;
    private final PresetApplier applier;

    private final PresetSelectMenu selectMenu = new PresetSelectMenu();
    private final ConfirmMenu confirmMenu = new ConfirmMenu();
    private final ManageMenu manageMenu = new ManageMenu();

    public FarmService(
            JavaPlugin plugin,
            ConfigManager configManager,
            FarmsWorldManager worldManager,
            FarmRepository repo,
            FarmAllocator allocator,
            PresetApplier applier
    ) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.worldManager = worldManager;
        this.repo = repo;
        this.allocator = allocator;
        this.applier = applier;
    }

    public Messages messages() {
        return configManager.messages();
    }

    public void handleFarmCommand(Player player) {
        Optional<Farm> existing = repo.findByOwner(player.getUniqueId());
        if (existing.isPresent()) {
            openManage(player);
            return;
        }
        openPresetSelect(player);
    }

    public void openPresetSelect(Player player) {
        Messages msg = messages();

        var all = configManager.presets().list().stream()
                .filter(p -> canUsePreset(player, p.key()))
                .toList();

        if (all.isEmpty()) {
            msg.send(player, "farm.no_presets");
            return;
        }

        player.openInventory(selectMenu.create(msg, all));
    }

    public void openConfirm(Player player, String presetKey) {
        Messages msg = messages();
        PresetDefinition preset = configManager.presets().get(presetKey);
        if (preset == null) {
            msg.send(player, "farm.no_presets");
            return;
        }
        player.openInventory(confirmMenu.create(msg, preset));
    }

    public void openManage(Player player) {
        Messages msg = messages();
        player.openInventory(manageMenu.create(msg));
    }

    public void createFarmFromPreset(Player player, String presetKey) {
        Messages msg = messages();

        if (repo.findByOwner(player.getUniqueId()).isPresent()) {
            msg.send(player, "farm.already_has_farm");
            openManage(player);
            return;
        }

        PresetDefinition preset = configManager.presets().get(presetKey);
        if (preset == null) {
            msg.send(player, "farm.no_presets");
            return;
        }

        if (!canUsePreset(player, presetKey)) {
            msg.send(player, "common.no_permission");
            return;
        }

        World world = worldManager.getOrLoadWorld();
        if (world == null) {
            msg.send(player, "common.error");
            return;
        }

        int index = repo.allocateNextIndex();
        FarmCenter center = allocator.centerForIndex(index);

        msg.send(player, "farm.creating", Map.of("preset", preset.key()));

        Farm farm = new Farm(
                player.getUniqueId(),
                index,
                center,
                preset.key(),
                Instant.now(),
                false,
                null,
                TeamState.empty(),
                Map.of()
        );
        repo.save(farm);

        try {
            applier.apply(world, preset, center);
            farm = farm.markPasted();
            repo.save(farm);
        } catch (Exception ex) {
            plugin.getLogger().severe("Failed to paste schematic for " + player.getName() + " preset=" + preset.key() + ": " + ex.getMessage());
            ex.printStackTrace();
            msg.send(player, "farm.pasted_failed");
            return;
        }

        msg.send(player, "farm.created");
        msg.send(player, "farm.teleporting");

        var loc = worldManager.farmSpawnLocation(world, center);
        player.closeInventory();
        player.teleportAsync(loc).thenRun(() -> msg.send(player, "farm.teleported"));
    }

    public void teleportHome(Player player) {
        Messages msg = messages();

        Farm farm = repo.findByOwner(player.getUniqueId()).orElse(null);
        if (farm == null) {
            msg.send(player, "farm.no_farm_yet");
            return;
        }

        World world = worldManager.getOrLoadWorld();
        if (world == null) {
            msg.send(player, "common.error");
            return;
        }

        Location loc = (farm.home() != null)
                ? new Location(world, farm.home().x(), farm.home().y(), farm.home().z(), farm.home().yaw(), farm.home().pitch())
                : worldManager.farmSpawnLocation(world, farm.center());

        player.closeInventory();
        player.teleportAsync(loc);
    }

    public void sendComingSoon(Player player) {
        messages().send(player, "ui.manage.coming_soon");
    }

    public void teleportAdminToFarmsSpawn(Player admin) {
        Messages msg = messages();

        World world = worldManager.getOrLoadWorld();
        if (world == null) {
            msg.send(admin, "admin.tp.world_unavailable");
            return;
        }

        Location spawn = world.getSpawnLocation().toCenterLocation();
        admin.teleportAsync(spawn);
        msg.send(admin, "admin.tp.success", Map.of("world", world.getName()));
    }

    public void sendAdminStatus(CommandSender sender) {
        Messages msg = messages();

        String worldName = worldManager.normalizeWorldName(configManager.runtime().worldName());
        File folder = new File(Bukkit.getWorldContainer(), worldName);
        boolean existsOnDisk = folder.isDirectory();

        World world = Bukkit.getWorld(worldName);
        boolean loaded = world != null;

        msg.send(sender, "admin.status.header");
        msg.send(sender, "admin.status.world", Map.of("world", worldName));
        msg.send(sender, "admin.status.world_exists", Map.of("exists", msg.yesNo(existsOnDisk)));
        msg.send(sender, "admin.status.world_loaded", Map.of("loaded", msg.yesNo(loaded)));

        if (loaded) {
            var spawn = world.getSpawnLocation();
            msg.send(sender, "admin.status.world_spawn", Map.of(
                    "x", spawn.getBlockX(),
                    "y", spawn.getBlockY(),
                    "z", spawn.getBlockZ()
            ));
            msg.send(sender, "admin.status.world_loaded_chunks", Map.of("chunks", world.getLoadedChunks().length));
        } else {
            msg.send(sender, "admin.status.world_spawn_unloaded");
        }

        msg.send(sender, "admin.status.farms_header");
        msg.send(sender, "admin.status.farms_count", Map.of("count", repo.count()));
        msg.send(sender, "admin.status.allocator_next_index", Map.of("next", repo.getNextIndex()));
        msg.send(sender, "admin.status.presets_count", Map.of("count", configManager.presets().size()));

        boolean worldEditPresent = Bukkit.getPluginManager().isPluginEnabled("WorldEdit");
        boolean demoSchematicsPresent = demoSchematicsPresent();

        msg.send(sender, "admin.status.integrations_header");
        msg.send(sender, "admin.status.worldedit_present", Map.of("present", msg.yesNo(worldEditPresent)));
        msg.send(sender, "admin.status.demo_schematics_present", Map.of("present", msg.yesNo(demoSchematicsPresent)));
    }

    private boolean demoSchematicsPresent() {
        File a = new File(plugin.getDataFolder(), "schematics/island_basic.schem");
        File b = new File(plugin.getDataFolder(), "schematics/island_desert.schem");
        return a.isFile() && b.isFile();
    }

    public void reloadAll() {
        configManager.reloadAll();
        repo.reload();
        applier.invalidateCaches();
    }

    private boolean canUsePreset(Player player, String presetKey) {
        return player.hasPermission("farms.preset." + presetKey) || player.hasPermission("farms.preset.*");
    }
}
