package dev.nachwahl.lobby.utils;

import dev.nachwahl.lobby.Lobby;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class BOTMPlaceholder extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "lobby";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Morgon";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (identifier.equals("ownbotmscore")) {

            UUID uuid = player.getUniqueId();
            String result = "";

            //Get Playername and score
            String playerName = player.getName();
            int score;
            try {
                score = Lobby.getInstance().getBotmScoreAPI().getScore(String.valueOf(uuid));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return playerName + ": " + ChatColor.GOLD + score;
        }
        return null;
    }

}
