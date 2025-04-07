package dev.nachwahl.lobby.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.TextHologramData;
import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.utils.MiniGameBlockUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@CommandAlias("registerminigameblock|rmgb")
public class RegisterMiniGameBlockCommand extends BaseCommand {

    @Dependency
    private Lobby lobby;

    @CommandPermission("lobby.manage.minigames")
    @Syntax("<game>")
    @Subcommand("add")
    public void onAdd(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return;
        Location location = player.getTargetBlockExact(3).getLocation();
        Location locHD = new Location(player.getWorld(), location.getBlockX() + 0.5, location.getBlockY() + 3.5, location.getBlockZ() + 0.5);

        List<String> list = lobby.getMiniGameBlockUtil().getList(args[0].toLowerCase());
        list.add(lobby.getLocationAPI().parseLocation(location));
        lobby.getMiniGameBlockUtil().getDataFile().set(args[0].toLowerCase(), list);
        try {
            lobby.getMiniGameBlockUtil().saveFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HologramManager manager = FancyHologramsPlugin.get().getHologramManager();
        TextHologramData data = new TextHologramData(args[0] + "_" + locHD.getBlockX() + "-" + locHD.getBlockZ(), locHD);
        data.setText(Collections.singletonList(dev.nachwahl.lobby.utils.MiniGameBlockUtil.FORMATTING_CODE + args[0]));
        data.setPersistent(false);
        manager.addHologram(manager.create(data));
        player.sendMessage("§aDu hast einen Minigameblock für das Spiel §9" + args[0] + " §ahinzugefügt!");
    }

    @CommandPermission("lobby.manage.minigames")
    @Syntax("<game>")
    @Subcommand("remove")
    public void onRemove(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return;
        Location location = player.getTargetBlockExact(3).getLocation();
        Location locHD = new Location(player.getWorld(), location.getBlockX() + 0.5, location.getBlockY() + 3.5, location.getBlockZ() + 0.5);

        for (String s : lobby.getMiniGameBlockUtil().getList(args[0].toLowerCase())) {
            if (lobby.getLocationAPI().parseLocation(s).getBlock().getLocation().equals(location.getBlock().getLocation())) {
                List<String> list1 = lobby.getMiniGameBlockUtil().getList(args[0].toLowerCase());
                list1.remove(s);
                lobby.getMiniGameBlockUtil().getDataFile().set(args[0].toLowerCase(), list1);
                try {
                    lobby.getMiniGameBlockUtil().saveFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                MiniGameBlockUtil.deleteHologram(args[0].toLowerCase(), locHD);
            }
        }

        player.sendMessage("§aDu hast den Minigameblock für das Spiel §6" + args[0] + " §agelöscht!");


    }
}
