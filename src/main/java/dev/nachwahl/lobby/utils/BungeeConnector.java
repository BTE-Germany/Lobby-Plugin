package dev.nachwahl.lobby.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.nachwahl.lobby.Lobby;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

public class BungeeConnector {
    private Lobby lobby;

    public BungeeConnector(Lobby lobby) {
        this.lobby = lobby;
    }

    public void sendToServer(Player player, String server, boolean showConfirmationMessage) {
        if (showConfirmationMessage)
            this.lobby.getLanguageAPI().sendMessageToPlayer(player, "bungee.switchServer", Placeholder.parsed("server", server));
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("Connect");
        output.writeUTF(server);
        player.sendPluginMessage(this.lobby, "BungeeCord", output.toByteArray());
    }
}
