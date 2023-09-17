package dev.nachwahl.lobby.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.guis.AccountGUI;
import dev.nachwahl.lobby.guis.NavigatorGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@CommandAlias("navigator|nav")
public class NavigatorCommand extends BaseCommand {
    @Dependency
    private Lobby plugin;


    @Default
    public void onNavigatorCommand(CommandSender sender) {
        Player player = (Player) sender;
        System.out.println("ttt");
        new NavigatorGUI(plugin, player);
    }

}
