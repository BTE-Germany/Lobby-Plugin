package dev.nachwahl.lobby.scoreboard;

import dev.nachwahl.cosmetics.Cosmetics;
import dev.nachwahl.lobby.Lobby;
import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.*;

public class Scoreboard {

    private final Lobby lobby;
    private final Cosmetics cosmetics;
    private final Map<UUID, FastBoard> scoreboards = new HashMap<>();

    public Scoreboard(Lobby lobby, Cosmetics cosmetics) {
        this.lobby = lobby;
        this.cosmetics = cosmetics;
    }

    public void initScoreboard(Player player) {
        if(this.scoreboards.containsKey(player.getUniqueId())) {
            return;
        }
        FastBoard board = new FastBoard(player);
        board.updateTitle(Component.text("\uE350"));
        board.updateLines(getUpdatedLines(player));
        this.scoreboards.put(player.getUniqueId(), board);
    }

    public void removeScoreboard(Player player) {
        FastBoard board = this.scoreboards.remove(player.getUniqueId());
        if (board != null) {
            board.delete();
        }
    }

    public void updateScoreboards() {
        for (FastBoard board : this.scoreboards.values()) {
            board.updateLines(getUpdatedLines(board.getPlayer()));
        }
    }

    public List<Component> getUpdatedLines(Player player) {
        List<Component> backupLines = List.of();
        if(this.scoreboards.get(player.getUniqueId())!=null){
            backupLines = this.scoreboards.get(player.getUniqueId()).getLines();
        }

        List<Component> lines = new ArrayList<>();
        this.cosmetics.getLanguageAPI().getLanguage(player, language -> {
            this.cosmetics.getGemsAPI().getBalance(player, gems -> {
                long playtime = this.cosmetics.getPlaytimeHandler().getPlaytime(player.getUniqueId());
                lines.add(Component.empty());
                lines.add(Component.text("§7Gems"));
                lines.add(Component.text("௴ " + gems));
                lines.add(Component.empty());
                lines.add(cosmetics.getLanguageAPI().getMessage(language, "scoreboard.playtime"));
                lines.add(Component.text("ꭑ " + formatPlaytime(playtime)));
                lines.add(Component.empty());
            });
        });

        if(!lines.isEmpty()){
            return lines;
        }else{
            return backupLines;
        }
    }

    public static String formatPlaytime(long seconds) {
        long days = seconds / (24 * 3600);
        seconds %= (24 * 3600);
        long hours = seconds / 3600;
        seconds %= 3600;
        long minutes = seconds / 60;

        StringBuilder sb = new StringBuilder();

        if (days > 0) {
            sb.append(days).append(" Tag");
            if (days > 1) {
                sb.append("e");
            }
            sb.append(" ");
        }

        sb.append(hours).append("h ").append(minutes).append("min");

        return sb.toString();
    }
}
