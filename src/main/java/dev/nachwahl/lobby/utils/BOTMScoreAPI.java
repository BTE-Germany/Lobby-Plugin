package dev.nachwahl.lobby.utils;

import co.aikar.commands.annotation.Dependency;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.nachwahl.lobby.Lobby;
import org.bukkit.Location;

import java.util.HashMap;
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

    public void setScore(String name, int score) {
        scoreCache.put(name, score);

        this.lobby.getDatabase().executeUpdateAsync("INSERT INTO botm (name, score) VALUES (?, ?) ON DUPLICATE KEY UPDATE score = ?", name, score, score);
    }

    public void getScore(String name, Consumer<Integer> callback) {
        Integer cache = scoreCache.getIfPresent(name);

        if (cache == null) {
            this.lobby.getDatabase().getFirstRowAsync("SELECT * FROM botm WHERE name = ?", name).thenAccept(dbRow -> {
                if (dbRow == null) {
                    callback.accept(0);
                } else {
                    scoreCache.put(name, dbRow.get("score"));
                    callback.accept(dbRow.get("score"));
                }
            });
        }else {
            callback.accept(cache);
        }
    }

    public void addPoints(String name) {
        getScore(name, score -> {
            if (score == null) {
                setScore(name, 1);
            }else {
                setScore(name, score + 1);
            }
        });
    }

    public void getAllScores(Consumer<HashMap<String, Integer>> callback) {
        this.lobby.getDatabase().getResultsAsync("SELECT * FROM botm").thenAccept(dbRows -> {
            HashMap<String, Integer> scores = new HashMap<>();
            dbRows.forEach(row -> {
                String name = row.getString("name");
                int score = row.getInt("score");
                scores.put(name, score);
            });
            callback.accept(scores);
        });
    }

    public void saveLocation(Location location) {
        this.lobby.getDatabase().executeUpdateAsync("INSET INTO botmLocation (location) VALUES (?)", location);
    }

    public void getLocations(Consumer<Location> callback) {
        this.lobby.getDatabase().getFirstRowAsync("SELECT * FROM botmLocation").thenAccept(dbRow -> {
            if (dbRow == null) {
                callback.accept(null);
            } else {
                callback.accept(dbRow.get("location"));
            }
        });
    }

    public void clearLocation() {
        this.lobby.getDatabase().executeUpdateAsync("DELETE FROM botmLocation");
    }

}
