package dev.nachwahl.lobby.guis;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.utils.ItemGenerator;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ServerGUI {

    private Gui gui;
    private final Lobby lobby;

    public ServerGUI(Lobby lobby, Player player) {
        this.lobby = lobby;
        this.lobby.getLanguageAPI().getLanguage(player, language -> {
            this.gui = Gui.gui()
                    .title(this.lobby.getLanguageAPI().getMessage(language, "server.title"))
                    .rows(3)
                    .disableAllInteractions()
                    .create();

            this.gui.setItem(2, 3, ItemBuilder.from(ItemGenerator.customModel(Material.PAPER,16))
                    .name(this.lobby.getLanguageAPI().getMessage(language, "server.name", Placeholder.parsed("server","1")))
                    .asGuiItem(event -> {
                        lobby.getBungeeConnector().sendToServer(player,"Terra-1",true);
                    }));

            this.gui.setItem(2, 5, ItemBuilder.from(ItemGenerator.customModel(Material.PAPER,17))
                    .name(this.lobby.getLanguageAPI().getMessage(language, "server.name", Placeholder.parsed("server","2")))
                    .asGuiItem(event -> {
                        lobby.getBungeeConnector().sendToServer(player,"Terra-2",true);
                    }));

            this.gui.setItem(2, 7, ItemBuilder.from(ItemGenerator.customModel(Material.PAPER,18))
                    .name(this.lobby.getLanguageAPI().getMessage(language, "server.name", Placeholder.parsed("server","3")))
                    .asGuiItem(event -> {
                        lobby.getBungeeConnector().sendToServer(player,"Terra-3",true);
                    }));

            this.gui.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());
            Bukkit.getScheduler().runTask(this.lobby, () -> this.gui.open(player));

        });
    }

    public Gui getGui() {
        return gui;
    }


}
