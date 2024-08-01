package dev.nachwahl.lobby.guis;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.utils.ItemGenerator;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class NavigatorGUI {

    private Gui gui;
    private final Lobby lobby;

    public NavigatorGUI(Lobby lobby, Player player) {
        this.lobby = lobby;
        this.lobby.getLanguageAPI().getLanguage(player, language -> {
            this.gui = Gui.gui()
                    .title(this.lobby.getLanguageAPI().getMessage(language, "navigator.title"))
                    .rows(3)
                    .disableAllInteractions()
                    .create();


            this.gui.setItem(2, 3, ItemBuilder.from(ItemGenerator.customModel(Material.PAPER, 2))
                    .name(this.lobby.getLanguageAPI().getMessage(language, "navigator.plots.name"))
                    .asGuiItem(event -> {
                        this.lobby.getBungeeConnector().sendToServer(player, this.lobby.getConfig().getString("server.Plot"), true);
                    }));


            this.gui.setItem(2, 5, ItemBuilder.from(Material.COMPASS)
                    .name(this.lobby.getLanguageAPI().getMessage(language, "navigator.warps.name"))
                    .asGuiItem(event -> {
                        player.performCommand("nwarp");
                        event.getInventory().close();
                    }));

            this.gui.setItem(2, 7, ItemBuilder.from(ItemGenerator.customModel(Material.PAPER, 5))
                    .name(this.lobby.getLanguageAPI().getMessage(language, "navigator.terra.name"))
                    .asGuiItem(event -> {
                        event.getInventory().close();
                        new ServerGUI(lobby, player);
                    }));

            this.gui.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());
            Bukkit.getScheduler().runTask(this.lobby, () -> this.gui.open(player));

        });
    }

    public Gui getGui() {
        return gui;
    }


}
