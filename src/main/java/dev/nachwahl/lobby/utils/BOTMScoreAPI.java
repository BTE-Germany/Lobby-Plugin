package dev.nachwahl.lobby.utils;

import co.aikar.idb.DbRow;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.nachwahl.lobby.Lobby;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class BOTMScoreAPI {


    private final Cache<String, Integer> scoreCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();
    private final Lobby lobby;

    public BOTMScoreAPI(Lobby lobby) {
        this.lobby = lobby;
    }

    public void setScore(String uuid, int score) throws SQLException {
        scoreCache.put(uuid, score);

        this.lobby.getDatabase().executeUpdate("INSERT INTO botm (uuid, score) VALUES (?, ?) ON DUPLICATE KEY UPDATE score = VALUES(score)", uuid, score);
    }

    public void getScore(String uuid, Consumer<Integer> callback) throws SQLException {
        Integer cache = scoreCache.getIfPresent(uuid);

        if (cache == null) {
            DbRow dbRow = this.lobby.getDatabase().getFirstRow("SELECT * FROM botm WHERE uuid = ?", uuid);
            if (dbRow == null) {
                callback.accept(0);
            } else {
                scoreCache.put(uuid, dbRow.get("score"));
                callback.accept(dbRow.get("score"));
            }
        }else {
            callback.accept(cache);
        }
    }

    public void addPoints(String uuid, int points) throws SQLException {
        getScore(uuid, score -> {
            try{
                if (score == null) {
                    setScore(uuid, points);
                }else {
                    setScore(uuid, score + points);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
