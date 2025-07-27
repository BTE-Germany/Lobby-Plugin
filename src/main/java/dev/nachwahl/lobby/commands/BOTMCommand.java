package dev.nachwahl.lobby.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.guis.botm.BOTMGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("botm")
public class BOTMCommand extends BaseCommand {

    @Dependency
    private static Lobby lobby;

    public BOTMCommand(Lobby lobby) {
        lobby = lobby;
    }

    @Default
    @CommandPermission("bteg.lobby.botm")
    public void onBOTMCommand(CommandSender sender) {
        Player player = (Player) sender;
        new BOTMGUI(lobby, player);
    }
/*

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

    @Subcommand("list")
    public void onBOTMList(CommandSender sender) throws SQLException {
        Player player = (Player) sender;

        HashMap<String, Integer> scores = new HashMap<>();
        List<DbRow> dbRows = lobby.getDatabase().getResults("SELECT * FROM botm");

        Map.Entry<Integer, String>[] relevantEntries = sortScores(scores, dbRows);


        for (int i = 0; i < relevantEntries.length; i++) {
            UUID uuid = UUID.fromString(relevantEntries[i].getValue());
            player.sendMessage(ChatColor.GOLD + String.valueOf(i + 1) + ". " + ChatColor.WHITE + Bukkit.getOfflinePlayer(uuid).getName() + ": " + ChatColor.GOLD + relevantEntries[i].getKey());
        }

    }

*/


    /*
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

    }
*/
}
