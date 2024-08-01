package dev.nachwahl.lobby.scoreboard;

import dev.nachwahl.lobby.Lobby;
import fr.mrmicky.fastboard.adventure.FastBoard;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Scoreboard {

    private final HashMap<UUID, FastBoard> scoreboards = new HashMap<>();
    private final Lobby lobby;

    public Scoreboard(Lobby lobby) {
        this.lobby = lobby;
    }

    public void initScoreboard(Player player) {
        FastBoard board = new FastBoard(player);
        board.updateTitle(this.lobby.getMiniMessage().deserialize("       <b><gradient:#262b44:#e63c45:#ffaf36>BTE Germany</gradient></b>       "));
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
            board.updateLine(0, this.lobby.getMiniMessage().deserialize(""));
            board.updateLine(1, this.lobby.getMiniMessage().deserialize("<gray>▶</gray> <color:#f23a29><b>Gems</b></color>"));
            board.updateLine(2, this.lobby.getMiniMessage().deserialize("   <color:#bdbdb1>187</color> ௰"));
            board.updateLine(3, this.lobby.getMiniMessage().deserialize(""));
        }
    }
}
