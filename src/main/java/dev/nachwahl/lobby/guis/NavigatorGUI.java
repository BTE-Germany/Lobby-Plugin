package dev.nachwahl.lobby.guis;

import dev.nachwahl.lobby.LobbyPlugin;
import dev.nachwahl.lobby.utils.ItemGenerator;
import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class NavigatorGUI {

    private Gui gui;
    private final LobbyPlugin lobbyPlugin;

    public NavigatorGUI(LobbyPlugin lobbyPlugin, Player player) {
        this.lobbyPlugin = lobbyPlugin;
        this.lobbyPlugin.getLanguageAPI().getLanguage(player, language -> {
            this.gui = Gui.gui()
                    .title(this.lobbyPlugin.getLanguageAPI().getMessage(language, "navigator.title"))
                    .rows(3)
                    .disableAllInteractions()
                    .create();


            this.gui.setItem(2, 3, PaperItemBuilder.from(ItemGenerator.customModel(Material.PAPER, "plot"))
                    .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "navigator.plots.name"))
                    .asGuiItem(event -> {
                        this.lobbyPlugin.getBungeeConnector().sendToServer(player, this.lobbyPlugin.getConfig().getString("server.Plot"), true);
                    }));


            this.gui.setItem(2, 5, PaperItemBuilder.from(Material.COMPASS)
                    .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "navigator.warps.name"))
                    .asGuiItem(event -> {
                        player.performCommand("nwarp");
                        event.getInventory().close();
                    }));

            this.gui.setItem(2, 7, PaperItemBuilder.from(ItemGenerator.customModel(Material.PAPER, "map"))
                    .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "navigator.terra.name"))
                    .asGuiItem(event -> {
                        new ServerGUI(lobbyPlugin, player, false).getGui().getGuiItems().forEach((slot, item) -> {
                            this.gui.setItem(slot, item);
                        });
                        this.gui.update();
                    }));

            this.gui.getFiller().fill(PaperItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());
            Bukkit.getScheduler().runTask(this.lobbyPlugin, () -> this.gui.open(player));

        });
    }

    public Gui getGui() {
        return gui;
    }


}
