package dev.nachwahl.lobby.events;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.commands.RegisterMiniGameBlockCommand;
import dev.nachwahl.lobby.utils.MiniGameBlockUtil;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiniGameBlockInteractEvent implements Listener {

    HashMap<Player,String> playerHashMap = new HashMap<>();

    @EventHandler
    void onInteract(PlayerInteractEvent e){

        Player p = e.getPlayer();
        Action action = e.getAction();
        Block block = e.getClickedBlock();
        Location loc = block.getLocation();

        if (e.getHand() == EquipmentSlot.HAND) {
            if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
                String gameName = "";

                String[] games = {"TicTacToe","Connect4","UNO","BattleShip","RockPaperScissors"};
                for(String s : games) {
                    for(String a : Lobby.getInstance().getMiniGameBlockUtil().getList(s.toLowerCase())) {
                        Location blockLoc = Lobby.getInstance().getLocationAPI().parseLocation(a);
                        if(blockLoc.getBlock().getLocation().equals(loc.getBlock().getLocation())) {
                            gameName = s;
                        }
                    }
                }

                if(gameName.equals("")) {
                    return;
                }
                if(playerHashMap.containsKey(p)) {
                    String existingGame = playerHashMap.get(p);
                    for (String s : Lobby.getInstance().getMiniGameBlockUtil().getList(existingGame.toLowerCase())) {
                        Location blockLoc = Lobby.getInstance().getLocationAPI().parseLocation(s);

                        for (Hologram h : Lobby.getInstance().getHologramAPI().getApi().getHolograms()) {
                            Location hloc = h.getPosition().toLocation();
                            if (blockLoc.getBlock().getLocation().equals(hloc.getBlock().getLocation())) {
                                h.delete();
                            }
                        }

                    }
                    MiniGameBlockUtil.setHoverText(existingGame);
                }
                playerHashMap.put(p,gameName);
                play(gameName,block.getLocation());
                e.setCancelled(true);
            }
        }
    }

    private void play(String game, Location locBlock){
        ArrayList<Player> players = new ArrayList<>();

        for (Map.Entry<Player, String> entry : playerHashMap.entrySet()) {
            Player key = entry.getKey();
            String value = entry.getValue();
            if(value.equalsIgnoreCase(game)){
                players.add(key);
            }
        }

        Player p1 = players.get(0);
        Location locHD = new Location(locBlock.getWorld(), locBlock.getBlockX()+0.5, locBlock.getBlockY()+3,locBlock.getBlockZ()+0.5);

        if(players.size() > 1) {
            MiniGameBlockUtil.deleteHologram(locHD);
            MiniGameBlockUtil.setHoverText(game);
            Player p2 = players.get(1);
            p2.playSound(p2, Sound.ENTITY_EXPERIENCE_ORB_PICKUP,100,0);
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "mbg force " + game+" "+ p1.getName() + " " + p2.getName());
            for (Player p : new ArrayList<>(playerHashMap.keySet())) {
                if(p.getUniqueId().equals(p1.getUniqueId())||p.getUniqueId().equals(p2.getUniqueId())){
                    playerHashMap.remove(p);
                }
            }
        }else{

            p1.sendMessage("§b§lBTEG §7» Warte auf 2. Mitspieler bei "+game+"...");
            p1.playSound(p1, Sound.ENTITY_EXPERIENCE_ORB_PICKUP,100,0);
            Hologram hologram = Lobby.getInstance().getHologramAPI().getApi().createHologram(locHD);
            hologram.getLines().appendText("§aWarte auf");
            hologram.getLines().appendText("§a2. Mitspieler bei");
            hologram.getLines().appendText("§2§l"+game);
            hologram.getLines().appendText("§a...");

        }
    }


}
