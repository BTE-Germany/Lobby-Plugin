package dev.nachwahl.lobby.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import dev.nachwahl.lobby.LobbyPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


@CommandAlias("spawn")
public class SpawnCommand extends BaseCommand {
    @Dependency
    private LobbyPlugin lobbyPlugin;


    @Default
    public void onSpawnCommand(CommandSender sender) {
        Player player = (Player) sender;
        LobbyPlugin.getInstance().getLocationAPI().teleportToLocation(player, "spawn", false);
        if (this.lobbyPlugin.getElytraPlayers().containsKey(player.getUniqueId())) {
            ItemStack item = this.lobbyPlugin.getElytraPlayers().remove(player.getUniqueId());
            player.getInventory().setChestplate(item);
        }
    }

}
