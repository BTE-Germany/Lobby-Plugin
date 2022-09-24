package dev.nachwahl.lobby.utils.guis;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.utils.language.Language;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class LanguageGUI {

    private Gui gui;
    private Lobby lobby;

    public LanguageGUI(Lobby lobby) {
        var mm = MiniMessage.miniMessage();
        this.gui = Gui.gui()
                .title(mm.deserialize("<color:#383838>▶</color> <color:#0058e6><i>Sprache</i> </color> <color:#383838>/</color> <color:#0058e6><i>Language</i> </color> <color:#383838>◀</color>"))
                .rows(3)
                .disableAllInteractions()
                .create();

        this.gui.setItem(2, 3, ItemBuilder.skull().texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWU3ODk5YjQ4MDY4NTg2OTdlMjgzZjA4NGQ5MTczZmU0ODc4ODY0NTM3NzQ2MjZiMjRiZDhjZmVjYzc3YjNmIn19fQ==")
                .name(mm.deserialize("<color:#ff615e><b>Deutsch</b></color>"))
                .asGuiItem(event -> {
                    Player player = (Player) event.getWhoClicked();
                    lobby.getLanguageAPI().setLanguage(Language.GERMAN, player);
                    event.getInventory().close();
                }));
        this.gui.setItem(2, 7, ItemBuilder.skull().texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNhYzk3NzRkYTEyMTcyNDg1MzJjZTE0N2Y3ODMxZjY3YTEyZmRjY2ExY2YwY2I0YjM4NDhkZTZiYzk0YjQifX19")
                .name(mm.deserialize("<b><color:#1f44ff>English</color></b>"))
                .asGuiItem(event -> {
                    Player player = (Player) event.getWhoClicked();
                    lobby.getLanguageAPI().setLanguage(Language.ENGLISH, player);
                    event.getInventory().close();
                }));
        this.gui.getFiller().fill(ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).name(Component.text("")).asGuiItem());
    }

    public Gui getGui() {
        return gui;
    }


}
