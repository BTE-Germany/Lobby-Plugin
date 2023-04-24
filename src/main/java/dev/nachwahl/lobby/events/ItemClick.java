package dev.nachwahl.lobby.events;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.utils.guis.AccountGUI;
import dev.nachwahl.lobby.utils.guis.NavigatorGUI;
import lombok.SneakyThrows;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItemClick implements Listener {

    private Lobby lobby;

    public ItemClick(Lobby lobby) {
        this.lobby = lobby;
    }

    @SneakyThrows
    @EventHandler
    public void onItemClick(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            Player player = event.getPlayer();
            this.lobby.getLanguageAPI().getLanguage(player, language -> {
                if(event.getItem() != null) {
                    if(event.getItem().getItemMeta().displayName().equals(this.lobby.getLanguageAPI().getMessage(language, "navigator.itemName"))) {
                        new NavigatorGUI(this.lobby, player);
                    }
                    if(event.getItem().getItemMeta().displayName().equals(this.lobby.getLanguageAPI().getMessage(language, "account.itemName", Placeholder.parsed("name", player.getName())))) {
                        new AccountGUI(this.lobby, player);
                    }
                }
            });
        }
    }
}
