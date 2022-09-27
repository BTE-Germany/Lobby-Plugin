package dev.nachwahl.lobby.events;

import co.aikar.idb.DbRow;
import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.utils.HotbarItems;
import dev.nachwahl.lobby.utils.guis.PrivacyGUI;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {

    private Lobby lobby;

    public JoinEvent(Lobby lobby) {
        this.lobby = lobby;
    }

    @SneakyThrows
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        DbRow user = this.lobby.getDatabase().getFirstRow("SELECT * FROM privacy WHERE minecraftUUID = ?", player.getUniqueId().toString());
        if (user == null) {
            new PrivacyGUI(player, this.lobby).getGui().open(player);
        }
        this.lobby.getHotbarItems().setHotbarItems(player);

    }
}
