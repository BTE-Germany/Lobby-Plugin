package dev.nachwahl.lobby.utils;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.language.LanguageAPI;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HotbarItems {
    private Lobby lobby;
    private LanguageAPI languageAPI;

    public HotbarItems(Lobby lobby) {
        this.lobby = lobby;
        this.languageAPI = lobby.getLanguageAPI();
    }

    public void setHotbarItems(Player player) {
        this.languageAPI.getLanguage(player, language -> {
            ItemStack navigator = ItemBuilder.from(Material.COMPASS).name(this.languageAPI.getMessage(language, "navigator.itemName")).build();
            ItemStack account = ItemBuilder.from(ItemGenerator.customModel(Material.PAPER, 11)).name(this.languageAPI.getMessage(language, "account.itemName")).build();

            player.getInventory().clear();

            player.getInventory().setItem(4, navigator);
            player.getInventory().setItem(6, account);

            setElytra(player, lobby);

            if (player.hasPermission("lobby.manage.edit")) {
                ItemStack buildMode = ItemBuilder.from(Material.GOLDEN_AXE).name(this.languageAPI.getMessage(language, "manage.editMode")).build();
                player.getInventory().setItem(8, buildMode);
            }
            // Bukkit.getScheduler().runTask(lobby,() -> player.performCommand("cosmetics item"));


        });
    }

    public static void setElytra(Player player, Lobby lobby) {
        if (player.getInventory().getChestplate() != null && player.getInventory().getChestplate().getType().equals(Material.ELYTRA)) return;
        if (!lobby.getElytraPlayers().containsKey(player.getUniqueId()))
            lobby.getElytraPlayers().put(player.getUniqueId(), player.getInventory().getChestplate());
        player.getInventory().setChestplate(ItemBuilder.from(Material.ELYTRA).enchant(Enchantment.MENDING).build());
    }
}
