package dev.nachwahl.lobby.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.utils.MiniGameBlockUtil;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

@CommandAlias("registerminigameblock|rmgb")
public class RegisterMiniGameBlockCommand extends BaseCommand {

    @Dependency
    private Lobby lobby;

    @CommandPermission("lobby.manage.minigames")
    @Syntax("<game>")
    @Subcommand("add")
    public void onAdd(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;
        Location location = player.getTargetBlockExact(3).getLocation();
        Location locHD = new Location(player.getWorld(), location.getBlockX()+0.5, location.getBlockY()+3.5,location.getBlockZ()+0.5);

        List<String> list = lobby.getMiniGameBlockUtil().getList(args[0].toLowerCase());
        list.add(lobby.getLocationAPI().parseLocation(location));
        lobby.getMiniGameBlockUtil().getDataFile().set(args[0].toLowerCase(),list);
        try {
            lobby.getMiniGameBlockUtil().saveFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Hologram hologram = lobby.getHologramAPI().getApi().createHologram(locHD);
        hologram.getLines().appendText("§6§l"+args[0]);
        player.sendMessage("§aDu hast einen Minigameblock für das Spiel §6"+args[0]+" §ahinzugefügt!");
    }

    @CommandPermission("lobby.manage.minigames")
    @Syntax("<game>")
    @Subcommand("remove")
    public void onRemove(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;
        Location location = player.getTargetBlockExact(3).getLocation();
        Location locHD = new Location(player.getWorld(), location.getBlockX()+0.5, location.getBlockY()+3.5,location.getBlockZ()+0.5);

        for(String s : lobby.getMiniGameBlockUtil().getList(args[0].toLowerCase())) {
            if(lobby.getLocationAPI().parseLocation(s).getBlock().getLocation().equals(location.getBlock().getLocation())){
                List<String> list1 = lobby.getMiniGameBlockUtil().getList(args[0].toLowerCase());
                list1.remove(s);
               lobby.getMiniGameBlockUtil().getDataFile().set(args[0].toLowerCase(),list1);
                try {
                    lobby.getMiniGameBlockUtil().saveFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                MiniGameBlockUtil.deleteHologram(locHD);
            }
        }

        player.sendMessage("§aDu hast den Minigameblock für das Spiel §6"+args[0]+" §agelöscht!");


    }
}
