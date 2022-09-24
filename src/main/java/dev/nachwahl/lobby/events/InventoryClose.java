package dev.nachwahl.lobby.events;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.utils.guis.LanguageGUI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.sql.SQLException;

public class InventoryClose implements Listener {

    private Lobby lobby;

    public InventoryClose(Lobby lobby) {
        this.lobby = lobby;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) throws SQLException {
        if (event.getReason() == InventoryCloseEvent.Reason.PLAYER) {
            var mm = MiniMessage.miniMessage();
            if (event.getView().title().toString().contains("Datenschutz")) {
                Player player = (Player) event.getPlayer();
                player.kick(mm.deserialize("<red><b>Sorry :C</b></red>\n" +
                        "\n" +
                        "<gray>You have to accept the privacy policy.</gray>\n" +
                        "<gray>Du musst der Datenschutzerklärung zustimmen.</gray>"));
            }
            if (event.getView().title().toString().contains("Sprache")) {

            }

        }
    }
}
