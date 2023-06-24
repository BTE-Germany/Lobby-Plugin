
package dev.nachwahl.lobby.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.quests.Quest;
import dev.nachwahl.lobby.quests.QuestType;
import dev.nachwahl.lobby.quests.car.CarQuest;
import dev.nachwahl.lobby.quests.mine.MineQuest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("quest")
public class QuestsCommand extends BaseCommand {

    @Dependency
    private Lobby lobby;
    @CommandPermission("lobby.vanish")
    @Default
    public boolean onCommand(CommandSender sender, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(Lobby.getInstance().getQuestManager().getQuestFromPlayer(player) != null){
                player.sendMessage("Du bist bereits in einer Quest!");
                return false;
            }else{
                Quest quest;
                if(args[0].equals("mine")){
                    quest = Lobby.getInstance().getPoolManager().getFreeQuest(QuestType.MINE);
                    if(quest != null){
                        if(quest instanceof MineQuest){
                            MineQuest mineQuest = (MineQuest) quest;
                            mineQuest.startQuest(player);
                        }
                    }else{
                        player.sendMessage(MineQuest.prefix + "Konnte kein freies Spiel finden, Du wurdest der Warteschlange hinzugefügt.");
                    }
                }else if(args[0].equals("car")){
                    quest = Lobby.getInstance().getPoolManager().getFreeQuest(QuestType.CAR);
                    if(quest != null){
                        if(quest instanceof CarQuest){
                            CarQuest carQuest = (CarQuest) quest;
                            carQuest.startQuest(player);
                        }
                    }else{
                        player.sendMessage(MineQuest.prefix + "Konnte kein freies Spiel finden, Du wurdest der Warteschlange hinzugefügt.");
                    }
                }
            }

            return false;
        }else{
            sender.sendMessage("Du musst ein Spieler sein, um diesen Befehl ausführen zu können.");
        }
        return false;
    }
}
