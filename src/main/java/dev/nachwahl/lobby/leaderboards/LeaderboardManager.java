package dev.nachwahl.lobby.leaderboards;

import dev.nachwahl.lobby.LobbyPlugin;
import lombok.Getter;

import java.sql.SQLException;

public class LeaderboardManager {

    @Getter
    private PlaytimeLeaderboard playtimeLeaderboard;

    private LobbyPlugin lobbyPlugin;

    public LeaderboardManager(LobbyPlugin lobbyPlugin) throws SQLException {
        this.lobbyPlugin = lobbyPlugin;

        // Reduces performance on startup (loads Plan players)
        //load();
    }

    public void load() throws SQLException {
        playtimeLeaderboard = new PlaytimeLeaderboard(lobbyPlugin, "lb.playtime");

    }


}
