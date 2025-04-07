package dev.nachwahl.lobby.utils;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.language.LanguageAPI;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class HotbarItems {
    private final Lobby lobby;
    private final LanguageAPI languageAPI;

    @Contract(pure = true)
    public HotbarItems(@NotNull Lobby lobby) {
        this.lobby = lobby;
        this.languageAPI = lobby.getLanguageAPI();
    }

    /**
     * Setzt die Hotbar Items in der richtigen Sprache. Implizit wird durch languageAPI.getLanguage auch die language Permission gesetzt.
     */
    public void setHotbarItems(Player player) {
        this.languageAPI.getLanguage(player, language -> {
            ItemStack navigator = ItemBuilder.from(Material.COMPASS).name(this.languageAPI.getMessage(language, "navigator.itemName")).build();
            ItemStack account = ItemBuilder.from(ItemGenerator.customModel(Material.PAPER, "config")).name(this.languageAPI.getMessage(language, "account.itemName")).build();

            player.getInventory().clear();

            player.getInventory().setItem(4, navigator);
            player.getInventory().setItem(6, account);

            setElytra(player, lobby);

            if (player.hasPermission("lobby.manage.edit")) {
                ItemStack buildMode = ItemBuilder.from(Material.GOLDEN_AXE).name(this.languageAPI.getMessage(language, "manage.editMode")).build();
                player.getInventory().setItem(8, buildMode);
            }
        });
    }

    public static void setElytra(@NotNull Player player, Lobby lobby) {
        if (player.getInventory().getChestplate() != null && player.getInventory().getChestplate().getType().equals(Material.ELYTRA)) return;
        if (!lobby.getElytraPlayers().containsKey(player.getUniqueId()))
            lobby.getElytraPlayers().put(player.getUniqueId(), player.getInventory().getChestplate());
        player.getInventory().setChestplate(ItemBuilder.from(Material.ELYTRA).enchant(Enchantment.MENDING).build());
    }
}
