package dev.nachwahl.lobby.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.nachwahl.lobby.Lobby;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("loc|location")
public class LocationCommand extends BaseCommand {

    @Dependency
    private Lobby lobby;

    @CommandPermission("lobby.location.set")
    @Syntax("<name>")
    @Subcommand("set")
    public void onLocationSet(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        this.lobby.getLocationAPI().setLocation(player.getLocation(), args[0]);
        this.lobby.getLanguageAPI().sendMessageToPlayer(player, "location.set", Placeholder.parsed("name", args[0]));
    }

    @CommandPermission("lobby.location.teleport")
    @Syntax("<name>")
    @Subcommand("teleport")
    public void onLocationTeleport(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        this.lobby.getLocationAPI().teleportToLocation(player, args[0], true);
    }
}
