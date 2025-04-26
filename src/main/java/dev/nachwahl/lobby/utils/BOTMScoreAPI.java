package dev.nachwahl.lobby.utils;

import co.aikar.idb.DbRow;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.nachwahl.lobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class BOTMScoreAPI {


    private final Cache<String, Integer> scoreCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();
    private final Map<UUID, String> playerNames = new HashMap<>();
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
                } else {
                    setScore(uuid, score + points);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<String> getPlayerName(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        if (player.getName() != null) {
            return CompletableFuture.completedFuture(player.getName());
        }
        if (this.playerNames.containsKey(uuid)) {
            return CompletableFuture.completedFuture(this.playerNames.get(uuid));
        }
        return CompletableFuture.supplyAsync(() -> this.getPlayerNameHttp(uuid));
    }

    private synchronized String getPlayerNameHttp(UUID uuid) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.mojang.com/user/profile/%s".formatted(uuid.toString())))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // rate limit
            Thread.sleep(1000);

            JSONObject json = new JSONObject(response.body());

            if (response.statusCode() == 204 || response.statusCode() == 404) {
                this.playerNames.put(uuid, "<Unbekannter Spieler>");
                return "<Unbekannter Spieler>";
            }

            if (response.statusCode() == 429) {
                this.lobby.getLogger().warning(() -> "UUID to player name timeout for %s".formatted(uuid.toString()));
                return null;
            }

            String name = json.getString("name");
            this.playerNames.put(uuid, name);
            return name;
        } catch (IOException | InterruptedException e) {
            this.lobby.getLogger().warning(() -> ("Loading name of unknown player %s failed. " + e.getMessage()).formatted(uuid.toString()));
            this.playerNames.put(uuid, "<Unbekannter Spieler>");
            return "<Unbekannter Spieler>";
        }
    }
}
