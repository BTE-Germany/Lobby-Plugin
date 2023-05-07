package dev.nachwahl.lobby.events;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.utils.guis.AccountGUI;
import dev.nachwahl.lobby.utils.guis.NavigatorGUI;
import lombok.SneakyThrows;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class ItemClick implements Listener {

    private Lobby lobby;

    public ItemClick(Lobby lobby) {
        this.lobby = lobby;
    }

    @SneakyThrows
    @EventHandler
    public void onItemClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
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
        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if(player.getPassengers().size()>=1) {
                Player passenger = (Player)  player.getPassengers().get(0);
                player.removePassenger(passenger);

                Vector v = player.getLocation().getDirection().multiply(2D).setY(2D);
                passenger.setVelocity(v);
            }
        }
    }

    @EventHandler
    public void onEntityClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if(event.getRightClicked() instanceof Player) {
            Player passenger = (Player) event.getRightClicked();
            Lobby.getInstance().getUserSettingsAPI().getBooleanSetting(passenger,"playerPickup",(result) -> {
                if(!result)
                    return;
                Bukkit.getScheduler().runTask(Lobby.getInstance(),() -> player.addPassenger(passenger));
            });
        }
    }
}
