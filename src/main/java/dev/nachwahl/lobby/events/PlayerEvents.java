package dev.nachwahl.lobby.events;

import co.aikar.idb.DbRow;
import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.guis.PrivacyGUI;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.lang.management.ManagementFactory;
import java.util.Objects;

public class PlayerEvents implements Listener {

    private Lobby lobby;

    public PlayerEvents(Lobby lobby) {
        this.lobby = lobby;
    }

    @EventHandler
    public void onInventoryChange(InventoryInteractEvent event) {
        if (!this.lobby.getEditModePlayers().contains((Player) event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    @SneakyThrows
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        DbRow user = this.lobby.getDatabase().getFirstRow("SELECT * FROM privacy WHERE minecraftUUID = ?", player.getUniqueId().toString());
        this.lobby.getUserSettingsAPI().setDefaultSettings(player);
        if (user == null) {
            new PrivacyGUI(player, this.lobby).getGui().open(player);
        } else {
        this.lobby.getHotbarItems().setHotbarItems(player);
        event.joinMessage(Component.empty());
        this.lobby.getLanguageAPI().getLanguage(player, language -> {
            final Title title = Title.title(
                    this.lobby.getLanguageAPI().getMessage(language, "welcomeTitle"),
                    this.lobby.getLanguageAPI().getMessage(language, "welcomeSubtitle"));
            player.showTitle(title);
            this.lobby.getHologramAPI().showHolograms(player,language);
        });
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        player.setFoodLevel(20);
        player.setHealth(20);
        this.lobby.getLocationAPI().teleportToLocation(player, "spawn", false);


        player.setAllowFlight(false); // No double jump
        player.setGameMode(GameMode.ADVENTURE);

        this.lobby.getUserSettingsAPI().getBooleanSetting(player,"playerVisibility",(value) -> {
            if(!value) {
                Bukkit.getOnlinePlayers().forEach((p) -> player.hidePlayer(this.lobby,p));
            }
        });}
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        event.quitMessage(Component.empty());
        this.lobby.getVanish().remove(event.getPlayer());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        event.setCancelled(true);
        event.getEntity().setFoodLevel(20);
        event.getEntity().setHealth(20);
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            Player player = (Player) event.getEntity();
            if (!this.lobby.getEditModePlayers().contains(player)) {
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(EntityDropItemEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            Player player = (Player) event.getEntity();
            if (!this.lobby.getEditModePlayers().contains(player)) {
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(player.getInventory().getChestplate()==null) return;
        if(player.getLocation().getY()>150) {
            if(player.getInventory().getChestplate().getType().equals(Material.ELYTRA)) return;
            if(!this.lobby.getElytraPlayers().containsKey(player.getUniqueId()))
                this.lobby.getElytraPlayers().put(player.getUniqueId(),player.getInventory().getChestplate());
            player.getInventory().setChestplate(ItemBuilder.from(Material.ELYTRA).enchant(Enchantment.MENDING).build());
            return;
        };
        if(player.getLocation().add(0,-1,0).getBlock().getType()==Material.AIR) return;
        if(!this.lobby.getElytraPlayers().containsKey(player.getUniqueId())) return;
        ItemStack item = this.lobby.getElytraPlayers().remove(player.getUniqueId());
        player.getInventory().setChestplate(item);
    }
}
