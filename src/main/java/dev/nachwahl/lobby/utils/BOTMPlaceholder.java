package dev.nachwahl.lobby.utils;

import co.aikar.commands.annotation.Dependency;
import dev.nachwahl.lobby.LobbyPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class BOTMPlaceholder extends PlaceholderExpansion {

    @Dependency
    private static LobbyPlugin lobbyPlugin;

    public BOTMPlaceholder(LobbyPlugin lobbyPlugin) {
        this.lobbyPlugin = lobbyPlugin;
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
            String position = lobbyPlugin.getLanguageAPI().getMessageString(this.lobbyPlugin.getLanguageAPI().getLanguage(player), "botm.no_position");
            try {
                List<Map.Entry<String, Integer>> scores = lobbyPlugin.getBotmScoreAPI().sortScores();

                List<Pair<Integer, Map.Entry<String, Integer>>> ranking = new java.util.ArrayList<>();
                int rank = 1;
                int count = 0;
                for(Map.Entry<String, Integer>score : scores) {
                    count++;
                    if(scores.indexOf(score) == 0) {
                        ranking.add(Pair.of(rank, score));
                        continue;
                    }
                    if(!Objects.equals(score.getValue(), scores.get(scores.indexOf(score) - 1).getValue())) {
                        rank = count;
                    }
                    ranking.add(Pair.of(rank, score));
                }

                for(Map.Entry<Integer, Map.Entry<String, Integer>>rankedEntry : ranking) {
                    if (rankedEntry.getValue().getKey().equals(uuid.toString())) {
                        position = String.valueOf(rankedEntry.getKey());
                        break;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


            String playerName = player.getName();
            int score;
            try {
                score = lobbyPlugin.getBotmScoreAPI().getScore(uuid);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return ChatColor.GOLD + "" + ChatColor.BOLD + position + ". " + ChatColor.WHITE + ChatColor.BOLD + playerName + ": " + ChatColor.GOLD + ChatColor.BOLD + score;
        }
        return null;
    }

}
