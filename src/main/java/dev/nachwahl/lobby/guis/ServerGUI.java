package dev.nachwahl.lobby.guis;

import dev.nachwahl.lobby.LobbyPlugin;
import dev.nachwahl.lobby.utils.ItemGenerator;
import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ServerGUI {

    private Gui gui;
    private final LobbyPlugin lobbyPlugin;

    public ServerGUI(LobbyPlugin lobbyPlugin, Player player) {
        this(lobbyPlugin, player, true);
    }

    public ServerGUI(LobbyPlugin lobbyPlugin, Player player, boolean open) {
        this.lobbyPlugin = lobbyPlugin;
        this.lobbyPlugin.getLanguageAPI().getLanguage(player, language -> {
            this.gui = Gui.gui()
                    .title(this.lobbyPlugin.getLanguageAPI().getMessage(language, "server.title"))
                    .rows(3)
                    .disableAllInteractions()
                    .create();

            this.gui.setItem(2, 3, PaperItemBuilder.from(ItemGenerator.customModel(Material.PAPER, "map_1"))
                    .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "server.name", Placeholder.parsed("server", "1")))
                    .asGuiItem(event -> {
                        lobbyPlugin.getBungeeConnector().sendToServer(player, "Terra-1", true);
                    }));

            this.gui.setItem(2, 5, PaperItemBuilder.from(ItemGenerator.customModel(Material.PAPER, "map_2"))
                    .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "server.name", Placeholder.parsed("server", "2")))
                    .asGuiItem(event -> {
                        lobbyPlugin.getBungeeConnector().sendToServer(player, "Terra-2", true);
                    }));

            this.gui.setItem(2, 7, PaperItemBuilder.from(ItemGenerator.customModel(Material.PAPER, "map_3"))
                    .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "server.name", Placeholder.parsed("server", "3")))
                    .asGuiItem(event -> {
                        lobbyPlugin.getBungeeConnector().sendToServer(player, "Terra-3", true);
                    }));

            this.gui.getFiller().fill(PaperItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());

            if (open) {
                Bukkit.getScheduler().runTask(this.lobbyPlugin, () -> this.gui.open(player));
            }

        });
    }

    public Gui getGui() {
        return gui;
    }


}
