package dev.nachwahl.lobby.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.idb.Database;
import co.aikar.idb.DbRow;
import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.language.Language;
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

        player.sendMessage(create(player.getLocation(), this.lobby.getDatabase(), this.lobby.getLanguageAPI().getLanguage(player)));
    }

    @CommandPermission("bteg.lobby.botm")
    @Subcommand("move")
    public void onBOTMMove(CommandSender sender) throws SQLException {
        DHAPI.moveHologram("BOTM", ((Player)sender).getLocation());
        this.lobby.getLocationAPI().setLocation(((Player) sender).getLocation(), "botm");
        this.lobby.getLanguageAPI().sendMessageToPlayer((Player) sender, "botm.moved");
    }

    @CommandPermission("bteg.lobby.botm")
    @Subcommand("remove")
    public void onBOTMRemove(CommandSender sender) {
        DHAPI.removeHologram("BOTM");
        this.lobby.getLanguageAPI().sendMessageToPlayer((Player) sender, "botm.removed");
    }

    @CommandPermission("bteg.lobby.botm")
    @Syntax("<player>")
    @Subcommand("add")
    public void onBOTMAdd(CommandSender sender, String target) throws SQLException {

        Player player = (Player) sender;

        this.lobby.getBotmScoreAPI().addPoints(target);
        update(player);

        this.lobby.getLanguageAPI().sendMessageToPlayer(player, "botm.added");
    }

    public static String create(Location location, Database database, Language language) throws SQLException {

        HashMap<String, Integer> scores = new HashMap<>();
        List<DbRow> dbRows = database.getResults("SELECT * FROM botm");

        dbRows.forEach(row -> {
            String name = row.getString("name");
            int score = row.getInt("score");
            scores.put(name, score);
        });

        // Create a hologram
        if (scores.size() >= 3) {

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
            lobby.getLocationAPI().setLocation(location, "botm");

            return lobby.getLanguageAPI().getMessageString(language, "botm.create.success");
        } else {
            // Send feedback
            return lobby.getLanguageAPI().getMessageString(language, "botm.create.failed");

        }
    }

    public void update(Player player) throws SQLException {
        if (DHAPI.getHologram("BOTM") != null) {
            Location location = DHAPI.getHologram("BOTM").getLocation();

            DHAPI.removeHologram("BOTM");
            create(location, this.lobby.getDatabase(), this.lobby.getLanguageAPI().getLanguage(player));
        }
    }

}
