package dev.nachwahl.lobby.guis;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.language.Language;
import dev.nachwahl.lobby.utils.ItemGenerator;
import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class LanguageGUI {

    @Getter
    private Gui gui;
    private final Lobby lobby;

    public LanguageGUI(Lobby lobby, Player player) {
        this.lobby = lobby;
        this.lobby.getLanguageAPI().getLanguage(player, language -> {
            this.gui = Gui.gui()
                    .title(this.lobby.getLanguageAPI().getMessage(language, "language-gui.title"))
                    .rows(3)
                    .disableAllInteractions()
                    .create();

            this.gui.setItem(2, 3, PaperItemBuilder.from(ItemGenerator.customModel(Material.PAPER, "de_flag"))
                    .name(this.lobby.getLanguageAPI().getMessage(language, "language-gui.german"))
                    .asGuiItem(event -> {
                        lobby.getLanguageAPI().setLanguage(Language.GERMAN, player);
                        event.getInventory().close();
                    }));
            this.gui.setItem(2, 7, PaperItemBuilder.from(ItemGenerator.customModel(Material.PAPER, "us_flag"))
                    .name(this.lobby.getLanguageAPI().getMessage(language, "language-gui.english"))
                    .asGuiItem(event -> {
                        lobby.getLanguageAPI().setLanguage(Language.ENGLISH, player);
                        event.getInventory().close();
                    }));
            this.gui.getFiller().fill(PaperItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());
            Bukkit.getScheduler().runTask(this.lobby, () -> this.gui.open(player));
        });
    }
}
