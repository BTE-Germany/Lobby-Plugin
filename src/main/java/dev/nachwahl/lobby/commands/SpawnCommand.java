package dev.nachwahl.lobby.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import dev.nachwahl.lobby.Lobby;
import org.bukkit.command.CommandSender;


public class SpawnCommand extends BaseCommand {
    @Dependency
    private Lobby plugin;


    @Default
    @Subcommand("set")
    public void onSpawnCommand(CommandSender sender) {

    }

}
