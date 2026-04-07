package dev.nachwahl.lobby.guis;

import dev.nachwahl.lobby.LobbyPlugin;
import dev.nachwahl.lobby.language.Language;
import dev.nachwahl.lobby.utils.GuiUtil;
import dev.nachwahl.lobby.utils.ItemGenerator;
import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class VisitGUI {

    private static final int FIRST_ROW_TPLL_WARPS = 2;
    private static final int FIRST_COL_TPLL = 2;
    private static final int FIRST_COL_WARPS = 7;

    @Getter
    private Gui gui;
    private final LobbyPlugin lobbyPlugin;

    public VisitGUI(LobbyPlugin lobbyPlugin, Player player) {
        this.lobbyPlugin = lobbyPlugin;
        this.lobbyPlugin.getLanguageAPI().getLanguage(player, language -> {
            this.gui = Gui.gui()
                    .title(GuiUtil.getCustomDataTitle(this.lobbyPlugin.getLanguageAPI().getMessage(language, "map.title"), "ऑ"))
                    .rows(4)
                    .disableAllInteractions()
                    .create();

            // hide [i] button
            this.gui.setItem(1, 1, PaperItemBuilder.from(ItemGenerator.customModel(Material.PAPER, "blank"))
                    .name(Component.empty())
                    .asGuiItem());



            this.gui.setItem(1, 9, PaperItemBuilder.from(ItemGenerator.customModelEmpty())
                    .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "gui.close.label"))
                    .asGuiItem(event -> event.getInventory().close()));


            GuiItem itemTpll = PaperItemBuilder.from(ItemGenerator.customModelEmpty())
                    .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "map.tpll.name"))
                    .asGuiItem(event -> {
                        event.getInventory().close();
                        Component component = this.lobbyPlugin.getLanguageAPI().getMessage(language, "map.tpll.message");
                        if (language == Language.ENGLISH) {
                            player.sendMessage(component.clickEvent(ClickEvent.openUrl("https://youtu.be/ukQ4ATKlhWU")));
                        } else {
                            player.sendMessage(component.clickEvent(ClickEvent.openUrl("https://youtu.be/T2NMEBdUAvs")));
                        }
                    });
            this.gui.setItem(FIRST_ROW_TPLL_WARPS, FIRST_COL_TPLL, itemTpll);
            this.gui.setItem(FIRST_ROW_TPLL_WARPS, FIRST_COL_TPLL + 1, itemTpll);
            this.gui.setItem(FIRST_ROW_TPLL_WARPS + 1, FIRST_COL_TPLL, itemTpll);
            this.gui.setItem(FIRST_ROW_TPLL_WARPS + 1, FIRST_COL_TPLL + 1, itemTpll);


            GuiItem itemWarps = PaperItemBuilder.from(ItemGenerator.customModelEmpty())
                    .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "navigator.warps.name"))
                    .asGuiItem(event -> {
                        player.performCommand("nwarp");
                        event.getInventory().close();
                    });
            this.gui.setItem(FIRST_ROW_TPLL_WARPS, FIRST_COL_WARPS, itemWarps);
            this.gui.setItem(FIRST_ROW_TPLL_WARPS, FIRST_COL_WARPS + 1, itemWarps);
            this.gui.setItem(FIRST_ROW_TPLL_WARPS + 1, FIRST_COL_WARPS, itemWarps);
            this.gui.setItem(FIRST_ROW_TPLL_WARPS + 1, FIRST_COL_WARPS + 1, itemWarps);


            Bukkit.getScheduler().runTask(this.lobbyPlugin, () -> this.gui.open(player));
        });
    }

}
