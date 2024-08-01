package dev.nachwahl.lobby.quests.listener;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.quests.Quest;
import dev.nachwahl.lobby.quests.car.CarQuest;
import dev.nachwahl.lobby.quests.mine.MineQuest;
import dev.nachwahl.lobby.quests.mine.VeinType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashMap;

public class BlockBreakListener implements Listener {
    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Quest quest;
        if ((quest = Lobby.getInstance().getQuestManager().getQuestFromPlayer(player)) != null) {
            if (quest instanceof MineQuest) {
                MineQuest mineQuest = (MineQuest) quest;
                Block block = event.getBlock();
                HashMap<String, Integer> map = ((MineQuest) quest).getBlockCount();
                if (block.getType().equals(Material.COAL_ORE)) {
                    event.setExpToDrop(0);
                    map.put(VeinType.COAL.toString(), map.get(VeinType.COAL.toString()));
                    //add Coins Method
                    mineQuest.getBlockCount().put(VeinType.COAL.toString(), mineQuest.getBlockCount().get(VeinType.COAL.toString()) + (VeinType.COAL.getValue()));
                    player.sendMessage(MineQuest.prefix + "Du hast " + VeinType.COAL.getValue() + " Coin(s) bekommen!");
                    event.setDropItems(false);
                } else if (block.getType().equals(Material.IRON_ORE)) {
                    map.put(VeinType.IRON.toString(), map.get(VeinType.IRON.toString()));
                    //add Coins Method
                    mineQuest.getBlockCount().put(VeinType.IRON.toString(), mineQuest.getBlockCount().get(VeinType.IRON.toString()) + VeinType.IRON.getValue());
                    player.sendMessage(MineQuest.prefix + "Du hast " + VeinType.IRON.getValue() + " Coin(s) bekommen!");
                    event.setDropItems(false);
                } else if (block.getType().equals(Material.GOLD_ORE)) {
                    map.put(VeinType.GOLD.toString(), map.get(VeinType.GOLD.toString()));
                    //add Coins Method
                    mineQuest.getBlockCount().put(VeinType.GOLD.toString(), mineQuest.getBlockCount().get(VeinType.GOLD.toString()) + VeinType.GOLD.getValue());
                    player.sendMessage(MineQuest.prefix + "Du hast " + VeinType.GOLD.getValue() + " Coin(s) bekommen!");
                    event.setDropItems(false);
                } else {
                    event.setCancelled(true);
                }
            } else if (quest instanceof CarQuest) {
                CarQuest carQuest = (CarQuest) quest;
                event.setCancelled((!carQuest.getPlacedBlocks().contains(event.getBlock())));
            }
        }
    }
}
