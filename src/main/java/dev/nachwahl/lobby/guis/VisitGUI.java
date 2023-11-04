package dev.nachwahl.lobby.guis;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.language.Language;
import dev.nachwahl.lobby.utils.ItemGenerator;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VisitGUI {

    private Gui gui;
    private final Lobby lobby;

    public VisitGUI(Lobby lobby, Player player) {
        this.lobby = lobby;
        this.lobby.getLanguageAPI().getLanguage(player, language -> {
            this.gui = Gui.gui()
                    .title(this.lobby.getLanguageAPI().getMessage(language, "map.title"))
                    .rows(3)
                    .disableAllInteractions()
                    .create();

            this.gui.setItem(2, 4,ItemBuilder.from(Material.LIGHTNING_ROD)
                            .name(this.lobby.getLanguageAPI().getMessage(language, "map.tpll.name"))
                    .asGuiItem(event -> {
                        event.getInventory().close();
                        Component component = this.lobby.getLanguageAPI().getMessage(language, "map.tpll.message");
                        if(language == Language.ENGLISH) {
                            player.sendMessage(component.clickEvent(ClickEvent.openUrl("https://youtu.be/ukQ4ATKlhWU")));
                        } else {
                            player.sendMessage(component.clickEvent(ClickEvent.openUrl("https://youtu.be/T2NMEBdUAvs")));
                        }
                    }));

            this.gui.setItem(2, 6, ItemBuilder.from(Material.COMPASS)
                            .name(this.lobby.getLanguageAPI().getMessage(language, "navigator.warps.name"))
                    .asGuiItem(event -> {
                        player.performCommand("nwarp");
                        event.getInventory().close();
                    }));

            this.gui.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());
            Bukkit.getScheduler().runTask(this.lobby, () -> this.gui.open(player));

        });
    }

    public Gui getGui() {
        return gui;
    }


}
