package dev.nachwahl.lobby.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.idb.Database;
import co.aikar.idb.DbRow;
import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.utils.BOTMScoreAPI;
import eu.decentsoftware.holograms.api.DHAPI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;

@CommandAlias("botm")
public class BOTMCommand extends BaseCommand {

    @Dependency
    private static Lobby lobby;

    public BOTMCommand(Lobby lobby) {
        this.lobby = lobby;
    }

    private static final int entries = 3;


    @CommandPermission("bteg.lobby.botm")
    @Subcommand("create")
    public void onBOTMCreate(CommandSender sender) throws SQLException {
        Player player = (Player) sender;

        player.sendMessage(create(player.getLocation(), this.lobby.getDatabase()));
    }

    @CommandPermission("bteg.lobby.botm")
    @Subcommand("move")
    public void onBOTMMove(CommandSender sender) throws SQLException {
        DHAPI.moveHologram("BOTM", ((Player)sender).getLocation());
        this.lobby.getBotmScoreAPI().saveLocation(((Player)sender).getLocation());
        sender.sendMessage(ChatColor.GREEN + "[BTE] Hologramm wurde verschoben!");
    }

    @CommandPermission("bteg.lobby.botm")
    @Subcommand("remove")
    public void onBOTMRemove(CommandSender sender) {
        DHAPI.removeHologram("BOTM");
        sender.sendMessage(ChatColor.GREEN + "[BTE] Hologramm wurde entfernt!");
    }

    @CommandPermission("bteg.lobby.botm")
    @Syntax("<player>")
    @Subcommand("add")
    public void onBOTMAdd(CommandSender sender, String target) throws SQLException {

        Player player = (Player) sender;

        this.lobby.getBotmScoreAPI().addPoints(target);
        update(true);

        player.sendMessage(ChatColor.GREEN + "[BTE] Punkte wurden hinzugefügt!");

    }

    public static String create(Location location, Database database) throws SQLException {

        HashMap<String, Integer> scores = new HashMap<>();
        List<DbRow> dbRows = database.getResults("SELECT * FROM botm");

        dbRows.forEach(row -> {
            String name = row.getString("name");
            int score = row.getInt("score");
            scores.put(name, score);
        });

        // Create a hologram
        if (scores.size() > 3) {

            ArrayList<String> keys = new ArrayList<>(scores.keySet());

            Map.Entry<Integer, String>[] relevantEntries = new Map.Entry[entries];

            for (int i = 0; i < entries; i++) {

                TreeMap<Integer, String> map = new TreeMap<>();

                for (String key : keys) {
                    map.put(scores.get(key), key);
                }
                relevantEntries[i] = map.lastEntry();
                keys.remove(relevantEntries[i].getValue());
            }

            List<String> lines = new ArrayList<>();
            lines.add(ChatColor.GOLD + "Build of the Month");
            for (int i = 0; i < entries; i++) lines.add(ChatColor.GRAY + relevantEntries[i].getValue() + ": " + ChatColor.GREEN + relevantEntries[i].getKey());

            DHAPI.createHologram("BOTM", location, lines);
            lobby.getBotmScoreAPI().saveLocation(location);

            return ChatColor.GREEN + "[BTE] Hologramm wurde erfolgreich erstellt!";
        } else {
            // Send feedback
            return ChatColor.YELLOW + "[BTE] Es existieren nicht genügend Einträge um ein Hologramm zu erstellen!";

        }
    }

    public void update(boolean sendFeedback) throws SQLException {
        if (DHAPI.getHologram("BOTM") != null) {
            Location location = DHAPI.getHologram("BOTM").getLocation();

            DHAPI.removeHologram("BOTM");
            create(location, this.lobby.getDatabase());
        }
    }

}
