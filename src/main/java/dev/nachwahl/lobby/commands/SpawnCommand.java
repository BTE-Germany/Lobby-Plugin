package dev.nachwahl.lobby.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.utils.HotbarItems;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;


@CommandAlias("spawn")
public class SpawnCommand extends BaseCommand {
    @Dependency
    private Lobby lobby;


    @Default
    public void onSpawnCommand(CommandSender sender) {
        Player player = (Player) sender;
        Lobby.getInstance().getLocationAPI().teleportToLocation((Player) sender,"spawn",false);
        HotbarItems.setElytra(player,lobby);
    }

}
