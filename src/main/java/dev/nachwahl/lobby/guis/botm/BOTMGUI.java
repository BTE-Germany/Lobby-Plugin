package dev.nachwahl.lobby.guis.botm;

import dev.nachwahl.lobby.LobbyPlugin;
import dev.triumphteam.gui.builder.item.PaperItemBuilder;
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
    private final LobbyPlugin lobbyPlugin;

    public BOTMGUI(LobbyPlugin lobbyPlugin, Player player) {
        this.lobbyPlugin = lobbyPlugin;

        Bukkit.getScheduler().runTask(this.lobbyPlugin, () ->
            this.lobbyPlugin.getLanguageAPI().getLanguage(player, language -> {

                this.gui = Gui.gui()
                        .title(this.lobbyPlugin.getLanguageAPI().getMessage(language, "botm-gui.title"))
                        .rows(3)
                        .disableAllInteractions()
                        .create();

                this.gui.setItem(2, 3, PaperItemBuilder.from(Material.NETHER_STAR)
                        .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "botm-gui.add_entry"))
                        .asGuiItem(event -> {
                            player.closeInventory();
                            new AddEntryGUI(lobbyPlugin, player);
                        }));

                this.gui.setItem(2, 5, PaperItemBuilder.from(Material.ORANGE_BANNER)
                        .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "botm-gui.create_hologram"))
                        .asGuiItem(event -> {
                            if (DHAPI.getHologram("BOTM") == null) {
                                try {
                                    Location location = player.getLocation();
                                    player.sendMessage(lobbyPlugin.getBotmScoreAPI().create(location.add(0, 4,0 ), lobbyPlugin.getDatabase(), language));
                                    lobbyPlugin.getLocationAPI().setLocation(location, "botm");
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
                                lobbyPlugin.getLocationAPI().setLocation(location, "botm");
                                lobbyPlugin.getLanguageAPI().sendMessageToPlayer(player, "botm.moved");
                            }
                        }));

                this.gui.setItem(2, 7, PaperItemBuilder.from(Material.PAPER)
                        .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "botm-gui.list_entries"))
                        .asGuiItem(event -> {
                            try {
                                new BOTMList(lobbyPlugin, player, 1);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }));

                this.gui.setItem(1, 9, PaperItemBuilder.from(Material.SUNFLOWER)
                        .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "botm-gui.reload"))
                        .asGuiItem(event -> {
                            try {
                                this.lobbyPlugin.getBotmScoreAPI().reload(player);
                                player.sendMessage(this.lobbyPlugin.getLanguageAPI().getMessage(language, "botm-gui.reload.success"));
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            } catch (ExecutionException e) {
                                throw new RuntimeException(e);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }));

                this.gui.getFiller().fill(PaperItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());
                this.gui.open(player);
        }));
    }
}
