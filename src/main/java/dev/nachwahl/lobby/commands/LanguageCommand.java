package dev.nachwahl.lobby.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import dev.nachwahl.lobby.LobbyPlugin;
import dev.nachwahl.lobby.guis.LanguageGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("language|lang")
public class LanguageCommand extends BaseCommand {

    @Dependency
    private LobbyPlugin lobbyPlugin;

    @Default
    public void onLanguageCommand(CommandSender sender) {
        Player player = (Player) sender;
        new LanguageGUI(lobbyPlugin, player);
    }
}
