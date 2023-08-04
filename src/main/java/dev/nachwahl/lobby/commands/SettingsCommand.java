package dev.nachwahl.lobby.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.guis.AccountGUI;
import dev.nachwahl.lobby.guis.LanguageGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@CommandAlias("settings|setting|profile|account|acc")
public class SettingsCommand extends BaseCommand {
    @Dependency
    private Lobby plugin;


    @Default
    public void onSettingsCommand(CommandSender sender) {
        Player player = (Player) sender;
        new AccountGUI(plugin, player);
    }

}
