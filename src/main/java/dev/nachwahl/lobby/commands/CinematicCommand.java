package dev.nachwahl.lobby.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.nachwahl.lobby.LobbyPlugin;
import dev.nachwahl.lobby.cinematic.PathPoint;
import dev.nachwahl.lobby.cinematic.PlayerCinematic;
import dev.nachwahl.lobby.utils.CinematicUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@CommandAlias("cinematic")
public class CinematicCommand extends BaseCommand {

    @Dependency
    private LobbyPlugin lobbyPlugin;

    private CinematicUtil cinematicUtil;
    private String commandCompletionStart;

    public CinematicCommand(CinematicUtil cinematicUtil){
        this.cinematicUtil = cinematicUtil;
        StringBuilder commandCompletionStart = new StringBuilder();
        cinematicUtil.getCinematicList().forEach(s -> commandCompletionStart.append(s).append("|"));
        this.commandCompletionStart = commandCompletionStart.toString();
    }

    @CommandPermission("lobby.cinematic.start")
    @Subcommand("start")
    public void onStartCommand(CommandSender sender, String[] args) {
        var mm = MiniMessage.miniMessage();
        sender.sendMessage(mm.deserialize("<green>Erfolgreich Cinematic gestartet.</green>"));
        PlayerCinematic cinematic = new PlayerCinematic(lobbyPlugin);
        List<PathPoint> points = cinematicUtil.getCinematicPath(args[0]);
        if(args.length>1){
            if(args.length>2){
                if(args[2].equalsIgnoreCase("spline")){
                    cinematic.startCinematic((Player) sender,points, 0,false);
                }else if(args[2].equalsIgnoreCase("linear")) {
                    cinematic.startCinematic((Player) sender, points, 0,true);
                }
            }else{
                cinematic.startCinematic((Player) sender,points, PlayerCinematic.convertToTicks(args[1]),true);
            }
        }else{
            cinematic.startCinematic((Player) sender,points,0,true);
        }
    }

    @CommandPermission("lobby.cinematic.add")
    @Subcommand("add")
    public void onAddCommand(CommandSender sender, String[] args) {
        var mm = MiniMessage.miniMessage();

        if(args.length==1){
            cinematicUtil.addCinematicPathPoint(args[0], new PathPoint(((Player) sender).getLocation(), 20));
        }
        else if(args.length==2){
            cinematicUtil.addCinematicPathPoint(args[0], new PathPoint(((Player) sender).getLocation(), PlayerCinematic.convertToTicks(args[1])));
        }

        sender.sendMessage(mm.deserialize("<green>Erfolgreich Punkt zu <dark_green>" + args[0] + "</dark_green> hinzugefügt.</green>"));
    }

    @CommandPermission("lobby.cinematic.list")
    @Subcommand("list")
    public void onShowCommand(CommandSender sender, String[] args) {
        var mm = MiniMessage.miniMessage();

        if(args.length==0){
            sender.sendMessage(mm.deserialize("<green>Kamerafahrten:</green>"));
            cinematicUtil.getCinematicList().forEach((cinematic) -> {
                sender.sendMessage(mm.deserialize("<dark_green>" + cinematic + "</dark_green>"));
            });
        }
        else if(args.length>0){
            sender.sendMessage(mm.deserialize("<green>Kamerafahrtpunkte für <dark_green>" + args[0] +"</dark_green>:</green>"));
            AtomicInteger i = new AtomicInteger(1);
            cinematicUtil.getCinematicPath(args[0]).forEach((point) -> {
                Location location = point.getLocation();
                sender.sendMessage(mm.deserialize("<dark_gray>"+ i.getAndIncrement() + ".</dark_gray> <gray>" + location.getWorld().getName() + ", " + location.getX() + ", " + location.getY() + ", " + location.getZ() + ", speed: " + point.getTicksToPoint() + "</gray>"));
            });
        }
    }
    @CommandPermission("lobby.cinematic.delete")
    @Subcommand("delete")
    public void onDeleteCommand(CommandSender sender, String[] args) {
        var mm = MiniMessage.miniMessage();

        if(args.length==1){
            cinematicUtil.deleteCinematic(args[0]);
            sender.sendMessage(mm.deserialize("<green>Erfolgreich Kamerafahrt <dark_green>"+args[0]+"</dark_green> gelöscht.</green>"));
        }else if(args.length==2){
            cinematicUtil.deleteCinematicPathPoint(args[0],Integer.parseInt(args[1]));
            sender.sendMessage(mm.deserialize("<green>Erfolgreich Punkt <dark_gray>" + args[1] + "</dark_gray> bei Kamerafahrt <dark_green>"+args[0]+"</dark_green> gelöscht.</green>"));
        }


    }


}
