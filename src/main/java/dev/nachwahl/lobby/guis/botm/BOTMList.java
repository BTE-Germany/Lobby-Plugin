package dev.nachwahl.lobby.guis.botm;

import co.aikar.idb.DbRow;
import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.utils.BOTMScoreAPI;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class BOTMList {

    @Getter
    public static Gui gui;
    private final Lobby lobby;

    public BOTMList(Lobby lobby, Player player, int page) throws SQLException {
        this.lobby = lobby;

        List<DbRow> botmEntries = lobby.getDatabase().getResults("SELECT * FROM botm ORDER BY year DESC, month DESC");
        int currentPage = page;
        int maxPage = (int) Math.ceil((double) botmEntries.size() / 6);

        this.lobby.getLanguageAPI().getLanguage(player, language -> {

            this.gui = Gui.gui()
                    .title(this.lobby.getLanguageAPI().getMessage(language, "botm.list.title"))
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
                    player1_name = this.lobby.getBotmScoreAPI().getPlayerName(UUID.fromString(entry.getString("player1_uuid"))).get();
                    player2_name = entry.getString("player2_uuid") != null ? this.lobby.getBotmScoreAPI().getPlayerName(UUID.fromString(entry.getString("player2_uuid"))).get() : null;
                    player3_name = entry.getString("player3_uuid") != null ? this.lobby.getBotmScoreAPI().getPlayerName(UUID.fromString(entry.getString("player3_uuid"))).get() : null;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }

                Component year_component = this.lobby.getLanguageAPI().getMessage(language, "botm.list.item.year");
                String year_text = PlainTextComponentSerializer.plainText().serialize(year_component) + ": 20" + year;

                this.gui.setItem(i + 1, 2, ItemBuilder.from(Material.CLOCK)
                        .amount(year)
                        .name(Component.text(year_text))
                        .asGuiItem()
                );

                Component month_component_1 = this.lobby.getLanguageAPI().getMessage(language, "botm.list.item.month");
                Component month_component_2 = this.lobby.getLanguageAPI().getMessage(language, "month." + month);
                String month_text = PlainTextComponentSerializer.plainText().serialize(month_component_1) + ": " + PlainTextComponentSerializer.plainText().serialize(month_component_2);


                this.gui.setItem(i + 1, 3, ItemBuilder.from(Material.CLOCK)
                        .amount(month)
                        .name(Component.text(month_text))
                        .asGuiItem()
                );

                this.gui.setItem(i + 1, 4, ItemBuilder.from(Material.NAME_TAG)
                        .name(Component.text(name))
                        .asGuiItem()
                );

                this.gui.setItem(i + 1, 5, ItemBuilder.from(Material.PLAYER_HEAD)
                        .name(Component.text(player1_name))
                        .asGuiItem()
                );

                if (player2_name != null) {
                    this.gui.setItem(i + 1, 6, ItemBuilder.from(Material.PLAYER_HEAD)
                            .name(Component.text(player2_name))
                            .asGuiItem()
                    );
                }else{
                    this.gui.setItem(i + 1, 6, ItemBuilder.from(Material.STRUCTURE_VOID)
                            .name(this.lobby.getLanguageAPI().getMessage(language, "botm.list.item.noplayer"))
                            .asGuiItem()
                    );
                }

                if (player3_name != null) {
                    this.gui.setItem(i + 1, 7, ItemBuilder.from(Material.PLAYER_HEAD)
                            .name(Component.text(player3_name))
                            .asGuiItem()
                    );
                }else{
                    this.gui.setItem(i + 1, 7, ItemBuilder.from(Material.STRUCTURE_VOID)
                            .name(this.lobby.getLanguageAPI().getMessage(language, "botm.list.item.noplayer"))
                            .asGuiItem()
                    );
                }

                this.gui.setItem(i + 1, 8, ItemBuilder.from(Material.BARRIER)
                        .name(this.lobby.getLanguageAPI().getMessage(language, "botm.list.item.delete"))
                        .asGuiItem(event -> {
                            new BOTMConfirm(this.lobby, player, currentPage, year, month);
                        })
                );

            }

            if (currentPage > 1) {
                this.gui.setItem(1, 1, ItemBuilder.from(Material.ARROW)
                        .name(this.lobby.getLanguageAPI().getMessage(language, "botm.list.item.previous"))
                        .asGuiItem(event -> {

                            try {
                                new BOTMList(this.lobby, player, currentPage - 1);
                            }catch (SQLException e){
                                this.lobby.getLanguageAPI().sendMessageToPlayer(player, "botm.list.error");
                            }

                        })
                );
            }else {
                this.gui.setItem(1, 1, ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                        .name(this.lobby.getLanguageAPI().getMessage(language, "botm.list.item.unavailable"))
                        .asGuiItem()
                );
            }

            if (currentPage < maxPage) {
                this.gui.setItem(1, 9, ItemBuilder.from(Material.ARROW)
                        .name(this.lobby.getLanguageAPI().getMessage(language, "botm.list.item.next"))
                        .asGuiItem(event -> {

                            try {
                                new BOTMList(this.lobby, player, currentPage + 1);
                            }catch (SQLException e){
                                this.lobby.getLanguageAPI().sendMessageToPlayer(player, "botm.list.error");
                            }

                        })
                );
            }else {
                this.gui.setItem(1, 9, ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                        .name(this.lobby.getLanguageAPI().getMessage(language, "botm.list.item.unavailable"))
                        .asGuiItem()
                );
            }

            this.gui.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());

            this.gui.open(player);

        });

    }
}
