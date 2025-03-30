package dev.nachwahl.lobby.commands;


import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import dev.nachwahl.lobby.Lobby;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

@CommandAlias("lobbymanage|managelobby|ml|lm|lobby")
public class LobbyManageCommand extends BaseCommand {
    @Dependency
    private Lobby lobby;

    @CommandPermission("lobby.manage.reload")
    @Subcommand("reload")
    public void onReloadCommand(@org.jetbrains.annotations.NotNull CommandSender sender) {
        var mm = MiniMessage.miniMessage();
        lobby.reloadConfig();
        sender.sendMessage(mm.deserialize("<green>Erfolgreich Konfiguration neugeladen.</green>"));
    }

    @CommandPermission("lobby.manage.edit")
    @Subcommand("edit")
    public void onEditCommand(CommandSender sender) {
        Player player = (Player) sender;
        if (this.lobby.getEditModePlayers().contains(player)) {
            this.lobby.getEditModePlayers().remove(player);
            this.lobby.getLanguageAPI().sendMessageToPlayer(player, "manage.editMode.disable");
            this.lobby.getHotbarItems().setHotbarItems(player);
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        } else {
            this.lobby.getEditModePlayers().add(player);
            this.lobby.getLanguageAPI().sendMessageToPlayer(player, "manage.editMode.enable");
            player.getInventory().clear();
            player.setGameMode(GameMode.CREATIVE);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

        }
    }


    @CommandPermission("lobby.manage.hologram")
    @Subcommand("hologram reload")
    public void onHologramReloadCommand(@org.jetbrains.annotations.NotNull CommandSender sender) {
        var mm = MiniMessage.miniMessage();
        sender.sendMessage(mm.deserialize("<gold>Hologramme werden neugeladen...</gold>"));
        this.lobby.getHologramAPI().loadData();
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.lobby.getHologramAPI().showHolograms(player);
        }
        sender.sendMessage(mm.deserialize("<green>Erfolgreich Hologramme neugeladen.</green>"));
    }

    @CommandPermission("lobby.manage.leaderboard")
    @Subcommand("leaderboard reload")
    public void onLeaderboardReloadCommand(@org.jetbrains.annotations.NotNull CommandSender sender) {
        var mm = MiniMessage.miniMessage();
        sender.sendMessage(mm.deserialize("<gold>Leaderboards werden neugeladen...</gold>"));
        try {
            this.lobby.getLeaderboardManager().load();
        } catch (SQLException ignored) { // Ignored
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.lobby.getHologramAPI().showHolograms(player);
        }
        sender.sendMessage(mm.deserialize("<green>Erfolgreich Leaderboards neugeladen.</green>"));
    }

    @CommandPermission("lobby.manage.hologram")
    @Subcommand("hologram list")
    public void onListFancyholograms(@org.jetbrains.annotations.NotNull CommandSender sender) {
        de.oliver.fancyholograms.api.FancyHologramsPlugin.get().getHologramManager().getHolograms().forEach(hologram -> sender.sendMessage(hologram.getName()));
    }
}
