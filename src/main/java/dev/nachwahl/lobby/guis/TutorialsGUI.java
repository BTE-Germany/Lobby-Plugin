package dev.nachwahl.lobby.guis;

import dev.nachwahl.lobby.LobbyPlugin;
import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TutorialsGUI {

    private Gui gui;
    private final LobbyPlugin lobbyPlugin;

    public TutorialsGUI(LobbyPlugin lobbyPlugin, Player player) {
        this.lobbyPlugin = lobbyPlugin;
        this.lobbyPlugin.getLanguageAPI().getLanguage(player, language -> {
            this.gui = Gui.gui()
                    .title(this.lobbyPlugin.getLanguageAPI().getMessage(language, "tutorials.title"))
                    .rows(3)
                    .disableAllInteractions()
                    .create();


            this.gui.setItem(1, 1, PaperItemBuilder.from(Material.GRASS_BLOCK)
                    .name(this.lobbyPlugin.getMiniMessage().deserialize("<gold>Terraform Tutorial</gold>"))
                    .asGuiItem(event -> {
                        event.getInventory().close();
                        Component component = this.lobbyPlugin.getLanguageAPI().getMessage(language, "map.tpll.message");
                        player.sendMessage(component.clickEvent(ClickEvent.openUrl("https://www.youtube.com/watch?v=no8a_79kd1k")));
                    }));

            this.gui.setItem(1, 2, PaperItemBuilder.from(Material.WOODEN_AXE)
                    .name(this.lobbyPlugin.getMiniMessage().deserialize("<gold>Worldedit Tutorial</gold>"))
                    .asGuiItem(event -> {
                        event.getInventory().close();
                        Component component = this.lobbyPlugin.getLanguageAPI().getMessage(language, "map.tpll.message");
                        player.sendMessage(component.clickEvent(ClickEvent.openUrl("https://www.youtube.com/watch?v=6ReoE4dGi4E")));
                    }));


            this.gui.getFiller().fill(PaperItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());
            Bukkit.getScheduler().runTask(this.lobbyPlugin, () -> this.gui.open(player));

        });
    }

    public Gui getGui() {
        return gui;
    }


}
