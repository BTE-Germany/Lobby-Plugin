package dev.nachwahl.lobby.utils;

import co.aikar.commands.annotation.Dependency;
import dev.nachwahl.lobby.Lobby;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class BOTMPlaceholder extends PlaceholderExpansion {

    @Dependency
    private static Lobby lobby;

    public BOTMPlaceholder(Lobby lobby) {
        this.lobby = lobby;
    }

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

            //Get Playername and score
            String position = lobby.getLanguageAPI().getMessageString(this.lobby.getLanguageAPI().getLanguage(player), "botm.no_position");
            try {
                List<Map.Entry<String, Integer>> scores = lobby.getBotmScoreAPI().sortScores();
                for(Map.Entry<String, Integer> entry : scores) {
                    if (entry.getKey().equals(uuid.toString())) {
                        position = String.valueOf(scores.indexOf(entry) + 1);
                        break;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


            String playerName = player.getName();
            int score;
            try {
                score = lobby.getBotmScoreAPI().getScore(uuid);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return ChatColor.GOLD + "" + ChatColor.BOLD + position + ". " + ChatColor.WHITE + ChatColor.BOLD + playerName + ": " + ChatColor.GOLD + ChatColor.BOLD + score;
        }
        return null;
    }

}
