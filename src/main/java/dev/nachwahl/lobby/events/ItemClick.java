package dev.nachwahl.lobby.events;

import dev.nachwahl.lobby.LobbyPlugin;
import dev.nachwahl.lobby.guis.AccountGUI;
import dev.nachwahl.lobby.guis.NavigatorGUI;
import lombok.SneakyThrows;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
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

import java.util.Objects;

public class ItemClick implements Listener {

    private LobbyPlugin lobbyPlugin;

    public ItemClick(LobbyPlugin lobbyPlugin) {
        this.lobbyPlugin = lobbyPlugin;
    }

    @SneakyThrows
    @EventHandler
    public void onItemClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {


            this.lobbyPlugin.getLanguageAPI().getLanguage(player, language -> {
                if (event.getItem() != null && event.getItem().getItemMeta().displayName() != null) {
                    if (event.getItem().getItemMeta().displayName().equals(this.lobbyPlugin.getLanguageAPI().getMessage(language, "navigator.itemName"))) {
                        new NavigatorGUI(this.lobbyPlugin, player);
                    }
                    if (event.getItem().getItemMeta().displayName().equals(this.lobbyPlugin.getLanguageAPI().getMessage(language, "account.itemName", Placeholder.parsed("name", player.getName())))) {
                        new AccountGUI(this.lobbyPlugin, player);
                    }
                    if (event.getItem().getItemMeta().displayName().equals(this.lobbyPlugin.getLanguageAPI().getMessage(language, "manage.editMode"))) {

                        Bukkit.getScheduler().runTask(lobbyPlugin, () -> player.performCommand("lm edit"));
                    }
                }
            });

        }
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) && Objects.equals(event.getHand(), EquipmentSlot.HAND)) {

            Block block = event.getClickedBlock();
            Location youtube = this.lobbyPlugin.getLocationAPI().parseLocation(this.lobbyPlugin.getConfig().getString("socials.youtube"));
            Location twitch = this.lobbyPlugin.getLocationAPI().parseLocation(this.lobbyPlugin.getConfig().getString("socials.twitch"));
            Location tiktok = this.lobbyPlugin.getLocationAPI().parseLocation(this.lobbyPlugin.getConfig().getString("socials.tiktok"));
            Location instagram = this.lobbyPlugin.getLocationAPI().parseLocation(this.lobbyPlugin.getConfig().getString("socials.instagram"));
            Location website = this.lobbyPlugin.getLocationAPI().parseLocation(this.lobbyPlugin.getConfig().getString("socials.website"));
            Location discord = this.lobbyPlugin.getLocationAPI().parseLocation(this.lobbyPlugin.getConfig().getString("socials.discord"));
            Location location = block.getLocation();

            this.lobbyPlugin.getLanguageAPI().getLanguage(player, language -> {

                net.kyori.adventure.text.Component textComponent = null;

                String url = "";
                if (location.equals(youtube)) {
                    url = "https://www.youtube.com/@BTEGermany";
                    textComponent = this.lobbyPlugin.getLanguageAPI().getMessage(language, "advertisement.checkOut", Placeholder.parsed("platform", "<color:#FF0000>YouTube</color>"));
                } else if (location.equals(twitch)) {
                    url = "https://www.twitch.tv/btegermany";
                    textComponent = this.lobbyPlugin.getLanguageAPI().getMessage(language, "advertisement.checkOut", Placeholder.parsed("platform", "<color:#9046ff>Twitch</color>"));
                } else if (location.equals(tiktok)) {
                    url = "https://www.tiktok.com/@btegermany";
                    textComponent = this.lobbyPlugin.getLanguageAPI().getMessage(language, "advertisement.checkOut", Placeholder.parsed("platform", "<color:#ff0050>Tik</color><color:#00f2ea>Tok</color>"));
                } else if (location.equals(instagram)) {
                    url = "https://www.instagram.com/btegermany/";
                    textComponent = this.lobbyPlugin.getLanguageAPI().getMessage(language, "advertisement.checkOut", Placeholder.parsed("platform", "<gradient:#f56040:#833ab4>Instagram</gradient>"));
                } else if (location.equals(website)) {
                    url = "https://bte-germany.de/";
                    textComponent = this.lobbyPlugin.getLanguageAPI().getMessage(language, "advertisement.checkOut", Placeholder.parsed("platform", "<color:#63b3ed>bte-germany.de</color>"));
                } else if (location.equals(discord)) {
                    url = "https://discord.gg/GkSxGTYaAJ";
                    textComponent = this.lobbyPlugin.getLanguageAPI().getMessage(language, "advertisement.checkOut", Placeholder.parsed("platform", "<color:#5865F2>Discord</color>"));
                } else {
                    return;
                }
                net.kyori.adventure.text.Component hoverEventComponent = textComponent.hoverEvent(this.lobbyPlugin.getLanguageAPI().getMessage(language, "advertisement.openURL", Placeholder.parsed("url", url)));
                player.sendMessage(hoverEventComponent.clickEvent(ClickEvent.openUrl(url)));
            });

        }

        if ((event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) && !player.getPassengers().isEmpty()) {
                Player passenger = (Player) player.getPassengers().stream().filter(Player.class::isInstance).findFirst().orElse(null);

                if (passenger == null) return;
                player.removePassenger(passenger);
                Vector v = passenger.getLocation().getDirection().multiply(2D).setY(2D);
                passenger.setVelocity(v);
            }

    }

    @EventHandler
    public void onEntityClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof Player) {
            Player passenger = (Player) event.getRightClicked();
            LobbyPlugin.getInstance().getUserSettingsAPI().getBooleanSetting(player, "playerPickup", (result) -> {
                if (!result)
                    return;
                LobbyPlugin.getInstance().getUserSettingsAPI().getBooleanSetting(passenger, "playerPickup", (result2) -> {
                    if (!result2)
                        return;

                    Bukkit.getScheduler().runTask(LobbyPlugin.getInstance(), () -> player.addPassenger(passenger));
                });
            });
        }
    }
}
