package dev.nachwahl.lobby.guis.botm;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.utils.ItemGenerator;
import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class AddEntryGUI {

    @Getter
    public static Gui gui;
    private final Lobby lobby;

    LocalDate date = LocalDate.now();
    int month = date.getMonthValue();
    int year = date.getYear() - 2000;
    String name;
    String player1;
    String player2;
    String player3;

    public AddEntryGUI(Lobby lobby, Player player) {
        this.lobby = lobby;

        if(!EntryUtil.entries.containsKey(player)) {
            EntryUtil.addEntry(player, month, year);
        }else {
            month = EntryUtil.getEntry(player).getMonth();
            year = EntryUtil.getEntry(player).getYear();
        }

        Bukkit.getScheduler().runTask(this.lobby, () ->

            this.lobby.getLanguageAPI().getLanguage(player, language -> {

                this.gui = Gui.gui()
                        .title(this.lobby.getLanguageAPI().getMessage(language, "botm-gui.add_entry"))
                        .rows(3)
                        .disableAllInteractions()
                        .create();

                this.gui.setItem(1, 2, PaperItemBuilder.from(Material.CLOCK)
                        .amount(month)
                        .name(this.lobby.getLanguageAPI().getMessage(language, "botm-gui.date.month"))
                        .asGuiItem(event -> {
                            if (event.getClick() == ClickType.LEFT) {
                                month++;
                                if (month > 12) {
                                    month = 1;
                                    year++;
                                }
                            } else if (event.getClick() == ClickType.RIGHT) {
                                month--;
                                if (month < 1) {
                                    month = 12;
                                    year--;
                                }
                            }
                            EntryUtil.getEntry(player).setMonth(month);
                            EntryUtil.getEntry(player).setYear(year);
                            new AddEntryGUI(lobby, player);
                        }));
                this.gui.setItem(2, 2, PaperItemBuilder.from(Material.CLOCK)
                        .amount(year)
                        .name(this.lobby.getLanguageAPI().getMessage(language, "botm-gui.date.year"))
                                .asGuiItem(event -> {
                                    if (event.getClick() == ClickType.LEFT) {
                                        year++;
                                    } else if (event.getClick() == ClickType.RIGHT) {
                                        year--;
                                    }
                                    EntryUtil.getEntry(player).setYear(year);
                                    new AddEntryGUI(lobby, player);
                                }));

                this.gui.setItem(2, 4, PaperItemBuilder.from(Material.NAME_TAG)
                        .name(this.lobby.getLanguageAPI().getMessage(language, "botm-gui.name"))
                        .lore(Component.text("§7" + (EntryUtil.getEntry(player).getName() != null
                                ? EntryUtil.getEntry(player).getName()
                                : LegacyComponentSerializer.legacySection().serialize(
                                this.lobby.getLanguageAPI().getMessage(language, "botm-gui.name_not_set")
                        )
                        )))
                        .asGuiItem(event -> {
                            new AnvilGUI.Builder()
                                    .onClick((slot, snapshot) -> {
                                           if (slot == AnvilGUI.Slot.OUTPUT) {
                                               EntryUtil.getEntry(player).setName(snapshot.getText());
                                               new AddEntryGUI(lobby, player);
                                           }
                                            return AnvilGUI.Response.close();
                                    })
                                    .title(LegacyComponentSerializer.legacySection().serialize(this.lobby.getLanguageAPI().getMessage(language, "botm-gui.entry_name_title")))
                                    .text(LegacyComponentSerializer.legacySection().serialize(this.lobby.getLanguageAPI().getMessage(language, "botm-gui.entry_name")))
                                    .plugin(this.lobby)
                                    .open(player);
                        }));

                ItemStack player1_head = new ItemStack(Material.PLAYER_HEAD);
                OfflinePlayer offlinePlayer1 = null;

                if (EntryUtil.getEntry(player).getPlayer1() != null) {
                    offlinePlayer1 = Bukkit.getOfflinePlayer(EntryUtil.getEntry(player).getPlayer1());
                    SkullMeta player1_meta = (SkullMeta) player1_head.getItemMeta();
                    player1_meta.displayName(Component.text(EntryUtil.getEntry(player).getPlayer1()));
                    player1_meta.setOwningPlayer(offlinePlayer1);
                    player1_head.setItemMeta(player1_meta);
                }

                this.gui.setItem(2, 6, PaperItemBuilder.from(player1_head)
                        .name(this.lobby.getLanguageAPI().getMessage(language, "botm-gui.player1"))
                        .lore(Component.text("§7" + (EntryUtil.getEntry(player).getPlayer1() != null
                                ? EntryUtil.getEntry(player).getPlayer1()
                                : LegacyComponentSerializer.legacySection().serialize(
                                this.lobby.getLanguageAPI().getMessage(language, "botm-gui.player_not_set")
                        ))))
                        .asGuiItem(event -> {
                            new AnvilGUI.Builder()
                                    .onClick((slot, snapshot) -> {
                                        if (slot == AnvilGUI.Slot.OUTPUT) {
                                            EntryUtil.getEntry(player).setPlayer1(snapshot.getText());
                                            new AddEntryGUI(lobby, player);
                                        }
                                        return AnvilGUI.Response.close();
                                    })
                                    .title(LegacyComponentSerializer.legacySection().serialize(this.lobby.getLanguageAPI().getMessage(language, "botm-gui.entry_player1_title")))
                                    .text(LegacyComponentSerializer.legacySection().serialize(this.lobby.getLanguageAPI().getMessage(language, "botm-gui.entry_player1")))
                                    .plugin(this.lobby)
                                    .open(player);
                        }));
              
                ItemStack player2_head = new ItemStack(Material.PLAYER_HEAD);
                OfflinePlayer offlinePlayer2 = null;

                if (EntryUtil.getEntry(player).getPlayer2() != null) {
                    offlinePlayer2 = Bukkit.getOfflinePlayer(EntryUtil.getEntry(player).getPlayer2());
                    SkullMeta player2_meta = (SkullMeta) player2_head.getItemMeta();
                    player2_meta.displayName(Component.text(EntryUtil.getEntry(player).getPlayer2()));
                    player2_meta.setOwningPlayer(offlinePlayer2);
                    player2_head.setItemMeta(player2_meta);
                }

                this.gui.setItem(2, 7, PaperItemBuilder.from(player2_head)
                        .name(this.lobby.getLanguageAPI().getMessage(language, "botm-gui.player2"))
                        .lore(Component.text("§7" + (EntryUtil.getEntry(player).getPlayer2() != null
                                ? EntryUtil.getEntry(player).getPlayer2()
                                : LegacyComponentSerializer.legacySection().serialize(
                                this.lobby.getLanguageAPI().getMessage(language, "botm-gui.player_not_set")
                        )
                        )))
                        .asGuiItem(event -> {
                            new AnvilGUI.Builder()
                                    .onClick((slot, snapshot) -> {
                                        if (slot == AnvilGUI.Slot.OUTPUT) {
                                            EntryUtil.getEntry(player).setPlayer2(snapshot.getText());
                                            new AddEntryGUI(lobby, player);
                                        }
                                        return AnvilGUI.Response.close();
                                    })
                                    .title(LegacyComponentSerializer.legacySection().serialize(this.lobby.getLanguageAPI().getMessage(language, "botm-gui.entry_player2_title")))
                                    .text(LegacyComponentSerializer.legacySection().serialize(this.lobby.getLanguageAPI().getMessage(language, "botm-gui.entry_player2")))
                                    .plugin(this.lobby)
                                    .open(player);
                        }));

                ItemStack player3_head = new ItemStack(Material.PLAYER_HEAD);
                OfflinePlayer offlinePlayer3 = null;

                if (EntryUtil.getEntry(player).getPlayer3() != null) {
                    offlinePlayer3 = Bukkit.getOfflinePlayer(EntryUtil.getEntry(player).getPlayer3());
                    SkullMeta player3_meta = (SkullMeta) player3_head.getItemMeta();
                    player3_meta.displayName(Component.text(EntryUtil.getEntry(player).getPlayer3()));
                    player3_meta.setOwningPlayer(offlinePlayer3);
                    player3_head.setItemMeta(player3_meta);
                }

                this.gui.setItem(2, 8, PaperItemBuilder.from(player3_head)
                        .name(this.lobby.getLanguageAPI().getMessage(language, "botm-gui.player3"))
                        .lore(Component.text("§7" + (EntryUtil.getEntry(player).getPlayer3() != null
                                ? EntryUtil.getEntry(player).getPlayer3()
                                : LegacyComponentSerializer.legacySection().serialize(
                                this.lobby.getLanguageAPI().getMessage(language, "botm-gui.player_not_set")
                        )
                        )))
                        .asGuiItem(event -> {
                            new AnvilGUI.Builder()
                                    .onClick((slot, snapshot) -> {
                                        if (slot == AnvilGUI.Slot.OUTPUT) {
                                            EntryUtil.getEntry(player).setPlayer3(snapshot.getText());
                                            new AddEntryGUI(lobby, player);
                                        }
                                        return AnvilGUI.Response.close();
                                    })
                                    .title(LegacyComponentSerializer.legacySection().serialize(this.lobby.getLanguageAPI().getMessage(language, "botm-gui.entry_player3_title")))
                                    .text(LegacyComponentSerializer.legacySection().serialize(this.lobby.getLanguageAPI().getMessage(language, "botm-gui.entry_player3")))
                                    .plugin(this.lobby)
                                    .open(player);
                        }));

                this.gui.setItem(3, 9, PaperItemBuilder.from(ItemGenerator.customModel(Material.PAPER, "stars"))
                        .name(this.lobby.getLanguageAPI().getMessage(language, "botm-gui.confirm"))
                        .asGuiItem(event -> {

                            if(EntryUtil.getEntry(player).getName() == null ||
                               EntryUtil.getEntry(player).getPlayer1() == null) {
                                this.lobby.getLanguageAPI().sendMessageToPlayer(player, "botm.incomplete");
                                return;
                            }

                            event.getInventory().close();
                            try {
                                boolean success =lobby.getBotmScoreAPI().addEntry(
                                        EntryUtil.getEntry(player).getName(),
                                        EntryUtil.getEntry(player).getYear(),
                                        EntryUtil.getEntry(player).getMonth(),
                                        EntryUtil.getEntry(player).getPlayer1(),
                                        EntryUtil.getEntry(player).getPlayer2(),
                                        EntryUtil.getEntry(player).getPlayer3());

                                if (success) {
                            EntryUtil.entries.remove(player);
                            this.lobby.getLanguageAPI().sendMessageToPlayer(player, "botm.added");

                            try {
                                this.lobby.getBotmScoreAPI().reload(player);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            } catch (ExecutionException e) {
                                throw new RuntimeException(e);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }else {
                                // Entry already exists for this month and year
                                this.lobby.getLanguageAPI().sendMessageToPlayer(player, "botm.duplicate");
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }));

                this.gui.getFiller().fill(PaperItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());

                this.gui.open(player);
        }));
    }
}
