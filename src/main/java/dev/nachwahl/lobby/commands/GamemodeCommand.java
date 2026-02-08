package dev.nachwahl.lobby.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.nachwahl.lobby.LobbyPlugin;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("gamemode|gm")
public class GamemodeCommand extends BaseCommand {

    @Dependency
    private LobbyPlugin lobbyPlugin;

    @Default
    @CommandPermission("lobby.manage.edit")
    @Syntax("<gamemode>")
    @CommandCompletion("creative|adventure|spectator")
    public void onGameModeCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0) return;

        if (args[0].startsWith("c")) {
            if (!this.lobbyPlugin.getEditModePlayers().contains(player)) {
                this.lobbyPlugin.getEditModePlayers().add(player);
                this.lobbyPlugin.getLanguageAPI().sendMessageToPlayer(player, "manage.editMode.enable");
                player.getInventory().clear();
            }
            player.setGameMode(GameMode.CREATIVE);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        } else if (args[0].startsWith("s")) {
            if (!this.lobbyPlugin.getEditModePlayers().contains(player)) {
                this.lobbyPlugin.getEditModePlayers().add(player);
                this.lobbyPlugin.getLanguageAPI().sendMessageToPlayer(player, "manage.editMode.enable");
                player.getInventory().clear();
            }
            player.setGameMode(GameMode.SPECTATOR);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        } else {
            if (this.lobbyPlugin.getEditModePlayers().contains(player)) {
                LobbyManageCommand.disableBuildMode(player, this.lobbyPlugin);
            }
        }

    }
}
