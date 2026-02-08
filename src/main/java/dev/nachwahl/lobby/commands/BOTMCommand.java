package dev.nachwahl.lobby.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.nachwahl.lobby.LobbyPlugin;
import dev.nachwahl.lobby.guis.botm.BOTMGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("botm")
public class BOTMCommand extends BaseCommand {

    private final LobbyPlugin lobbyPlugin;

    public BOTMCommand(LobbyPlugin lobbyPlugin) {
        this.lobbyPlugin = lobbyPlugin;
    }

    @Default
    @CommandPermission("bteg.lobby.botm")
    public void onBOTMCommand(CommandSender sender) {
        Player player = (Player) sender;
        new BOTMGUI(lobbyPlugin, player);
    }
}
