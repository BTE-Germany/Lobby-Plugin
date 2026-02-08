package dev.nachwahl.lobby.events;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.hologram.Hologram;
import dev.nachwahl.lobby.LobbyPlugin;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MiniGameBlockInteractEvent implements Listener {

    HashMap<Player, String> playerHashMap = new HashMap<>();

    @EventHandler
    void onInteract(@NotNull PlayerInteractEvent e) {

        Player p = e.getPlayer();
        Action action = e.getAction();
        Block block = e.getClickedBlock();
        if (block == null) {
            return;
        }
        Location loc = block.getLocation();

        if (e.getHand() == EquipmentSlot.HAND && action.equals(Action.RIGHT_CLICK_BLOCK)) {
            String gameName = "";

            String[] games = {"TicTacToe", "Connect4", "UNO", "BattleShip", "RockPaperScissors"};
            for (String s : games) {
                for (String a : LobbyPlugin.getInstance().getMiniGameBlockUtil().getList(s.toLowerCase())) {
                    Location blockLoc = LobbyPlugin.getInstance().getLocationAPI().parseLocation(a);
                    if (blockLoc.getBlock().getLocation().equals(loc.getBlock().getLocation())) {
                        gameName = s;
                    }
                }
            }

            if (gameName.isEmpty()) {
                return;
            }
            if (playerHashMap.containsKey(p)) {
                String existingGame = playerHashMap.get(p);
                for (String s : LobbyPlugin.getInstance().getMiniGameBlockUtil().getList(existingGame.toLowerCase())) {
                    Location blockLoc = LobbyPlugin.getInstance().getLocationAPI().parseLocation(s);
                    HologramManager manager = FancyHologramsPlugin.get().getHologramManager();
                    java.util.Optional<Hologram> hologram = manager.getHologram(gameName.toLowerCase() + "_" + blockLoc.getBlockX() + "-" + blockLoc.getBlockZ());
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

    @EventHandler
    public void onLeave(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (playerHashMap.containsKey(player)) {
            String existingGame = playerHashMap.get(player);
            for (String s : LobbyPlugin.getInstance().getMiniGameBlockUtil().getList(existingGame.toLowerCase())) {
                Location blockLoc = LobbyPlugin.getInstance().getLocationAPI().parseLocation(s);
                HologramManager manager = FancyHologramsPlugin.get().getHologramManager();
                Optional<Hologram> hologram = manager.getHologram(existingGame.toLowerCase() + "_" + blockLoc.getBlockX() + "-" + blockLoc.getBlockZ());
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
            LobbyPlugin.getInstance().getLanguageAPI().getMessage(p1, message -> p1.sendMessage(LobbyPlugin.getInstance().getMiniMessage().deserialize("<prefix> ").append(message)), "minigame.queue");
            p1.playSound(p1, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 0);
            setQueueHoverTexts(game);
        }
    }

    private void setQueueHoverTexts(@NotNull String game) {
        for (String s : LobbyPlugin.getInstance().getMiniGameBlockUtil().getList(game.toLowerCase())) {
            Location blockLoc = LobbyPlugin.getInstance().getLocationAPI().parseLocation(s);
            Location loc = new Location(blockLoc.getWorld(), blockLoc.getBlockX() + 0.5, blockLoc.getBlockY() + 3, blockLoc.getBlockZ() + 0.5);
            Component englishComponent = LobbyPlugin.getInstance().getLanguageAPI().getMessage(Language.ENGLISH, "minigame.queue");
            Component germanComponent = LobbyPlugin.getInstance().getLanguageAPI().getMessage(Language.GERMAN, "minigame.queue");
            String[] englishString = LegacyComponentSerializer.legacyAmpersand().serialize(englishComponent).replaceAll("&", "§").split(" ");
            String[] germanString = LegacyComponentSerializer.legacyAmpersand().serialize(germanComponent).replaceAll("&", "§").split(" ");

            String[] german = new String[]{germanString[0] + " " + germanString[1], germanString[2] + " " + germanString[3]};
            String[] english = new String[]{englishString[0] + " " + englishString[1], englishString[2] + " " + englishString[3]};

            LobbyPlugin.getInstance().getHologramAPI().addHologram(game + "." + s, new dev.nachwahl.lobby.hologram.Hologram(loc, english, german,
                    game + " " + loc.getBlockX() + "-" + loc.getBlockZ()));
        }
    }
}
