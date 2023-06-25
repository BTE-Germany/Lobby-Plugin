package dev.nachwahl.lobby.events;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.commands.RegisterMiniGameBlockCommand;
import dev.nachwahl.lobby.utils.MiniGameBlockUtil;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
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
        if(block == null) {
            return;
        }
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
                            if (blockLoc.getBlock().getLocation().getBlockX() == hloc.getBlock().getLocation().getBlockX() && blockLoc.getBlock().getLocation().getBlockZ() == hloc.getBlock().getLocation().getBlockZ()) {
                                h.delete();
                            }
                        }

                    }
                    playerHashMap.remove(p);
                    MiniGameBlockUtil.setGameTitleHoverTexts(existingGame);
                }else {
                    playerHashMap.put(p, gameName);
                    play(gameName, loc);
                }
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
            Player p2 = players.get(1);
            p2.playSound(p2, Sound.ENTITY_EXPERIENCE_ORB_PICKUP,100,0);
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "mbg force " + game+" "+ p1.getName() + " " + p2.getName());
            for (Player p : new ArrayList<>(playerHashMap.keySet())) {
                if(p.getUniqueId().equals(p1.getUniqueId())||p.getUniqueId().equals(p2.getUniqueId())){
                    playerHashMap.remove(p);
                }
            }
            MiniGameBlockUtil.setGameTitleHoverText(game, locBlock);
        }else{
            Lobby.getInstance().getLanguageAPI().getLanguage(p1, language -> {
                Lobby.getInstance().getLanguageAPI().sendMessageToPlayer(p1, "minigame.queue", Placeholder.parsed("minigame", game));
            });
            p1.playSound(p1, Sound.ENTITY_EXPERIENCE_ORB_PICKUP,100,0);
            setQueueHoverTexts(game);

        }
    }

    private void setQueueHoverTexts(String game){

        for (String s : Lobby.getInstance().getMiniGameBlockUtil().getList(game.toLowerCase())) {
            Location blockLoc = Lobby.getInstance().getLocationAPI().parseLocation(s);
            Location loc = new Location(blockLoc.getWorld(), blockLoc.getBlockX()+0.5, blockLoc.getBlockY()+3,blockLoc.getBlockZ()+0.5);
            Hologram hologram = Lobby.getInstance().getHologramAPI().getApi().createHologram(loc);
            for(Player player : Bukkit.getOnlinePlayers()) {
                Lobby.getInstance().getLanguageAPI().getLanguage(player, language -> {
                    String[] text = Lobby.getInstance().getLanguageAPI().getMessageString(language,"minigame.queue").replaceAll("<[^>]*>", "").split(" ");
                    // TODO: ohne language api funktionierten die queue Hover texts. mit noch nicht
                    hologram.getLines().appendText("§a" + text[0].replace(" ","") + " " + text[1]);
                    hologram.getLines().appendText("§a" + text[2] + " " + text[3] + " " + text[4]);
                    hologram.getLines().appendText("§2§l"+game);
                    hologram.getLines().appendText("§a...");
                });
            }
        }
    }

}
