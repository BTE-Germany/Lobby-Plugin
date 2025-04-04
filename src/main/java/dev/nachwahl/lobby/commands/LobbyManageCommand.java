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
import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import org.jetbrains.annotations.NotNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.sql.SQLException;

@CommandAlias("lobbymanage|managelobby|ml|lm|lobby")
public class LobbyManageCommand extends BaseCommand {
    @Dependency
    private Lobby lobby;

    @CommandPermission("lobby.manage.reload")
    @Subcommand("reload")
    public void onReloadCommand(@NotNull CommandSender sender) {
        var mm = MiniMessage.miniMessage();
        lobby.reloadConfig();
        sender.sendMessage(mm.deserialize("<green>Erfolgreich Konfiguration neugeladen.</green>"));
    }

    @CommandPermission("lobby.manage.edit")
    @Subcommand("edit")
    public void onEditCommand(@NotNull CommandSender sender) {
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
    public void onHologramReloadCommand(@NotNull CommandSender sender) {
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
    public void onLeaderboardReloadCommand(@NotNull CommandSender sender) {
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
    public void onHologramList(@NotNull CommandSender sender) {
        FancyHologramsPlugin.get().getHologramManager().getHolograms().forEach(hologram -> {
            sender.sendMessage(hologram.getName());
            hologram.forceUpdate();
            hologram.forceUpdateShownStateFor((Player) sender);
        });
    }

    @CommandPermission("lobby.manage.hologram")
    @Subcommand("hologram delete")
    public void onHologramDelete(@NotNull CommandSender sender) {
        var manager = FancyHologramsPlugin.get().getHologramManager();
        manager.getHolograms().forEach(manager::removeHologram);
        sender.sendMessage(Component.text("Deleted all holograms.", NamedTextColor.DARK_RED));
    }

    @CommandPermission("lobby.manage.hologram")
    @Subcommand("hologram debug")
    public void onHologramDebug(@NotNull CommandSender sender) {
        if (!(sender instanceof Player p)) return;
        var manager = FancyHologramsPlugin.get().getHologramManager();
        de.oliver.fancyholograms.api.data.TextHologramData data =
            new de.oliver.fancyholograms.api.data.TextHologramData("debug", p.getLocation().add(2, 1, 2));
        data.addLine("<blue>Super Cool Custom Hologram! Which seems to work?</blue>");
        data.setPersistent(false);
        manager.addHologram(manager.create(data));

        Lobby.getInstance().getHologramAPI().addHologram("debugAPI", new dev.nachwahl.lobby.hologram.Hologram(p.getLocation().add(-2, 1, -2),
            java.util.Collections.singletonList("<orange>Cool Orange Test text</orange>"),
            java.util.Collections.singletonList("<red>Cool Red Test text</red>"), "debugAPI"));
    }
}
