package dev.nachwahl.lobby.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.guis.TutorialGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@CommandAlias("tutorial|help")
public class TutorialCommand extends BaseCommand {
    @Dependency
    private Lobby plugin;


    @Default
    public void onTutorialCommand(CommandSender sender) {
        Player player = (Player) sender;
        new TutorialGUI(plugin, player);
    }

}
