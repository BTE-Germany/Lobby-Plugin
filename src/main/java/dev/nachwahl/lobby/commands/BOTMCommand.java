package dev.nachwahl.lobby.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import dev.nachwahl.lobby.Lobby;
import eu.decentsoftware.holograms.api.DHAPI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

@CommandAlias("botm")
public class BOTMCommand extends BaseCommand {

    @Dependency
    private Lobby lobby;

    private static final int entries = 3;


    @CommandPermission("bteg.lobby.botm")
    @Subcommand("create")
    public void onBOTMCreate(CommandSender sender) {
        create(((Player)sender).getLocation());
    }

    @CommandPermission("bteg.lobby.botm")
    @Subcommand("move")
    public void onBOTMMove(CommandSender sender) {
        DHAPI.moveHologram("BOTM", ((Player)sender).getLocation());
    }

    @CommandPermission("bteg.lobby.botm")
    @Subcommand("remove")
    public void onBOTMRemove(CommandSender sender) {
        DHAPI.removeHologram("BOTM");
    }

    @CommandPermission("bteg.lobby.botm")
    @Subcommand("add <player>")
    public void onBOTMAdd(String player) {

        this.lobby.getBotmScoreAPI().addPoints(player);
        update(true);

    }

    public String create(Location location) {

        HashMap<String, Integer> scores = new HashMap<>();

        this.lobby.getBotmScoreAPI().getAllScores(scores::putAll);

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
            for (int i = 0; i < entries; i++)
                lines.add(ChatColor.GRAY + relevantEntries[i].getValue() + ": " + ChatColor.GREEN + relevantEntries[i].getKey());

            DHAPI.createHologram("BOTM", location, lines);

            return ChatColor.GREEN + "[BTE] Hologramm wurde erfolgreich erstellt!";
        } else {
            // Send feedback
            return ChatColor.YELLOW + "[BTE] Es existieren nicht genügend Einträge um ein Hologramm zu erstellen!";

        }
    }

    public void update(boolean sendFeedback) {
        if (DHAPI.getHologram("BOTM") != null) {
            Location location = DHAPI.getHologram("BOTM").getLocation();

            DHAPI.removeHologram("BOTM");
            create(location);
        }
    }

}
