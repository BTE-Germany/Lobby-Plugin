package dev.nachwahl.lobby.utils;

import dev.nachwahl.lobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Actions {
    private Actions() {}

    public static void performJoinActions(@NotNull Lobby lobby, Player player) {
        lobby.getHotbarItems().setHotbarItems(player);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        player.setFoodLevel(20);
        player.setHealth(20);


        player.setAllowFlight(false); // No double jump
        player.setGameMode(GameMode.ADVENTURE);

        lobby.getUserSettingsAPI().getBooleanSetting(player, "realTime", (s) -> {
            if (s) {
                player.setPlayerTime(lobby.getRealTime().getTime(), false);
            } else {
                player.resetPlayerTime();
            }
        });

        lobby.getUserSettingsAPI().getBooleanSetting(player, "playerVisibility", (value) -> {
            if (Boolean.FALSE.equals(value)) {
                Bukkit.getOnlinePlayers().forEach(p -> player.hidePlayer(lobby, p));
            }
        });
    }

}
