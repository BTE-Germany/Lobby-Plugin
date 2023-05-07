package dev.nachwahl.lobby.commands;


import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import dev.nachwahl.lobby.Lobby;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("lobbymanage|managelobby|ml|lm")
public class LobbyManageCommand extends BaseCommand {
    @Dependency
    private Lobby lobby;

    @CommandPermission("lobby.manage.reload")
    @Subcommand("reload")
    public void onReloadCommand(CommandSender sender) {
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
            if(player.hasPermission("lobby.fly")) {
                player.setAllowFlight(true);
            }
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        } else {
            this.lobby.getEditModePlayers().add(player);
            this.lobby.getLanguageAPI().sendMessageToPlayer(player, "manage.editMode.enable");
            player.getInventory().clear();
            player.setGameMode(GameMode.CREATIVE);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

        }
    }
}
