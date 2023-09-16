package dev.nachwahl.lobby.utils;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.language.LanguageAPI;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
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
            ItemStack account = ItemBuilder.from(Material.REPEATER).name(this.languageAPI.getMessage(language, "account.itemName")).build();

            player.getInventory().clear();

            player.getInventory().setItem(4, navigator);
            player.getInventory().setItem(6, account);

            if(player.hasPermission("lobby.manage.edit")) {
                ItemStack buildMode = ItemBuilder.from(Material.GOLDEN_AXE).name(this.languageAPI.getMessage(language, "manage.editMode")).build();
                player.getInventory().setItem(8,buildMode);
            }
            // Bukkit.getScheduler().runTask(lobby,() -> player.performCommand("cosmetics item"));


        });
    }
}
