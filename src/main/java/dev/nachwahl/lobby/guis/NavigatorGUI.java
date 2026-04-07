package dev.nachwahl.lobby.guis;

import dev.nachwahl.lobby.LobbyPlugin;
import dev.nachwahl.lobby.utils.GuiUtil;
import dev.nachwahl.lobby.utils.ItemGenerator;
import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class NavigatorGUI {

    private static final int FIRST_ROW_PLOTS_WARPS = 2;
    private static final int FIRST_COL_PLOTS = 2;
    private static final int FIRST_COL_WARPS = 7;
    private static final int ROW_SERVERS = 4;
    private static final int FIRST_COL_SERVERS = 4;

    @Getter
    private Gui gui;
    private final LobbyPlugin lobbyPlugin;

    public NavigatorGUI(LobbyPlugin lobbyPlugin, Player player) {
        this.lobbyPlugin = lobbyPlugin;
        this.lobbyPlugin.getLanguageAPI().getLanguage(player, language -> {
            this.gui = Gui.gui()
                    .title(GuiUtil.getCustomDataTitle(this.lobbyPlugin.getLanguageAPI().getMessage(language, "navigator.title"), "ग"))
                    .rows(4)
                    .disableAllInteractions()
                    .create();

            // hide [i] button
            this.gui.setItem(1, 1, PaperItemBuilder.from(ItemGenerator.customModel(Material.PAPER, "blank"))
                    .name(Component.empty())
                    .asGuiItem());



            this.gui.setItem(1, 9, PaperItemBuilder.from(ItemGenerator.customModelEmpty())
                    .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "gui.close.label"))
                    .asGuiItem(event -> event.getInventory().close()));


            GuiItem itemPlots = PaperItemBuilder.from(ItemGenerator.customModelEmpty())
                    .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "navigator.plots.name"))
                    .asGuiItem(event -> {
                        this.lobbyPlugin.getBungeeConnector().sendToServer(player, this.lobbyPlugin.getConfig().getString("server.Plot"), true);
                    });
            this.gui.setItem(FIRST_ROW_PLOTS_WARPS, FIRST_COL_PLOTS, itemPlots);
            this.gui.setItem(FIRST_ROW_PLOTS_WARPS, FIRST_COL_PLOTS + 1, itemPlots);
            this.gui.setItem(FIRST_ROW_PLOTS_WARPS + 1, FIRST_COL_PLOTS, itemPlots);
            this.gui.setItem(FIRST_ROW_PLOTS_WARPS + 1, FIRST_COL_PLOTS + 1, itemPlots);


            GuiItem itemWarps = PaperItemBuilder.from(ItemGenerator.customModelEmpty())
                    .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "navigator.warps.name"))
                    .asGuiItem(event -> {
                        player.performCommand("nwarp");
                        event.getInventory().close();
                    });
            this.gui.setItem(FIRST_ROW_PLOTS_WARPS, FIRST_COL_WARPS, itemWarps);
            this.gui.setItem(FIRST_ROW_PLOTS_WARPS, FIRST_COL_WARPS + 1, itemWarps);
            this.gui.setItem(FIRST_ROW_PLOTS_WARPS + 1, FIRST_COL_WARPS, itemWarps);
            this.gui.setItem(FIRST_ROW_PLOTS_WARPS + 1, FIRST_COL_WARPS + 1, itemWarps);


            this.gui.setItem(ROW_SERVERS, FIRST_COL_SERVERS, PaperItemBuilder.from(ItemGenerator.customModelEmpty())
                    .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "server.name", Placeholder.parsed("server", "1")))
                    .asGuiItem(event -> {
                        lobbyPlugin.getBungeeConnector().sendToServer(player, "Terra-1", true);
                    }));

            this.gui.setItem(ROW_SERVERS, FIRST_COL_SERVERS + 1, PaperItemBuilder.from(ItemGenerator.customModelEmpty())
                    .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "server.name", Placeholder.parsed("server", "2")))
                    .asGuiItem(event -> {
                        lobbyPlugin.getBungeeConnector().sendToServer(player, "Terra-2", true);
                    }));

            this.gui.setItem(ROW_SERVERS, FIRST_COL_SERVERS + 2, PaperItemBuilder.from(ItemGenerator.customModelEmpty())
                    .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "server.name", Placeholder.parsed("server", "3")))
                    .asGuiItem(event -> {
                        lobbyPlugin.getBungeeConnector().sendToServer(player, "Terra-3", true);
                    }));


            Bukkit.getScheduler().runTask(this.lobbyPlugin, () -> this.gui.open(player));
        });
    }

}
