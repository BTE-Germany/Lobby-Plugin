package dev.nachwahl.lobby.guis;

import dev.nachwahl.lobby.LobbyPlugin;
import dev.nachwahl.lobby.utils.GuiUtil;
import dev.nachwahl.lobby.utils.ItemGenerator;
import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TutorialGUI {

    private static final int FIRST_ROW_PLOTS_APPLY = 2;
    private static final int FIRST_COL_PLOTS = 2;
    private static final int FIRST_COL_APPLY = 7;

    @Getter
    private Gui gui;
    private final LobbyPlugin lobbyPlugin;

    public TutorialGUI(LobbyPlugin lobbyPlugin, Player player) {
        this.lobbyPlugin = lobbyPlugin;
        this.lobbyPlugin.getLanguageAPI().getLanguage(player, language -> {
            this.gui = Gui.gui()
                    .title(GuiUtil.getCustomDataTitle(this.lobbyPlugin.getLanguageAPI().getMessage(language, "help.title"), "ऒ"))
                    .rows(4)
                    .disableAllInteractions()
                    .create();



            this.gui.setItem(1, 1, PaperItemBuilder.from(ItemGenerator.customModelEmpty())
                    .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "help.guides.name"))
                    .asGuiItem(event -> {
                        event.getInventory().close();
                        player.sendMessage(this.lobbyPlugin.getLanguageAPI().getMessage(language, "help.guides.message")
                                .clickEvent(net.kyori.adventure.text.event.ClickEvent.openUrl("https://discord.com/channels/692825222373703772/781642142174019594")));
                    }));


            this.gui.setItem(1, 9, PaperItemBuilder.from(ItemGenerator.customModelEmpty())
                    .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "gui.close.label"))
                    .asGuiItem(event -> event.getInventory().close()));


            GuiItem itemPlots = PaperItemBuilder.from(ItemGenerator.customModelEmpty())
                    .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "help.plots.name"))
                    .asGuiItem(event -> this.lobbyPlugin.getBungeeConnector().sendToServer(player, this.lobbyPlugin.getConfig().getString("server.Plot"), true));
            this.gui.setItem(FIRST_ROW_PLOTS_APPLY, FIRST_COL_PLOTS, itemPlots);
            this.gui.setItem(FIRST_ROW_PLOTS_APPLY, FIRST_COL_PLOTS + 1, itemPlots);
            this.gui.setItem(FIRST_ROW_PLOTS_APPLY + 1, FIRST_COL_PLOTS, itemPlots);
            this.gui.setItem(FIRST_ROW_PLOTS_APPLY + 1, FIRST_COL_PLOTS + 1, itemPlots);


            GuiItem itemApply = PaperItemBuilder.from(ItemGenerator.customModelEmpty())
                    .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "help.apply.name"))
                    .asGuiItem(event -> {
                        event.getInventory().close();
                        player.sendMessage(this.lobbyPlugin.getLanguageAPI().getMessage(language, "help.apply.message")
                                .clickEvent(net.kyori.adventure.text.event.ClickEvent.openUrl("https://buildtheearth.net/teams/de/apply")));
                    });
            this.gui.setItem(FIRST_ROW_PLOTS_APPLY, FIRST_COL_APPLY, itemApply);
            this.gui.setItem(FIRST_ROW_PLOTS_APPLY, FIRST_COL_APPLY + 1, itemApply);
            this.gui.setItem(FIRST_ROW_PLOTS_APPLY + 1, FIRST_COL_APPLY, itemApply);
            this.gui.setItem(FIRST_ROW_PLOTS_APPLY + 1, FIRST_COL_APPLY + 1, itemApply);


            Bukkit.getScheduler().runTask(this.lobbyPlugin, () -> this.gui.open(player));
        });
    }
}
