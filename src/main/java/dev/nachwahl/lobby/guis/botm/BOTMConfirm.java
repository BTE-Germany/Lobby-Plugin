package dev.nachwahl.lobby.guis.botm;

import dev.nachwahl.lobby.LobbyPlugin;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class BOTMConfirm {

    @Getter
    private Gui gui;
    private final LobbyPlugin lobbyPlugin;


    public BOTMConfirm(LobbyPlugin lobbyPlugin, Player player, int currentPage, int year, int month) {
        this.lobbyPlugin = lobbyPlugin;

        this.lobbyPlugin.getLanguageAPI().getLanguage(player, language -> {

            this.gui = Gui.gui()

                    .title(lobbyPlugin.getLanguageAPI().getMessage(language, "botm.confirm.confirm_delete"))
                    .rows(3)
                    .disableAllInteractions()
                    .create();

            this.gui.setItem(2, 3, ItemBuilder.from(Material.LIME_WOOL)
                    .name(lobbyPlugin.getLanguageAPI().getMessage(language, "botm.confirm.yes"))
                    .asGuiItem(event -> {
                        try {
                            this.lobbyPlugin.getDatabase().executeUpdate("DELETE FROM botm WHERE year = ? AND month = ?", year, month);
                            this.lobbyPlugin.getBotmScoreAPI().reload(player);
                            new BOTMList(this.lobbyPlugin, player, currentPage);
                        } catch (SQLException e) {
                            this.lobbyPlugin.getLanguageAPI().sendMessageToPlayer(player, "botm.list.error");
                            throw new RuntimeException(e);
                        } catch (ExecutionException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }));

            this.gui.setItem(2, 7, ItemBuilder.from(Material.RED_WOOL)
                    .name(lobbyPlugin.getLanguageAPI().getMessage(language, "botm.confirm.no"))
                    .asGuiItem(event -> {
                        try {
                            new BOTMList(this.lobbyPlugin, player, currentPage);
                        } catch (SQLException e) {
                            this.lobbyPlugin.getLanguageAPI().sendMessageToPlayer(player, "botm.list.error");
                            throw new RuntimeException(e);
                        }
                    }));

            this.gui.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());

            gui.open(player);

        });


    }
}
