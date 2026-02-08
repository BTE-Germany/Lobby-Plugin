package dev.nachwahl.lobby.guis;

import dev.nachwahl.lobby.LobbyPlugin;
import dev.nachwahl.lobby.language.Language;
import dev.nachwahl.lobby.utils.ItemGenerator;
import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class VisitGUI {

    private Gui gui;
    private final LobbyPlugin lobbyPlugin;

    public VisitGUI(LobbyPlugin lobbyPlugin, Player player) {
        this.lobbyPlugin = lobbyPlugin;
        this.lobbyPlugin.getLanguageAPI().getLanguage(player, language -> {
            this.gui = Gui.gui()
                    .title(this.lobbyPlugin.getLanguageAPI().getMessage(language, "map.title"))
                    .rows(3)
                    .disableAllInteractions()
                    .create();

            this.gui.setItem(2, 4, PaperItemBuilder.from(ItemGenerator.customModel(Material.PAPER, "map"))
                    .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "map.tpll.name"))
                    .asGuiItem(event -> {
                        event.getInventory().close();
                        Component component = this.lobbyPlugin.getLanguageAPI().getMessage(language, "map.tpll.message");
                        if (language == Language.ENGLISH) {
                            player.sendMessage(component.clickEvent(ClickEvent.openUrl("https://youtu.be/ukQ4ATKlhWU")));
                        } else {
                            player.sendMessage(component.clickEvent(ClickEvent.openUrl("https://youtu.be/T2NMEBdUAvs")));
                        }
                    }));

            this.gui.setItem(2, 6, PaperItemBuilder.from(Material.COMPASS)
                    .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "navigator.warps.name"))
                    .asGuiItem(event -> {
                        player.performCommand("nwarp");
                        event.getInventory().close();
                    }));

            this.gui.getFiller().fill(PaperItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());
            Bukkit.getScheduler().runTask(this.lobbyPlugin, () -> this.gui.open(player));

        });
    }

    public Gui getGui() {
        return gui;
    }


}
