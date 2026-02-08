package dev.nachwahl.lobby.utils;

import dev.nachwahl.lobby.LobbyPlugin;
import dev.nachwahl.lobby.language.LanguageAPI;
import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class HotbarItems {
    private final LobbyPlugin lobbyPlugin;
    private final LanguageAPI languageAPI;

    @Contract(pure = true)
    public HotbarItems(@NotNull LobbyPlugin lobbyPlugin) {
        this.lobbyPlugin = lobbyPlugin;
        this.languageAPI = lobbyPlugin.getLanguageAPI();
    }

    /**
     * Setzt die Hotbar Items in der richtigen Sprache. Implizit wird durch languageAPI.getLanguage auch die language Permission gesetzt.
     */
    public void setHotbarItems(Player player) {
        LobbyPlugin.getInstance().getHologramAPI().sendDebugMsg(Component.text("Set Menu Items - language perm should be set"));
        this.languageAPI.getLanguage(player, language -> {
            ItemStack navigator = PaperItemBuilder.from(Material.COMPASS).name(this.languageAPI.getMessage(language, "navigator.itemName")).build();
            ItemStack account = PaperItemBuilder.from(ItemGenerator.customModel(Material.PAPER, "config")).name(this.languageAPI.getMessage(language, "account.itemName")).build();

            player.getInventory().clear();

            player.getInventory().setItem(4, navigator);
            player.getInventory().setItem(6, account);

            setElytra(player, lobbyPlugin);

            if (player.hasPermission("lobby.manage.edit")) {
                ItemStack buildMode = PaperItemBuilder.from(Material.GOLDEN_AXE).name(this.languageAPI.getMessage(language, "manage.editMode")).build();
                player.getInventory().setItem(8, buildMode);
            }
        });
    }

    public static void setElytra(@NotNull Player player, LobbyPlugin lobbyPlugin) {
        if (player.getInventory().getChestplate() != null && player.getInventory().getChestplate().getType().equals(Material.ELYTRA))
            return;
        if (!lobbyPlugin.getElytraPlayers().containsKey(player.getUniqueId()))
            lobbyPlugin.getElytraPlayers().put(player.getUniqueId(), player.getInventory().getChestplate());
        player.getInventory().setChestplate(PaperItemBuilder.from(Material.ELYTRA).enchant(Enchantment.MENDING).build());
    }
}
