package dev.nachwahl.lobby.guis;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.language.Language;
import dev.nachwahl.lobby.utils.ItemGenerator;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.builder.item.SkullBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AccountGUI {

    private Gui gui;
    private final Lobby lobby;

    final String HEAD_ON = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWMwMWY2Nzk2ZWI2M2QwZThhNzU5MjgxZDAzN2Y3YjM4NDMwOTBmOWE0NTZhNzRmNzg2ZDA0OTA2NWM5MTRjNyJ9fX0=";
    final String HEAD_OFF = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQ4ZDdkMWUwM2UxYWYxNDViMDEyNWFiODQxMjg1NjcyYjQyMTI2NWRhMmFiOTE1MDE1ZjkwNTg0MzhiYTJkOCJ9fX0=";


    public AccountGUI(Lobby lobby, Player player) {
        this.lobby = lobby;
        this.lobby.getLanguageAPI().getLanguage(player, language -> {
            this.gui = Gui.gui()
                    .title(this.lobby.getLanguageAPI().getMessage(language, "account.title", Placeholder.parsed("name", player.getName())))
                    .rows(5)
                    .disableAllInteractions()
                    .create();

            this.gui.setItem(1, 5, ItemBuilder.skull().owner(Bukkit.getOfflinePlayer(player.getUniqueId()))
                    .name(this.lobby.getLanguageAPI().getMessage(language, "account.skull.name",Placeholder.parsed("name",player.getName())))
                    .asGuiItem(event -> {
                    }));


            // Player Visibility

            this.gui.setItem(3, 2,
                    ItemBuilder.from(Material.TINTED_GLASS).name(
                            this.lobby.getLanguageAPI().getMessage(language, "account.players.name")).asGuiItem()
            );

            this.lobby.getUserSettingsAPI().getBooleanSetting(player, "playerVisibility", (setting) -> {
                this.gui.setItem(4, 2,
                        ItemBuilder.skull().texture(setting ? HEAD_ON : HEAD_OFF)
                                .name(this.lobby.getLanguageAPI().getMessage(language, setting ? "account.on" : "account.off"))
                                .asGuiItem(event -> {
                                    this.lobby.getUserSettingsAPI().toggleSetting(player, "playerVisibility", (i) -> {
                                        this.lobby.getUserSettingsAPI().getBooleanSetting(player, "playerVisibility", (newSetting) -> {
                                            this.gui.updateItem(4, 2, ItemBuilder.skull().texture(newSetting ? HEAD_ON : HEAD_OFF).name(this.lobby.getLanguageAPI().getMessage(language, newSetting ? "account.on" : "account.off")).build());

                                            Bukkit.getOnlinePlayers().forEach((p) -> {
                                                if (newSetting == false) {
                                                    Bukkit.getScheduler().runTask(this.lobby, () -> {
                                                        player.hidePlayer(this.lobby, p);
                                                    });

                                                } else {
                                                    Bukkit.getScheduler().runTask(this.lobby, () -> {
                                                        player.showPlayer(this.lobby, p);
                                                    });
                                                }
                                            });

                                        });
                                    });
                                }));
            });

            // Language Item

            this.gui.setItem(3,4,ItemBuilder.skull().texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThkYWExZTNlZDk0ZmYzZTMzZTFkNGM2ZTQzZjAyNGM0N2Q3OGE1N2JhNGQzOGU3NWU3YzkyNjQxMDYifX19").name(this.lobby.getLanguageAPI().getMessage(language,"account.language.name")).asGuiItem());
/*Ü
            ItemBu languageItem = ItemBuilder.skull().texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWU3ODk5YjQ4MDY4NTg2OTdlMjgzZjA4NGQ5MTczZmU0ODc4ODY0NTM3NzQ2MjZiMjRiZDhjZmVjYzc3YjNmIn19fQ==");
            if (this.lobby.getLanguageAPI().getLanguage(player).equals(Language.ENGLISH)) {
                languageItem = ItemBuilder.skull().texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNhYzk3NzRkYTEyMTcyNDg1MzJjZTE0N2Y3ODMxZjY3YTEyZmRjY2ExY2YwY2I0YjM4NDhkZTZiYzk0YjQifX19");
            }*/
            ItemBuilder languageItem = ItemBuilder.from(ItemGenerator.customModel(Material.PAPER,9));

            if(this.lobby.getLanguageAPI().getLanguage(player).equals(Language.ENGLISH)) {
                languageItem = ItemBuilder.from(ItemGenerator.customModel(Material.PAPER,10));
            }

            this.gui.setItem(4, 4,
                    languageItem
                            .name(this.lobby.getLanguageAPI().getMessage(language, "language"))
                            .asGuiItem(event -> {
                                new LanguageGUI(lobby, player);
                            }));

            // Real Time

            this.gui.setItem(3, 6,
                    ItemBuilder.from(Material.CLOCK).name(
                            this.lobby.getLanguageAPI().getMessage(language, "account.realtime.name")).asGuiItem()
            );

            this.lobby.getUserSettingsAPI().getBooleanSetting(player, "realTime", (setting) -> {
                this.gui.setItem(4, 6,
                        ItemBuilder.skull().texture(setting ? HEAD_ON : HEAD_OFF)
                                .name(this.lobby.getLanguageAPI().getMessage(language, setting ? "account.on" : "account.off"))
                                .asGuiItem(event -> {
                                    this.lobby.getUserSettingsAPI().toggleSetting(player, "realTime", (i) -> {
                                        this.lobby.getUserSettingsAPI().getBooleanSetting(player, "realTime", (newSetting) -> {
                                            this.gui.updateItem(4, 6, ItemBuilder.skull().texture(newSetting ? HEAD_ON : HEAD_OFF).name(this.lobby.getLanguageAPI().getMessage(language, newSetting ? "account.on" : "account.off")).build());

                                            Bukkit.getOnlinePlayers().forEach((p) -> {
                                                if (!newSetting) {
                                                    player.resetPlayerTime();
                                                } else {
                                                    player.setPlayerTime(lobby.getRealTime().getTime(),false);
                                                }
                                            });

                                        });
                                    });
                                }));
            });

            // Pickup Item

            this.gui.setItem(3, 8,
                    ItemBuilder.from(Material.PLAYER_HEAD).name(
                            this.lobby.getLanguageAPI().getMessage(language, "account.pickup.name")).asGuiItem()
            );

            this.lobby.getUserSettingsAPI().getBooleanSetting(player, "playerPickup", (setting) -> {
                this.gui.setItem(4, 8,
                        ItemBuilder.skull().texture(setting ? HEAD_ON : HEAD_OFF)
                                .name(this.lobby.getLanguageAPI().getMessage(language, setting ? "account.on" : "account.off"))
                                .asGuiItem(event -> {
                                    this.lobby.getUserSettingsAPI().toggleSetting(player, "playerPickup", (i) -> {
                                        this.lobby.getUserSettingsAPI().getBooleanSetting(player, "playerPickup", (newSetting) -> {
                                            this.gui.updateItem(4, 8, ItemBuilder.skull().texture(newSetting ? HEAD_ON : HEAD_OFF).name(this.lobby.getLanguageAPI().getMessage(language, newSetting ? "account.on" : "account.off")).build());
                                        });
                                    });
                                }));
            });

            this.gui.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());
            Bukkit.getScheduler().runTask(this.lobby, () -> this.gui.open(player));



        });
    }


}
