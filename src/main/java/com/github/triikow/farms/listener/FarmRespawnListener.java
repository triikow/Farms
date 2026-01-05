package com.github.triikow.farms.listener;

import com.github.triikow.farms.persistence.FarmRepository;
import com.github.triikow.farms.world.FarmsWorldManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public final class FarmRespawnListener implements Listener {

    private final FarmsWorldManager worldManager;
    private final FarmRepository repo;

    public FarmRespawnListener(FarmsWorldManager worldManager, FarmRepository repo) {
        this.worldManager = worldManager;
        this.repo = repo;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (e.getRespawnReason() != PlayerRespawnEvent.RespawnReason.DEATH) return;

        var player = e.getPlayer();
        var farmOpt = repo.findByOwner(player.getUniqueId());
        if (farmOpt.isEmpty()) return;

        World farmsWorld = worldManager.getOrLoadWorld();
        if (farmsWorld == null) return;

        // We only override respawn if the player died in the farms world.
        Location lastDeath = player.getLastDeathLocation();
        if (lastDeath == null || lastDeath.getWorld() == null) return;
        if (!lastDeath.getWorld().getName().equals(farmsWorld.getName())) return;

        var farm = farmOpt.get();

        Location target;
        if (farm.home() != null) {
            target = new Location(
                    farmsWorld,
                    farm.home().x(),
                    farm.home().y(),
                    farm.home().z(),
                    farm.home().yaw(),
                    farm.home().pitch()
            );
        } else {
            target = worldManager.farmSpawnLocation(farmsWorld, farm.center());
        }

        e.setRespawnLocation(target);
    }
}
