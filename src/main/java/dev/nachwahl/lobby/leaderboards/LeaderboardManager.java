package dev.nachwahl.lobby.leaderboards;

import dev.nachwahl.lobby.Lobby;
import lombok.Getter;

import java.sql.SQLException;

public class LeaderboardManager {

    @Getter
    private PlaytimeLeaderboard playtimeLeaderboard;

    private Lobby lobby;

    public LeaderboardManager(Lobby lobby) throws SQLException {
        this.lobby = lobby;

        // Reduces performance on startup (loads Plan players)
        //load();
    }

    public void load() throws SQLException {
        playtimeLeaderboard = new PlaytimeLeaderboard(lobby,"lb.playtime");

    }


}
