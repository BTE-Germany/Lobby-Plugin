package dev.nachwahl.lobby.utils;

import co.aikar.idb.Database;
import co.aikar.idb.DbRow;
import dev.nachwahl.lobby.LobbyPlugin;
import dev.nachwahl.lobby.leaderboards.JnRLeaderboard;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

public class LobbyPlaceholderExpansion extends PlaceholderExpansion {

    private final LobbyPlugin lobbyPlugin;
    private final Database database;

    public LobbyPlaceholderExpansion(LobbyPlugin lobbyPlugin) {
        this.lobbyPlugin = lobbyPlugin;
        this.database = lobbyPlugin.getDatabase();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "bteg-lobby";
    }

    @Override
    public @NotNull String getAuthor() {
        return "BTE Germany Lobby authors";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.equalsIgnoreCase("own-jnr-score")) {
            DbRow dbRow;
            try {
                dbRow = this.database.getFirstRow("SELECT score, player, pos FROM (SELECT player, Row_Number() OVER(ORDER BY score DESC) AS pos, score FROM parkour_scores WHERE area = \"JnR\") AS filtered WHERE player = ?", player.getUniqueId().toString());
            } catch (SQLException e) {
                this.lobbyPlugin.getLogger().warning(e.toString());
                return this.lobbyPlugin.getLanguageAPI().getMessageString(this.lobbyPlugin.getLanguageAPI().getLanguage(player), "placeholder.playtime.error");
            }

            if (dbRow == null) {
                return "";
            }

            int rank = dbRow.getInt("pos");
            int score = dbRow.getInt("score");
            return JnRLeaderboard.getLineContent(rank, player.getName(), score, true);
        }
        return null;
    }


}
