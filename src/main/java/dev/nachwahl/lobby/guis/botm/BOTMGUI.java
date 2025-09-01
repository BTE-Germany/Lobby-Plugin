package dev.nachwahl.lobby.guis.botm;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.utils.ItemGenerator;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import eu.decentsoftware.holograms.api.DHAPI;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class BOTMGUI {

    @Getter
    private Gui gui;
    private final Lobby lobby;

    public BOTMGUI(Lobby lobby, Player player) {
        this.lobby = lobby;

        Bukkit.getScheduler().runTask(this.lobby, () ->
            this.lobby.getLanguageAPI().getLanguage(player, language -> {

                this.gui = Gui.gui()
                        .title(this.lobby.getLanguageAPI().getMessage(language, "botm-gui.title"))
                        .rows(3)
                        .disableAllInteractions()
                        .create();

                this.gui.setItem(2, 3, ItemBuilder.from(Material.NETHER_STAR)
                        .name(this.lobby.getLanguageAPI().getMessage(language, "botm-gui.add_entry"))
                        .asGuiItem(event -> {
                            player.closeInventory();
                            new AddEntryGUI(lobby, player);
                        }));

                this.gui.setItem(2, 5, ItemBuilder.from(Material.ORANGE_BANNER)
                        .name(this.lobby.getLanguageAPI().getMessage(language, "botm-gui.create_hologram"))
                        .asGuiItem(event -> {
                            if (DHAPI.getHologram("BOTM") == null) {
                                try {
                                    Location location = player.getLocation();
                                    player.sendMessage(lobby.getBotmScoreAPI().create(location.add(0, 4,0 ), lobby.getDatabase(), language));
                                    lobby.getLocationAPI().setLocation(location, "botm");
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                } catch (ExecutionException e) {
                                    throw new RuntimeException(e);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }else {
                                Location location = player.getLocation();
                                DHAPI.moveHologram("BOTM", location.add(0, 4,0 ));
                                lobby.getLocationAPI().setLocation(location, "botm");
                                lobby.getLanguageAPI().sendMessageToPlayer(player, "botm.moved");
                            }
                        }));

                this.gui.setItem(2, 7, ItemBuilder.from(Material.PAPER)
                        .name(this.lobby.getLanguageAPI().getMessage(language, "botm-gui.list_entries"))
                        .lore(this.lobby.getLanguageAPI().getMessage(language, "coming-soon"))
                        .asGuiItem(event -> {
                            //comming soon
                        }));

                this.gui.setItem(1, 9, ItemBuilder.from(Material.SUNFLOWER)
                        .name(this.lobby.getLanguageAPI().getMessage(language, "botm-gui.reload"))
                        .asGuiItem(event -> {
                            try {
                                this.lobby.getBotmScoreAPI().reload(player);
                                player.sendMessage(this.lobby.getLanguageAPI().getMessage(language, "botm-gui.reload.success"));
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            } catch (ExecutionException e) {
                                throw new RuntimeException(e);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }));

                this.gui.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());
                this.gui.open(player);
        }));
    }
}
