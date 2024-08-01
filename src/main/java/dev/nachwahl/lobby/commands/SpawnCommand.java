package dev.nachwahl.lobby.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.utils.HotbarItems;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@CommandAlias("spawn")
public class SpawnCommand extends BaseCommand {
    @Dependency
    private Lobby lobby;


    @Default
    public void onSpawnCommand(CommandSender sender) {
        Player player = (Player) sender;
        Lobby.getInstance().getLocationAPI().teleportToLocation((Player) sender, "spawn", false);
        HotbarItems.setElytra(player, lobby);
    }

}
