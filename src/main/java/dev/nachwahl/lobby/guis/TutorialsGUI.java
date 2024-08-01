package dev.nachwahl.lobby.guis;

import dev.nachwahl.lobby.Lobby;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TutorialsGUI {

    private Gui gui;
    private final Lobby lobby;

    public TutorialsGUI(Lobby lobby, Player player) {
        this.lobby = lobby;
        this.lobby.getLanguageAPI().getLanguage(player, language -> {
            this.gui = Gui.gui()
                    .title(this.lobby.getLanguageAPI().getMessage(language, "tutorials.title"))
                    .rows(3)
                    .disableAllInteractions()
                    .create();


            this.gui.setItem(1, 1, ItemBuilder.from(Material.GRASS_BLOCK)
                    .name(this.lobby.getMiniMessage().deserialize("<gold>Terraform Tutorial</gold>"))
                    .asGuiItem(event -> {
                        event.getInventory().close();
                        Component component = this.lobby.getLanguageAPI().getMessage(language, "map.tpll.message");
                        player.sendMessage(component.clickEvent(ClickEvent.openUrl("https://www.youtube.com/watch?v=no8a_79kd1k")));
                    }));

            this.gui.setItem(1, 2, ItemBuilder.from(Material.WOODEN_AXE)
                    .name(this.lobby.getMiniMessage().deserialize("<gold>Worldedit Tutorial</gold>"))
                    .asGuiItem(event -> {
                        event.getInventory().close();
                        Component component = this.lobby.getLanguageAPI().getMessage(language, "map.tpll.message");
                        player.sendMessage(component.clickEvent(ClickEvent.openUrl("https://www.youtube.com/watch?v=6ReoE4dGi4E")));
                    }));


            this.gui.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());
            Bukkit.getScheduler().runTask(this.lobby, () -> this.gui.open(player));

        });
    }

    public Gui getGui() {
        return gui;
    }


}
