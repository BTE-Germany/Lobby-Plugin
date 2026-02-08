package dev.nachwahl.lobby.events;

import co.aikar.idb.DbRow;
import dev.nachwahl.lobby.LobbyPlugin;
import dev.nachwahl.lobby.guis.PrivacyGUI;
import dev.nachwahl.lobby.language.Language;
import dev.nachwahl.lobby.utils.Actions;
import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import eu.decentsoftware.holograms.api.DHAPI;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class PlayerEvents implements Listener {

    private LobbyPlugin lobbyPlugin;

    public PlayerEvents(LobbyPlugin lobbyPlugin) {
        this.lobbyPlugin = lobbyPlugin;
    }

    @EventHandler
    public void onInventoryChange(InventoryInteractEvent event) {
        if (!this.lobbyPlugin.getEditModePlayers().contains((Player) event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    @SneakyThrows
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setGameMode(GameMode.ADVENTURE);
        Actions.performJoinActions(lobbyPlugin, player);
        if (this.lobbyPlugin.getConfig().getString("resourcepack") != null) {
            if (player.getResourcePackStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED && LobbyPlugin.getInstance().getHologramAPI().debugPlayer != null) {
                LobbyPlugin.getInstance().getHologramAPI().sendDebugMsg(Component.text("Skipped resourcepack loading because its already loaded"));
            } else
                player.setResourcePack("https://cdn.bte-germany.de/general/resourcepacks/resourcepack_bteg_" + this.lobbyPlugin.getConfig().getString("resourcepack") + ".zip", this.lobbyPlugin.getConfig().getString("resourcepack"), true, this.lobbyPlugin.getMiniMessage().deserialize("<red><b>Bitte akzeptiere unser Resourcepack um auf dem Server spielen zu können.\nPlease accept our resourcepack to play on our server.</b></red>"));
        }
        this.lobbyPlugin.getUserSettingsAPI().setDefaultSettings(player);
        DbRow user = this.lobbyPlugin.getDatabase().getFirstRow("SELECT * FROM privacy WHERE minecraftUUID = ?", player.getUniqueId().toString());
        this.lobbyPlugin.getLocationAPI().teleportToLocation(player, "spawn", false);

        event.joinMessage(Component.empty());

        if (user == null) {
            new PrivacyGUI(player, this.lobbyPlugin).getGui().open(player);
        }

        // Init scoreboard
        if (player.hasPermission("lobby.scoreboard")) {
            this.lobbyPlugin.getScoreboard().initScoreboard(player);
        }

        if (DHAPI.getHologram("BOTM") == null && lobbyPlugin.getLocationAPI().getLocation("botm") != null) {
            try {
                Location location = lobbyPlugin.getLocationAPI().getLocation("botm");
                if (location != null) {
                    lobbyPlugin.getBotmScoreAPI().create(location, lobbyPlugin.getDatabase(), Language.GERMAN);
                }
            } catch (SQLException e) {
                Bukkit.getLogger().warning("Es wurde keine Location für das BOTM Hologramm gefunden.");
                throw new RuntimeException(e);
            }
        }

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        event.quitMessage(Component.empty());
        this.lobbyPlugin.getVanish().remove(event.getPlayer());
        this.lobbyPlugin.getScoreboard().removeScoreboard(event.getPlayer());
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
            if (!this.lobbyPlugin.getEditModePlayers().contains(player)) {
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getChestplate() == null) return;
        if (player.getLocation().getY() > 150) {
            if (player.getInventory().getChestplate().getType().equals(Material.ELYTRA)) return;
            if (!this.lobbyPlugin.getElytraPlayers().containsKey(player.getUniqueId()))
                this.lobbyPlugin.getElytraPlayers().put(player.getUniqueId(), player.getInventory().getChestplate());
            player.getInventory().setChestplate(PaperItemBuilder.from(Material.ELYTRA).enchant(Enchantment.MENDING).build());
            return;
        }
        ;
        if (player.getLocation().add(0, -1, 0).getBlock().getType() == Material.AIR) return;
        if (!this.lobbyPlugin.getElytraPlayers().containsKey(player.getUniqueId())) return;
        ItemStack item = this.lobbyPlugin.getElytraPlayers().remove(player.getUniqueId());
        player.getInventory().setChestplate(item);
    }
}
