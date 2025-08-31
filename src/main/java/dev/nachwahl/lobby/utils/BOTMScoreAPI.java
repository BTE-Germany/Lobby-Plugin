package dev.nachwahl.lobby.utils;

import co.aikar.commands.annotation.Dependency;
import co.aikar.idb.Database;
import co.aikar.idb.DbRow;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.language.Language;
import eu.decentsoftware.holograms.api.DHAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.bukkit.OfflinePlayer;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CompletableFuture;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class BOTMScoreAPI {

    @Dependency
    private static Lobby lobby;

    private final Map<UUID, String> playerNames = new HashMap<>();

    private static final int entries = 3;

    public BOTMScoreAPI(Lobby lobby) {
        this.lobby = lobby;
    }


    public boolean addEntry(String name, int year, int month, String player1_name, @Nullable String player2_name, @Nullable String player3_name) throws SQLException {
        DbRow dbRow = this.lobby.getDatabase().getFirstRow(
                "SELECT * FROM botm WHERE year = ? AND month = ?", year, month
        );
        if (dbRow != null) {
            // Eintrag existiert bereits für diesen Monat und Jahr
            return false;
        }

        // Hole die UUIDs der Spieler anhand der Namen
        UUID player1_uuid = Bukkit.getOfflinePlayer(player1_name).getUniqueId();
        UUID player2_uuid = null;
        UUID player3_uuid = null;
        if (player2_name != null) {
            player2_uuid = Bukkit.getOfflinePlayer(player2_name).getUniqueId();
        }
        if (player3_name != null) {
            player3_uuid = Bukkit.getOfflinePlayer(player3_name).getUniqueId();
        }

        this.lobby.getDatabase().executeUpdate(
                "INSERT INTO botm (name, year, month, player1_uuid, player2_uuid, player3_uuid) VALUES (?, ?, ?, ?, ?, ?)",
                name,
                year,
                month,
                player1_uuid.toString(),
                player2_uuid != null ? player2_uuid.toString() : null,
                player3_uuid != null ? player3_uuid.toString() : null
        );
        return true;
    }

    /**
     * Gibt die Anzahl der Einträge zurück, in denen der Spieler (per Name) als Spieler vorkommt.
     */
    public int getScore(String playerName) throws SQLException {
        String uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId().toString();
        DbRow row = this.lobby.getDatabase().getFirstRow(
            "SELECT COUNT(*) AS cnt FROM botm WHERE player1_uuid = ? OR player2_uuid = ? OR player3_uuid = ?",
                uuid.toString(), uuid.toString(), uuid.toString()
        );
        if (row != null) {
            return row.getInt("cnt");
        }
        return 0;
    }

    public int getScore(UUID uuid) throws SQLException {
        DbRow row = this.lobby.getDatabase().getFirstRow(
                "SELECT COUNT(*) AS cnt FROM botm WHERE player1_uuid = ? OR player2_uuid = ? OR player3_uuid = ?",
                uuid.toString(), uuid.toString(), uuid.toString()
        );
        if (row != null) {
            return row.getInt("cnt");
        }
        return 0;
    }

    public String create(Location location, Database database, Language language) throws SQLException, ExecutionException, InterruptedException {

        List<Map.Entry<String, Integer>> scores = sortScores();

        // Create a hologram
        if (scores.size() >= entries) {

            //Headline
            List<String> lines = new ArrayList<>();
            lines.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Build of the Month");

            // Get the latest BOTM UUID and score
            List<DbRow> dbRows = database.getResults("SELECT player1_uuid, month, year FROM botm ORDER BY year DESC, month DESC LIMIT 1");
            if (dbRows.isEmpty()) {
                return null;
            }
            DbRow row = dbRows.get(0);

            int score = lobby.getBotmScoreAPI().getScore(UUID.fromString(row.getString("player1_uuid")));

            lines.add(ChatColor.GOLD + "" + row.getInt("month") + "/" + row.getInt("year") + " " + ChatColor.WHITE + lobby.getBotmScoreAPI().getPlayerName(UUID.fromString(row.getString("player1_uuid"))).get() + ": " + ChatColor.GOLD + score);

            lines.add("");

            // Add the top entries to the hologram
            for (int i = 0; i < entries; i++){
                UUID uuid = UUID.fromString(scores.get(i).getKey());
                lines.add(ChatColor.GOLD + String.valueOf(i + 1) + ". " + ChatColor.WHITE + lobby.getBotmScoreAPI().getPlayerName(uuid).get() + ": " + ChatColor.GOLD + scores.get(i).getValue());
            }

            lines.add("");

            // Add the own score placeholder
            lines.add("%lobby_ownbotmscore%");

            // Create the hologram
            DHAPI.createHologram("BOTM", location, lines);

            return lobby.getLanguageAPI().getMessageString(language, "botm.create.success");
        } else {
            return lobby.getLanguageAPI().getMessageString(language, "botm.create.failed");

        }
    }

    public void reload(Player player) throws SQLException, ExecutionException, InterruptedException {
        if (DHAPI.getHologram("BOTM") != null) {
            Location location = DHAPI.getHologram("BOTM").getLocation();

            DHAPI.removeHologram("BOTM");
            create(location, this.lobby.getDatabase(), this.lobby.getLanguageAPI().getLanguage(player));
        }
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

    public List<Map.Entry<String, Integer>> sortScores() throws SQLException {
        List<DbRow> dbRows = lobby.getDatabase().getResults("SELECT * FROM botm");
        Map<String, Integer> scores = new HashMap<>();

        for (DbRow row : dbRows) {
            for (String col : Arrays.asList("player1_uuid", "player2_uuid", "player3_uuid")) {
                String uuid = row.getString(col);
                if (uuid != null && !uuid.isEmpty()) {
                    scores.put(uuid, scores.getOrDefault(uuid, 0) + 1);
                }
            }
        }

        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(scores.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        return sorted;
    }

}
