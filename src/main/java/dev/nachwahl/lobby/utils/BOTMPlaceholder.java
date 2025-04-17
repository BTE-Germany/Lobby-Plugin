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
             AtomicInteger score = new AtomicInteger();
            //Get score from database
            try {
                Lobby.getInstance().getBotmScoreAPI().getScore(uuid.toString(), scoreInDB -> {
                    //This is a callback, we need to set the result here
                    score.set(Integer.parseInt(String.valueOf(scoreInDB)));
                });
            } catch (SQLException e) {
                score.set(0);
            }

            return player.getName() + ": " + ChatColor.GOLD + score;
        }
        return null;
    }

}
