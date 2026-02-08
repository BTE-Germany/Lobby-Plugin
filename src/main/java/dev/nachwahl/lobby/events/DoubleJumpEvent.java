package dev.nachwahl.lobby.events;

import dev.nachwahl.lobby.LobbyPlugin;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

public class DoubleJumpEvent implements Listener {

    private LobbyPlugin lobbyPlugin;

    public DoubleJumpEvent(LobbyPlugin lobbyPlugin) {
        this.lobbyPlugin = lobbyPlugin;
    }

    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.ADVENTURE || player.getGameMode() == GameMode.SURVIVAL) {


            if (!player.getAllowFlight()) return;
            /* if (player.getInventory().getChestplate() != null && player.getInventory().getChestplate().getType().equals(Material.ELYTRA))
                return;

                The players have creative flight when having a elytra, if this is not commented out
            */

            event.setCancelled(true);
            Vector v = player.getLocation().getDirection().multiply(2D).setY(2D);
            player.setVelocity(v);
            player.setAllowFlight(false);

        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getLocation().add(0, -1, 0).getBlock().getType() == Material.AIR) return;

        player.setAllowFlight(true);
    }
}
