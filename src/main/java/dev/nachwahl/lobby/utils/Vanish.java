package dev.nachwahl.lobby.utils;

import dev.nachwahl.lobby.LobbyPlugin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Vanish {

    @Getter
    private final ArrayList<Player> players;

    public Vanish() {
        players = new ArrayList<>();
    }

    public void add(Player player) {
        players.add(player);
        updateVisibility(player, true);
    }

    public void remove(Player player) {
        players.remove(player);
        updateVisibility(player, false);
    }

    public void toggle(Player player) {
        if (players.contains(player)) {
            remove(player);
        } else {
            add(player);
        }
    }

    public boolean isHidden(Player player) {
        return players.contains(player);
    }

    private void updateVisibility(Player player, boolean show) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (show) {
                p.showPlayer(LobbyPlugin.getInstance(), player);
            } else {
                if (!p.hasPermission("lobby.vanish")) {
                    p.hidePlayer(LobbyPlugin.getInstance(), player);
                }
            }
        }
    }
}
