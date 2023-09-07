package dev.nachwahl.lobby.leaderboards;

import dev.nachwahl.lobby.Lobby;
import lombok.Getter;

import java.sql.SQLException;

public class LeaderboardManager {

    @Getter
    private PlaytimeLeaderboard playtimeLeaderboard;

    public LeaderboardManager(Lobby lobby) throws SQLException {
        playtimeLeaderboard = new PlaytimeLeaderboard(lobby,"lb.playtime");
    }


}
