package dev.nachwahl.lobby.quests.listener;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.quests.Quest;
import dev.nachwahl.lobby.quests.car.CarQuest;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

public class InteractListener implements Listener {

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Quest quest;
        if ((quest = Lobby.getInstance().getQuestManager().getQuestFromPlayer(player)) != null) {
            if (quest instanceof CarQuest) {
                CarQuest carQuest = (CarQuest) quest;
                if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    if (carQuest.getCarArena().getButtonBlock1().equalsBukkitBlockLocation(Objects.requireNonNull(event.getClickedBlock()))) {
                        System.out.println("Button 1");
                        carQuest.nextStage(0);
                    } else if (carQuest.getCarArena().getButtonBlock2().equalsBukkitBlockLocation(Objects.requireNonNull(event.getClickedBlock()))) {
                        System.out.println("Button 2");
                        carQuest.nextStage(1);
                    } else if (carQuest.getCarArena().getButtonBlock3().equalsBukkitBlockLocation(Objects.requireNonNull(event.getClickedBlock()))) {
                        System.out.println("Button 3");
                        carQuest.nextStage(2);
                    } else if (carQuest.getCarArena().getButtonBlock4().equalsBukkitBlockLocation(Objects.requireNonNull(event.getClickedBlock()))) {
                        System.out.println("Button 4");
                        carQuest.nextStage(3);
                    } else {
                        Block block = event.getClickedBlock();
                        if (block.getType().equals(Material.ANVIL) || block.getType().equals(Material.SMITHING_TABLE) || block.getType().toString().contains("DOOR") || block.getType().toString().contains("GATE")
                                || block.getType().toString().contains("BUTTON") || block.getType().toString().contains("LEVER")) {
                            event.setCancelled(!carQuest.getPlacedBlocks().contains(event.getClickedBlock()));
                        } else {
                            if (event.getClickedBlock().getType().equals(Material.POTTED_CORNFLOWER)) {
                                event.setCancelled(true);
                                player.sendMessage(carQuest.prefix + "Herzlichen Glückwunsch, Du hast ein Easter Egg gefunden. Du erhältst 10 Coins dafür :)");
                            } else {
                                event.setCancelled(false);
                            }
                        }
                    }
                } else {
                    event.setCancelled(false);
                }

            }
        }
    }
}
