package dev.nachwahl.lobby.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.idb.Database;
import co.aikar.idb.DbRow;
import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.language.Language;
import eu.decentsoftware.holograms.api.DHAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

@CommandAlias("botm")
public class BOTMCommand extends BaseCommand {

    @Dependency
    private static Lobby lobby;

    public BOTMCommand(Lobby lobby) {
        lobby = lobby;
    }

    private static final int entries = 8;


    @CommandPermission("bteg.lobby.botm")
    @Subcommand("show")
    public void onBOTMCreate(CommandSender sender) throws SQLException {
        Player player = (Player) sender;

        create(player.getLocation(), lobby.getDatabase(), lobby.getLanguageAPI().getLanguage(player))
                .thenAccept(player::sendMessage);
    }

    @CommandPermission("bteg.lobby.botm")
    @Subcommand("move")
    public void onBOTMMove(CommandSender sender) throws SQLException {
        DHAPI.moveHologram("BOTM", ((Player)sender).getLocation());
        lobby.getLocationAPI().setLocation(((Player) sender).getLocation(), "botm");
        lobby.getLanguageAPI().sendMessageToPlayer((Player) sender, "botm.moved");
    }

    @CommandPermission("bteg.lobby.botm")
    @Subcommand("unshow")
    public void onBOTMUnshow(CommandSender sender) {
        DHAPI.removeHologram("BOTM");
        lobby.getLanguageAPI().sendMessageToPlayer((Player) sender, "botm.unshow");
    }

    @CommandPermission("bteg.lobby.botm")
    @Syntax("<player> <month> <year>")
    @Subcommand("add")
    @CommandCompletion("* 1|2|3|4|5|6|7|8|9|10|11|12")
    public void onBOTMAdd(CommandSender sender, String target, int month, int year) throws SQLException {

        Player player = (Player) sender;
        String uuid = Bukkit.getOfflinePlayer(target).getUniqueId().toString();

        lobby.getBotmScoreAPI().addPoints(uuid, 1);
        lobby.getConfig().set("lastBOTM.UUID", uuid);
        lobby.getConfig().set("lastBOTM.month", month);
        lobby.getConfig().set("lastBOTM.year", year);
        lobby.saveConfig();

        update(player);

        lobby.getLanguageAPI().sendMessageToPlayer(player, "botm.added");
    }

    @CommandPermission("bteg.lobby.botm")
    @Syntax("<player>")
    @Subcommand("remove")
    public void onBOTMRemove(CommandSender sender, String target) throws SQLException {

        Player player = (Player) sender;
        String uuid = Bukkit.getOfflinePlayer(target).getUniqueId().toString();

        lobby.getBotmScoreAPI().addPoints(uuid, -1);
        update(player);

        lobby.getLanguageAPI().sendMessageToPlayer(player, "botm.removed");
    }

    @CommandPermission("bteg.lobby.botm")
    @Subcommand("set")
    @Syntax("<player> <score>")
    public void onBOTMSet(CommandSender sender, String target, int score) throws SQLException {

        Player player = (Player) sender;
        String uuid = Bukkit.getOfflinePlayer(target).getUniqueId().toString();

        lobby.getBotmScoreAPI().setScore(uuid, score);
        update(player);

        lobby.getLanguageAPI().sendMessageToPlayer(player, "botm.set");
    }

    @Subcommand("list")
    public void onBOTMList(CommandSender sender) throws SQLException {
        Player player = (Player) sender;

        HashMap<String, Integer> scores = new HashMap<>();
        List<DbRow> dbRows = lobby.getDatabase().getResults("SELECT * FROM botm");

        Map.Entry<Integer, String>[] relevantEntries = sortScores(scores, dbRows);


        player.sendMessage(ChatColor.RED + String.valueOf(relevantEntries.length) + ChatColor.WHITE + " Einträge");
        CompletableFuture.runAsync(() -> {
            try {
                for (int i = 0; i < relevantEntries.length; i++) {
                    UUID uuid = UUID.fromString(relevantEntries[i].getValue());
                    player.sendMessage(ChatColor.GOLD + String.valueOf(i + 1) + ". " + ChatColor.WHITE + Bukkit.getOfflinePlayer(uuid).getName() + ": " + ChatColor.GOLD + relevantEntries[i].getKey());
                }
            } catch (InterruptedException | ExecutionException e) {
                lobby.getLogger().info(e::getMessage);
            }
        });

    }

    @CommandPermission("bteg.lobby.botm")
    @Syntax("<player> <month> <year>")
    @Subcommand("latest")
    @CommandCompletion("* 1|2|3|4|5|6|7|8|9|10|11|12")
    public void onBOTMLatest(CommandSender sender, String target, int month, int year) throws SQLException {

        Player player = (Player) sender;
        String uuid = Bukkit.getOfflinePlayer(target).getUniqueId().toString();

        lobby.getConfig().set("lastBOTM.UUID", uuid);
        lobby.getConfig().set("lastBOTM.month", month);
        lobby.getConfig().set("lastBOTM.year", year);
        lobby.saveConfig();

        update(player);
        lobby.getLanguageAPI().sendMessageToPlayer(player, "botm.latest.set");
    }

    public static CompletableFuture<String> create(Location location, Database database, Language language) throws SQLException {

        HashMap<String, Integer> scores = new HashMap<>();
        List<DbRow> dbRows = database.getResults("SELECT * FROM botm");


        Map.Entry<Integer, String>[] relevantEntries = sortScores(scores, dbRows);

        // Create a hologram
        if (scores.size() >= 3) {

            List<String> lines = new ArrayList<>();
            lines.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Build of the Month");

            UUID uuidLatest = UUID.fromString(lobby.getConfig().getString("lastBOTM.UUID"));
            AtomicInteger latestScore = new AtomicInteger();
            lobby.getBotmScoreAPI().getScore(uuidLatest.toString(), latestScore::set);

            return CompletableFuture.supplyAsync(() -> {
                try {
                    lines.add(ChatColor.GOLD + "" + lobby.getConfig().getInt("lastBOTM.month") + "." + lobby.getConfig().getInt("lastBOTM.year") + " " + ChatColor.WHITE + lobby.getBotmScoreAPI().getPlayerName(uuidLatest).get() + " " + ChatColor.GOLD + latestScore.get());

                    lines.add("");

                    for (int i = 0; i < entries; i++) {
                        UUID uuid = UUID.fromString(relevantEntries[i].getValue());
                        lines.add(ChatColor.GOLD + String.valueOf(i + 1) + ". " + ChatColor.WHITE + lobby.getBotmScoreAPI().getPlayerName(uuid).get() + ": " + ChatColor.GOLD + relevantEntries[i].getKey());
                    }
                } catch (InterruptedException | ExecutionException e) {
                    lobby.getLogger().info(e::getMessage);
                }

            lines.add("");

            lines.add("%lobby_ownbotmscore%");

            DHAPI.createHologram("BOTM", location, lines);
            lobby.getLocationAPI().setLocation(location, "botm");

                return lobby.getLanguageAPI().getMessageString(language, "botm.create.success");
            });
        } else {
            // Send feedback
            return CompletableFuture.completedFuture(lobby.getLanguageAPI().getMessageString(language, "botm.create.failed"));

        }
    }

    private static Map.Entry<Integer, String>[] sortScores(HashMap<String, Integer> scores, List<DbRow> dbRows) {
        dbRows.forEach(row -> {
            String uuid = row.getString("uuid");
            int score = row.getInt("score");
            scores.put(uuid, score);
        });

        ArrayList<String> keys = new ArrayList<>(scores.keySet());

        Map.Entry<Integer, String>[] relevantEntries = new Map.Entry[scores.size()];



        for (int i = 0; i < scores.size(); i++) {

            TreeMap<Integer, String> treeMap = new TreeMap<>();

            for (String key : keys) {
                treeMap.put(scores.get(key), key);
            }
            relevantEntries[i] = treeMap.lastEntry();
            keys.remove(relevantEntries[i].getValue());
        }
        return relevantEntries;
    }

    public void update(Player player) throws SQLException {
        if (DHAPI.getHologram("BOTM") != null) {
            Location location = DHAPI.getHologram("BOTM").getLocation();

            DHAPI.removeHologram("BOTM");
            create(location, this.lobby.getDatabase(), this.lobby.getLanguageAPI().getLanguage(player));
        }
    }

}
