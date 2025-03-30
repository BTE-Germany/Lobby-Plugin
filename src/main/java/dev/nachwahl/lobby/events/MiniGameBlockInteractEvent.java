package dev.nachwahl.lobby.events;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.language.Language;
import dev.nachwahl.lobby.utils.MiniGameBlockUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MiniGameBlockInteractEvent implements Listener {

    HashMap<Player, String> playerHashMap = new HashMap<>();

    @EventHandler
    void onInteract(PlayerInteractEvent e) {

        Player p = e.getPlayer();
        Action action = e.getAction();
        Block block = e.getClickedBlock();
        if (block == null) {
            return;
        }
        Location loc = block.getLocation();

        if (e.getHand() == EquipmentSlot.HAND) {
            if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
                String gameName = "";

                String[] games = {"TicTacToe", "Connect4", "UNO", "BattleShip", "RockPaperScissors"};
                for (String s : games) {
                    for (String a : Lobby.getInstance().getMiniGameBlockUtil().getList(s.toLowerCase())) {
                        Location blockLoc = Lobby.getInstance().getLocationAPI().parseLocation(a);
                        if (blockLoc.getBlock().getLocation().equals(loc.getBlock().getLocation())) {
                            gameName = s;
                        }
                    }
                }

                if (gameName.equals("")) {
                    return;
                }
                if (playerHashMap.containsKey(p)) {
                    String existingGame = playerHashMap.get(p);
                    for (String s : Lobby.getInstance().getMiniGameBlockUtil().getList(existingGame.toLowerCase())) {
                        Location blockLoc = Lobby.getInstance().getLocationAPI().parseLocation(s);

                        de.oliver.fancyholograms.api.HologramManager manager = de.oliver.fancyholograms.api.FancyHologramsPlugin.get().getHologramManager();
                        java.util.Optional<de.oliver.fancyholograms.api.hologram.Hologram> hologram =
                            manager.getHologram(gameName.toLowerCase() + "_" + blockLoc.getBlockX() + "-" + blockLoc.getBlockZ());
                        hologram.ifPresent(manager::removeHologram);
                    }
                    playerHashMap.remove(p);
                    MiniGameBlockUtil.setGameTitleHoverTexts(existingGame);
                } else {
                    playerHashMap.put(p, gameName);
                    play(gameName, loc);
                }
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (playerHashMap.containsKey(player)) {
            String existingGame = playerHashMap.get(player);
            for (String s : Lobby.getInstance().getMiniGameBlockUtil().getList(existingGame.toLowerCase())) {
                Location blockLoc = Lobby.getInstance().getLocationAPI().parseLocation(s);

                de.oliver.fancyholograms.api.HologramManager manager = de.oliver.fancyholograms.api.FancyHologramsPlugin.get().getHologramManager();
                java.util.Optional<de.oliver.fancyholograms.api.hologram.Hologram> hologram =
                    manager.getHologram(existingGame.toLowerCase() + "_" + blockLoc.getBlockX() + "-" + blockLoc.getBlockZ());
                hologram.ifPresent(manager::removeHologram);

            }
            playerHashMap.remove(player);
            MiniGameBlockUtil.setGameTitleHoverTexts(existingGame);
        }
    }

    private void play(String game, Location locBlock) {
        ArrayList<Player> players = new ArrayList<>();

        for (Map.Entry<Player, String> entry : playerHashMap.entrySet()) {
            Player key = entry.getKey();
            String value = entry.getValue();
            if (value.equalsIgnoreCase(game)) {
                players.add(key);
            }
        }

        Player p1 = players.get(0);
        Location locHD = new Location(locBlock.getWorld(), locBlock.getBlockX() + 0.5, locBlock.getBlockY() + 3, locBlock.getBlockZ() + 0.5);

        if (players.size() > 1) {
            MiniGameBlockUtil.deleteHologram(game, locHD);
            Player p2 = players.get(1);
            p2.playSound(p2, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 0);
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "mbg force " + game + " " + p1.getName() + " " + p2.getName());
            for (Player p : new ArrayList<>(playerHashMap.keySet())) {
                if (p.getUniqueId().equals(p1.getUniqueId()) || p.getUniqueId().equals(p2.getUniqueId())) {
                    playerHashMap.remove(p);
                }
            }
            MiniGameBlockUtil.setGameTitleHoverText(game, locBlock);
        } else {
            Lobby.getInstance().getLanguageAPI().getMessage(p1, message -> {
                p1.sendMessage(Lobby.getInstance().getMiniMessage().deserialize("<prefix> ").append(message));
            }, "minigame.queue");
            p1.playSound(p1, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 0);
            setQueueHoverTexts(game);
        }
    }

    private void setQueueHoverTexts(String game) {
        for (String s : Lobby.getInstance().getMiniGameBlockUtil().getList(game.toLowerCase())) {
            Location blockLoc = Lobby.getInstance().getLocationAPI().parseLocation(s);
            Location loc = new Location(blockLoc.getWorld(), blockLoc.getBlockX() + 0.5, blockLoc.getBlockY() + 3, blockLoc.getBlockZ() + 0.5);
            Component englishComponent = Lobby.getInstance().getLanguageAPI().getMessage(Language.ENGLISH, "minigame.queue");
            Component germanComponent = Lobby.getInstance().getLanguageAPI().getMessage(Language.GERMAN, "minigame.queue");
            String[] englishString = LegacyComponentSerializer.legacyAmpersand().serialize(englishComponent).replaceAll("&", "§").split(" ");
            String[] germanString = LegacyComponentSerializer.legacyAmpersand().serialize(germanComponent).replaceAll("&", "§").split(" ");

            String[] german = new String[]{germanString[0] + " " + germanString[1], germanString[2] + " " + germanString[3]};
            String[] english = new String[]{englishString[0] + " " + englishString[1], englishString[2] + " " + englishString[3]};

            Lobby.getInstance().getHologramAPI().addHologram(game + "." + s, new dev.nachwahl.lobby.hologram.Hologram(loc, english, german,
                game + " " + loc.getBlockX() + "-" + loc.getBlockZ()));
        }
    }

}
