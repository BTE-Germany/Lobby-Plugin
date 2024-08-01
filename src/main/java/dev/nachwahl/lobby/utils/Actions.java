package dev.nachwahl.lobby.utils;

import dev.nachwahl.lobby.Lobby;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

public class Actions {

    public static void performJoinActions(Lobby lobby, Player player) {
        lobby.getHotbarItems().setHotbarItems(player);
        lobby.getLanguageAPI().getLanguage(player, language -> {
            lobby.getHologramAPI().showHolograms(player, language);
        });
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
            if (!value) {
                Bukkit.getOnlinePlayers().forEach((p) -> player.hidePlayer(lobby, p));
            }
        });
    }

}
