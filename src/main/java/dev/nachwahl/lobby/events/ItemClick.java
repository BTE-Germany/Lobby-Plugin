package dev.nachwahl.lobby.events;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.utils.guis.AccountGUI;
import dev.nachwahl.lobby.utils.guis.NavigatorGUI;
import lombok.SneakyThrows;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import java.awt.*;
import java.awt.TextComponent;
import java.util.Objects;

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
        if((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) && Objects.equals(event.getHand(), EquipmentSlot.HAND)){

            Block block = event.getClickedBlock();
            Location youtube = this.lobby.getLocationAPI().parseLocation(this.lobby.getConfig().getString("socials.youtube"));
            Location twitch =this.lobby.getLocationAPI().parseLocation(this.lobby.getConfig().getString("socials.twitch"));
            Location tiktok = this.lobby.getLocationAPI().parseLocation(this.lobby.getConfig().getString("socials.tiktok"));
            Location instagram = this.lobby.getLocationAPI().parseLocation(this.lobby.getConfig().getString("socials.instagram"));
            Location website = this.lobby.getLocationAPI().parseLocation(this.lobby.getConfig().getString("socials.website"));
            Location discord =this.lobby.getLocationAPI().parseLocation(this.lobby.getConfig().getString("socials.discord"));
            Location location = block.getLocation();

            this.lobby.getLanguageAPI().getLanguage(player, language -> {

                net.kyori.adventure.text.Component textComponent = null;

                String url = "";
                if (location.equals(youtube)) {
                    url = "https://www.youtube.com/@BTEGermany";
                    textComponent = this.lobby.getLanguageAPI().getMessage(language, "advertisement.checkOut", Placeholder.parsed("platform", "<red>YouTube</red>"));
                } else if (location.equals(twitch)) {
                    url = "https://www.twitch.tv/btegermany";
                    textComponent = this.lobby.getLanguageAPI().getMessage(language, "advertisement.checkOut", Placeholder.parsed("platform", "<dark_purple>Twitch</dark_purple>"));
                } else if (location.equals(tiktok)) {
                    url = "https://www.tiktok.com/@btegermany";
                    textComponent = this.lobby.getLanguageAPI().getMessage(language, "advertisement.checkOut", Placeholder.parsed("platform", "<black>TikTok</black>"));
                } else if (location.equals(instagram)) {
                    url = "https://www.instagram.com/btegermany/";
                    textComponent = this.lobby.getLanguageAPI().getMessage(language, "advertisement.checkOut", Placeholder.parsed("platform", "<red>Insta</red><light_purple>gram</light_purple>"));
                } else if (location.equals(website)) {
                    url = "https://bte-germany.de/";
                    textComponent = this.lobby.getLanguageAPI().getMessage(language, "advertisement.checkOut", Placeholder.parsed("platform", "<blue>bte-germany.de</blue>"));
                } else if (location.equals(discord)) {
                    url = "https://discord.gg/GkSxGTYaAJ";
                    textComponent = this.lobby.getLanguageAPI().getMessage(language, "advertisement.checkOut", Placeholder.parsed("platform", "<gray>Discord</gray>"));
                } else {
                    return;
                }
                net.kyori.adventure.text.Component hoverEventComponent = textComponent.hoverEvent(this.lobby.getLanguageAPI().getMessage(language, "advertisement.openURL", Placeholder.parsed("url", url)));
                player.sendMessage(hoverEventComponent.clickEvent(ClickEvent.openUrl(url)));
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
