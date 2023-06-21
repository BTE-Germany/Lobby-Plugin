package dev.nachwahl.lobby.quests.listener;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.quests.Quest;
import dev.nachwahl.lobby.quests.car.CarQuest;
import dev.nachwahl.lobby.quests.mine.MineQuest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.checkerframework.checker.units.qual.C;

public class BlockPlaceEvent implements Listener {
    @EventHandler
    public void placeBlockEvent(org.bukkit.event.block.BlockPlaceEvent event){
        Player player = event.getPlayer();
        Quest quest;
        if((quest = Lobby.getInstance().getQuestManager().getQuestFromPlayer(player))!= null){
            if(quest instanceof MineQuest){
                event.setCancelled(true);
            }else if(quest instanceof CarQuest){
                CarQuest carQuest = (CarQuest) quest;
                if(carQuest.getCarArena().isBlockInRadius(event.getBlock())){
                    carQuest.addBlock(event.getBlock());
                    carQuest.getCarArena().addAllPlacedBlock(event.getBlock());
                    event.setCancelled(false);
                }else{
                    player.sendMessage(carQuest.prefix+"Du darfst nur im grünen Bereich bauen!");
                    event.setCancelled(true);
                }
            }
        }
    }
}
