package dev.nachwahl.lobby.guis;

import dev.nachwahl.lobby.LobbyPlugin;
import dev.nachwahl.lobby.language.Language;
import dev.nachwahl.lobby.utils.ItemGenerator;
import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class AccountGUI {

    private Gui gui;
    private final LobbyPlugin lobbyPlugin;

    final String HEAD_ON = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWMwMWY2Nzk2ZWI2M2QwZThhNzU5MjgxZDAzN2Y3YjM4NDMwOTBmOWE0NTZhNzRmNzg2ZDA0OTA2NWM5MTRjNyJ9fX0=";
    final String HEAD_OFF = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQ4ZDdkMWUwM2UxYWYxNDViMDEyNWFiODQxMjg1NjcyYjQyMTI2NWRhMmFiOTE1MDE1ZjkwNTg0MzhiYTJkOCJ9fX0=";


    public AccountGUI(LobbyPlugin lobbyPlugin, Player player) {
        this.lobbyPlugin = lobbyPlugin;
        this.lobbyPlugin.getLanguageAPI().getLanguage(player, language -> {
            this.gui = Gui.gui()
                    .title(this.lobbyPlugin.getLanguageAPI().getMessage(language, "account.title", Placeholder.parsed("name", player.getName())))
                    .rows(5)
                    .disableAllInteractions()
                    .create();

            this.gui.setItem(1, 5, PaperItemBuilder.skull().owner(Bukkit.getOfflinePlayer(player.getUniqueId()))
                    .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "account.skull.name", Placeholder.parsed("name", player.getName())))
                    .asGuiItem(event -> {
                    }));


            // Player Visibility

            this.gui.setItem(3, 2,
                    PaperItemBuilder.from(Material.TINTED_GLASS).name(
                            this.lobbyPlugin.getLanguageAPI().getMessage(language, "account.players.name")).asGuiItem()
            );

            this.lobbyPlugin.getUserSettingsAPI().getBooleanSetting(player, "playerVisibility", (setting) -> {
                this.gui.setItem(4, 2,
                        PaperItemBuilder.skull().texture(setting ? HEAD_ON : HEAD_OFF)
                                .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, setting ? "account.on" : "account.off"))
                                .asGuiItem(event -> {
                                    this.lobbyPlugin.getUserSettingsAPI().toggleSetting(player, "playerVisibility", (i) -> {
                                        this.lobbyPlugin.getUserSettingsAPI().getBooleanSetting(player, "playerVisibility", (newSetting) -> {
                                            this.gui.updateItem(4, 2, PaperItemBuilder.skull().texture(newSetting ? HEAD_ON : HEAD_OFF).name(this.lobbyPlugin.getLanguageAPI().getMessage(language, newSetting ? "account.on" : "account.off")).build());

                                            Bukkit.getOnlinePlayers().forEach((p) -> {
                                                if (newSetting == false) {
                                                    Bukkit.getScheduler().runTask(this.lobbyPlugin, () -> {
                                                        player.hidePlayer(this.lobbyPlugin, p);
                                                    });

                                                } else {
                                                    Bukkit.getScheduler().runTask(this.lobbyPlugin, () -> {
                                                        player.showPlayer(this.lobbyPlugin, p);
                                                    });
                                                }
                                            });

                                        });
                                    });
                                }));
            });

            // Language Item

            this.gui.setItem(3, 4, PaperItemBuilder.skull().texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThkYWExZTNlZDk0ZmYzZTMzZTFkNGM2ZTQzZjAyNGM0N2Q3OGE1N2JhNGQzOGU3NWU3YzkyNjQxMDYifX19").name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "account.language.name")).asGuiItem());
/*Ü
            ItemBu languageItem = PaperItemBuilder.skull().texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWU3ODk5YjQ4MDY4NTg2OTdlMjgzZjA4NGQ5MTczZmU0ODc4ODY0NTM3NzQ2MjZiMjRiZDhjZmVjYzc3YjNmIn19fQ==");
            if (this.lobby.getLanguageAPI().getLanguage(player).equals(Language.ENGLISH)) {
                languageItem = PaperItemBuilder.skull().texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNhYzk3NzRkYTEyMTcyNDg1MzJjZTE0N2Y3ODMxZjY3YTEyZmRjY2ExY2YwY2I0YjM4NDhkZTZiYzk0YjQifX19");
            }*/
            PaperItemBuilder languageItem = PaperItemBuilder.from(ItemGenerator.customModel(Material.PAPER, "de_flag"));

            if (this.lobbyPlugin.getLanguageAPI().getLanguage(player).equals(Language.ENGLISH)) {
                languageItem = PaperItemBuilder.from(ItemGenerator.customModel(Material.PAPER, "us_flag"));
            }

            this.gui.setItem(4, 4,
                    languageItem
                            .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "language"))
                            .asGuiItem(event -> {
                                new LanguageGUI(lobbyPlugin, player);
                            }));

            // Real Time

            this.gui.setItem(3, 6,
                    PaperItemBuilder.from(Material.CLOCK).name(
                            this.lobbyPlugin.getLanguageAPI().getMessage(language, "account.realtime.name")).asGuiItem()
            );

            this.lobbyPlugin.getUserSettingsAPI().getBooleanSetting(player, "realTime", (setting) -> {
                this.gui.setItem(4, 6,
                        PaperItemBuilder.skull().texture(setting ? HEAD_ON : HEAD_OFF)
                                .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, setting ? "account.on" : "account.off"))
                                .asGuiItem(event -> {
                                    this.lobbyPlugin.getUserSettingsAPI().toggleSetting(player, "realTime", (i) -> {
                                        this.lobbyPlugin.getUserSettingsAPI().getBooleanSetting(player, "realTime", (newSetting) -> {
                                            this.gui.updateItem(4, 6, PaperItemBuilder.skull().texture(newSetting ? HEAD_ON : HEAD_OFF).name(this.lobbyPlugin.getLanguageAPI().getMessage(language, newSetting ? "account.on" : "account.off")).build());

                                            Bukkit.getOnlinePlayers().forEach((p) -> {
                                                if (!newSetting) {
                                                    player.resetPlayerTime();
                                                } else {
                                                    player.setPlayerTime(lobbyPlugin.getRealTime().getTime(), false);
                                                }
                                            });

                                        });
                                    });
                                }));
            });

            // Pickup Item

            this.gui.setItem(3, 8,
                    PaperItemBuilder.from(Material.PLAYER_HEAD).name(
                            this.lobbyPlugin.getLanguageAPI().getMessage(language, "account.pickup.name")).asGuiItem()
            );

            this.lobbyPlugin.getUserSettingsAPI().getBooleanSetting(player, "playerPickup", (setting) -> {
                this.gui.setItem(4, 8,
                        PaperItemBuilder.skull().texture(setting ? HEAD_ON : HEAD_OFF)
                                .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, setting ? "account.on" : "account.off"))
                                .asGuiItem(event -> {
                                    this.lobbyPlugin.getUserSettingsAPI().toggleSetting(player, "playerPickup", (i) -> {
                                        this.lobbyPlugin.getUserSettingsAPI().getBooleanSetting(player, "playerPickup", (newSetting) -> {
                                            this.gui.updateItem(4, 8, PaperItemBuilder.skull().texture(newSetting ? HEAD_ON : HEAD_OFF).name(this.lobbyPlugin.getLanguageAPI().getMessage(language, newSetting ? "account.on" : "account.off")).build());
                                        });
                                    });
                                }));
            });

            this.gui.getFiller().fill(PaperItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());
            Bukkit.getScheduler().runTask(this.lobbyPlugin, () -> this.gui.open(player));


        });
    }


}
