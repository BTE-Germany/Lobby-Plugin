package dev.nachwahl.lobby.guis;

import dev.nachwahl.lobby.Lobby;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TutorialGUI {

    private Gui gui;
    private final Lobby lobby;

    public TutorialGUI(Lobby lobby, Player player) {
        this.lobby = lobby;
        this.lobby.getLanguageAPI().getLanguage(player, language -> {
            this.gui = Gui.gui()
                    .title(this.lobby.getLanguageAPI().getMessage(language, "help.title"))
                    .rows(3)
                    .disableAllInteractions()
                    .create();


            this.gui.setItem(2, 3, ItemBuilder.from(Material.GRASS_BLOCK)
                    .name(this.lobby.getLanguageAPI().getMessage(language, "help.plots.name"))
                    .asGuiItem(event -> {
                       // TODO
                    }));


            this.gui.setItem(2, 5, ItemBuilder.from(Material.WOODEN_PICKAXE)
                    .name(this.lobby.getLanguageAPI().getMessage(language, "help.test.name"))
                    .asGuiItem(event -> {
                        // TODO
                    }));

            this.gui.setItem(2, 7, ItemBuilder.from(Material.DIAMOND_PICKAXE)
                    .name(this.lobby.getLanguageAPI().getMessage(language, "help.builder.name"))
                    .asGuiItem(event -> {
                        // TODO
                    }));


            this.gui.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());
            Bukkit.getScheduler().runTask(this.lobby, () -> this.gui.open(player));

        });
    }

    public Gui getGui() {
        return gui;
    }


}
