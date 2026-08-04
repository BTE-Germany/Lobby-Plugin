package dev.nachwahl.lobby.guis.botm;

import co.aikar.idb.DbRow;
import dev.nachwahl.lobby.LobbyPlugin;
import dev.nachwahl.lobby.language.Language;
import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class BOTMList {

    @Getter
    public static Gui gui;
    private final LobbyPlugin lobbyPlugin;

    public BOTMList(LobbyPlugin lobbyPlugin, Player player, int page) throws SQLException {
        this.lobbyPlugin = lobbyPlugin;

        List<DbRow> botmEntries = lobbyPlugin.getDatabase().getResults("SELECT * FROM botm ORDER BY year DESC, month DESC");
        int currentPage = page;
        int maxPage = (int) Math.ceil((double) botmEntries.size() / 6);

        this.lobbyPlugin.getLanguageAPI().getLanguage(player, language -> {

            this.gui = Gui.gui()
                    .title(this.lobbyPlugin.getLanguageAPI().getMessage(language, "botm.list.title"))
                    .rows(6)
                    .disableAllInteractions()
                    .create();

            for (int i = 0; i < 6; i++) {
                int index = (currentPage - 1) * 6 + i;
                if (index >= botmEntries.size()) break;

                DbRow entry = botmEntries.get(index);
                int year = entry.getInt("year");
                int month = entry.getInt("month");
                String name = entry.getString("name");
                String player1_name = null;
                String player2_name = null;
                String player3_name = null;
                try {
                    player1_name = this.lobbyPlugin.getBotmScoreAPI().getPlayerName(UUID.fromString(entry.getString("player1_uuid"))).get();
                    player2_name = entry.getString("player2_uuid") != null ? this.lobbyPlugin.getBotmScoreAPI().getPlayerName(UUID.fromString(entry.getString("player2_uuid"))).get() : null;
                    player3_name = entry.getString("player3_uuid") != null ? this.lobbyPlugin.getBotmScoreAPI().getPlayerName(UUID.fromString(entry.getString("player3_uuid"))).get() : null;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }

                Component year_component = this.lobbyPlugin.getLanguageAPI().getMessage(language, "botm.list.item.year");
                String year_text = PlainTextComponentSerializer.plainText().serialize(year_component) + ": 20" + year;

                this.gui.setItem(i + 1, 2, PaperItemBuilder.from(Material.CLOCK)
                        .amount(year)
                        .name(Component.text(year_text))
                        .asGuiItem()
                );

                Component month_component_1 = this.lobbyPlugin.getLanguageAPI().getMessage(language, "botm.list.item.month");
                Component month_component_2 = this.lobbyPlugin.getLanguageAPI().getMessage(language, "month." + month);
                String month_text = PlainTextComponentSerializer.plainText().serialize(month_component_1) + ": " + PlainTextComponentSerializer.plainText().serialize(month_component_2);


                this.gui.setItem(i + 1, 3, PaperItemBuilder.from(Material.CLOCK)
                        .amount(month)
                        .name(Component.text(month_text))
                        .asGuiItem()
                );

                this.gui.setItem(i + 1, 4, PaperItemBuilder.from(Material.NAME_TAG)
                        .name(Component.text(name))
                        .asGuiItem()
                );

                OfflinePlayer offlinePlayer1 = Bukkit.getOfflinePlayer(player1_name);

                ItemStack player1_head = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta player1_meta = (SkullMeta) player1_head.getItemMeta();
                player1_meta.displayName(Component.text(player1_name));
                player1_meta.setOwningPlayer(offlinePlayer1);
                player1_head.setItemMeta(player1_meta);

                this.gui.setItem(i + 1, 5, PaperItemBuilder.from(player1_head)
                        .name(Component.text(player1_name))
                        .lore(this.getOfflineErrorOrNoComponent(offlinePlayer1, language))
                        .asGuiItem()
                );

                if (player2_name != null) {

                    OfflinePlayer offlinePlayer2 = Bukkit.getOfflinePlayer(player2_name);

                    ItemStack player2_head = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta player2_meta = (SkullMeta) player2_head.getItemMeta();
                    player2_meta.displayName(Component.text(player2_name));
                    player2_meta.setOwningPlayer(offlinePlayer2);
                    player2_head.setItemMeta(player2_meta);

                    this.gui.setItem(i + 1, 6, PaperItemBuilder.from(player2_head)
                            .name(Component.text(player2_name))
                            .lore(this.getOfflineErrorOrNoComponent(offlinePlayer2, language))
                            .asGuiItem()
                    );
                } else {
                    this.gui.setItem(i + 1, 6, PaperItemBuilder.from(Material.STRUCTURE_VOID)
                            .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "botm.list.item.noplayer"))
                            .asGuiItem()
                    );
                }

                if (player3_name != null) {

                    OfflinePlayer offlinePlayer3 = Bukkit.getOfflinePlayer(player3_name);

                    ItemStack player3_head = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta player3_meta = (SkullMeta) player3_head.getItemMeta();
                    player3_meta.displayName(Component.text(player3_name));
                    player3_meta.setOwningPlayer(offlinePlayer3);
                    player3_head.setItemMeta(player3_meta);

                    this.gui.setItem(i + 1, 7, PaperItemBuilder.from(player3_head)
                            .name(Component.text(player3_name))
                            .lore(this.getOfflineErrorOrNoComponent(offlinePlayer3, language))
                            .asGuiItem()
                    );
                } else {
                    this.gui.setItem(i + 1, 7, PaperItemBuilder.from(Material.STRUCTURE_VOID)
                            .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "botm.list.item.noplayer"))
                            .asGuiItem()
                    );
                }

                this.gui.setItem(i + 1, 8, PaperItemBuilder.from(Material.BARRIER)
                        .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "botm.list.item.delete"))
                        .asGuiItem(event -> {
                            new BOTMConfirm(this.lobbyPlugin, player, currentPage, year, month);
                        })
                );

            }

            if (currentPage > 1) {
                this.gui.setItem(1, 1, PaperItemBuilder.from(Material.ARROW)
                        .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "botm.list.item.previous"))
                        .asGuiItem(event -> {

                            try {
                                new BOTMList(this.lobbyPlugin, player, currentPage - 1);
                            } catch (SQLException e) {
                                this.lobbyPlugin.getLanguageAPI().sendMessageToPlayer(player, "botm.list.error");
                            }

                        })
                );
            } else {
                this.gui.setItem(1, 1, PaperItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                        .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "botm.list.item.unavailable"))
                        .asGuiItem()
                );
            }

            if (currentPage < maxPage) {
                this.gui.setItem(1, 9, PaperItemBuilder.from(Material.ARROW)
                        .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "botm.list.item.next"))
                        .asGuiItem(event -> {

                            try {
                                new BOTMList(this.lobbyPlugin, player, currentPage + 1);
                            } catch (SQLException e) {
                                this.lobbyPlugin.getLanguageAPI().sendMessageToPlayer(player, "botm.list.error");
                            }

                        })
                );
            } else {
                this.gui.setItem(1, 9, PaperItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                        .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "botm.list.item.unavailable"))
                        .asGuiItem()
                );
            }

            this.gui.getFiller().fill(PaperItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());

            this.gui.open(player);

        });

    }

    private Component[] getOfflineErrorOrNoComponent(OfflinePlayer offlinePlayer, Language language) {
        if (offlinePlayer.getLastSeen() == 0) {
            return new Component[] {
                    this.lobbyPlugin.getLanguageAPI().getMessage(language, "botm.list.item.offline_player_error")
            };
        }
        return new Component[0];
    }
}
