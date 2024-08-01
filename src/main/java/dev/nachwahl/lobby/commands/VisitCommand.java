package dev.nachwahl.lobby.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.guis.VisitGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@CommandAlias("visit|map")
public class VisitCommand extends BaseCommand {
    @Dependency
    private Lobby plugin;


    @Default
    public void onVisitCommand(CommandSender sender) {
        Player player = (Player) sender;
        new VisitGUI(plugin, player);
    }

}
