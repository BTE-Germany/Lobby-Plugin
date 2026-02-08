package dev.nachwahl.lobby.events;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import dev.nachwahl.lobby.LobbyPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class EnvironmentEvents implements Listener {

    private LobbyPlugin lobbyPlugin;

    public EnvironmentEvents(LobbyPlugin lobbyPlugin) {
        this.lobbyPlugin = lobbyPlugin;
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onThunderstormChange(ThunderChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockDestroy(BlockBreakEvent event) {
        if (!this.lobbyPlugin.getEditModePlayers().contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBuild(BlockPlaceEvent event) {
        if (!this.lobbyPlugin.getEditModePlayers().contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!this.lobbyPlugin.getEditModePlayers().contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        event.setCancelled(false);
    }

    @EventHandler
    public void onDestroy(BlockDestroyEvent event) {
        event.setCancelled(true);
    }


}
