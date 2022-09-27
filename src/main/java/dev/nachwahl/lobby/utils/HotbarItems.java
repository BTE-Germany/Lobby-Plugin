package dev.nachwahl.lobby.utils;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.utils.language.LanguageAPI;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import org.bukkit.Material;
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

            player.getInventory().clear();

            player.getInventory().setItem(4, navigator);
        });
    }
}
