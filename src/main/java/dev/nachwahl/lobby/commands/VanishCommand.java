package dev.nachwahl.lobby.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import dev.nachwahl.lobby.Lobby;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("vanish|van")
public class VanishCommand extends BaseCommand {


    @Dependency
    private Lobby lobby;

    @CommandPermission("lobby.vanish")
    @Default
    public void onVanish(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Lobby.getInstance().getVanish().toggle(player);
        if (Lobby.getInstance().getVanish().isHidden(player)) {
            Lobby.getInstance().getLanguageAPI().getMessage(player, player::sendMessage, "vanish.off");
        } else {
            Lobby.getInstance().getLanguageAPI().getMessage(player, player::sendMessage, "vanish.on");
        }
    }
}
