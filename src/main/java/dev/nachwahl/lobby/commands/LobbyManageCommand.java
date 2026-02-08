package dev.nachwahl.lobby.commands;


import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import dev.nachwahl.lobby.LobbyPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

@CommandAlias("lobbymanage|managelobby|ml|lm|lobby")
public class LobbyManageCommand extends BaseCommand {
    @Dependency
    private LobbyPlugin lobbyPlugin;

    @CommandPermission("lobby.manage.reload")
    @Subcommand("reload")
    public void onReloadCommand(@NotNull CommandSender sender) {
        var mm = MiniMessage.miniMessage();
        lobbyPlugin.reloadConfig();
        sender.sendMessage(mm.deserialize("<green>Erfolgreich Konfiguration neugeladen.</green>"));
    }

    @CommandPermission("lobby.manage.edit")
    @Subcommand("edit")
    public void onEditCommand(@NotNull CommandSender sender) {
        Player player = (Player) sender;
        if (this.lobbyPlugin.getEditModePlayers().contains(player)) {
            disableBuildMode(player, this.lobbyPlugin);
        } else {
            this.lobbyPlugin.getEditModePlayers().add(player);
            this.lobbyPlugin.getLanguageAPI().sendMessageToPlayer(player, "manage.editMode.enable");
            player.getInventory().clear();
            player.setGameMode(GameMode.CREATIVE);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

        }
    }

    static void disableBuildMode(Player player, @NotNull LobbyPlugin lobbyPlugin) {
        lobbyPlugin.getEditModePlayers().remove(player);
        lobbyPlugin.getLanguageAPI().sendMessageToPlayer(player, "manage.editMode.disable");
        lobbyPlugin.getHotbarItems().setHotbarItems(player);
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
    }


    @CommandPermission("lobby.manage.hologram")
    @Subcommand("hologram reload")
    public void onHologramReloadCommand(@NotNull CommandSender sender) {
        var mm = MiniMessage.miniMessage();
        sender.sendMessage(mm.deserialize("<gold>Hologramme werden neugeladen...</gold>"));
        this.lobbyPlugin.getHologramAPI().loadData();
        for (Player player : Bukkit.getOnlinePlayers()) {
            LobbyPlugin.getInstance().getLanguageAPI().getLanguage(player);
        }
        sender.sendMessage(mm.deserialize("<green>Erfolgreich Hologramme neugeladen.</green>"));
    }

    @CommandPermission("lobby.manage.leaderboard")
    @Subcommand("leaderboard reload")
    public void onLeaderboardReloadCommand(@NotNull CommandSender sender) {
        var mm = MiniMessage.miniMessage();
        sender.sendMessage(mm.deserialize("<gold>Leaderboards werden neugeladen...</gold>"));
        try {
            this.lobbyPlugin.getLeaderboardManager().load();
        } catch (SQLException ignored) { // Ignored
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            LobbyPlugin.getInstance().getLanguageAPI().getLanguage(player);
        }
        sender.sendMessage(mm.deserialize("<green>Erfolgreich Leaderboards neugeladen.</green>"));
    }

    @CommandPermission("lobby.manage.hologram")
    @Subcommand("hologram debug")
    public void onHologramDebugCommand(@NotNull CommandSender sender) {
        if (sender instanceof Player p) { // TODO Remove when Lobby Holograms works fine again
            LobbyPlugin inst = LobbyPlugin.getInstance();
            if (inst.getHologramAPI().debugPlayer != p.getUniqueId()) {
                p.sendMessage(Component.text("Enabled/Switched Lobby Hologram Debug mode"));
                inst.getHologramAPI().debugPlayer = p.getUniqueId();
            } else {
                p.sendMessage(Component.text("Disabled Lobby Hologram Debug mode"));
                inst.getHologramAPI().debugPlayer = null;
            }
            inst.getLanguageAPI().setLanguage(inst.getLanguageAPI().getOppositeLanguage(inst.getLanguageAPI().getLanguage(p)), p);
        }
    }
}
