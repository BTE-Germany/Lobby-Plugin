package dev.nachwahl.lobby.utils;

import co.aikar.idb.DbRow;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.nachwahl.lobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.SQLException;
import java.util.List;
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

    public void setScore(String name, int score) throws SQLException {
        scoreCache.put(name, score);

        this.lobby.getDatabase().executeUpdate("INSERT INTO botm (name, score) VALUES (?, ?) ON DUPLICATE KEY UPDATE score = VALUES(score)", name, score);
    }

    public void getScore(String name, Consumer<Integer> callback) throws SQLException {
        Integer cache = scoreCache.getIfPresent(name);

        if (cache == null) {
            DbRow dbRow = this.lobby.getDatabase().getFirstRow("SELECT * FROM botm WHERE name = ?", name);
            if (dbRow == null) {
                callback.accept(0);
            } else {
                scoreCache.put(name, dbRow.get("score"));
                callback.accept(dbRow.get("score"));
            }
        }else {
            callback.accept(cache);
        }
    }

    public void addPoints(String name) throws SQLException {
        getScore(name, score -> {
            try{
                if (score == null) {
                    setScore(name, 1);
                }else {
                    setScore(name, score + 1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
