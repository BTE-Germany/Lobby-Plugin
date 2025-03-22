package dev.nachwahl.lobby.guis;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.utils.ItemGenerator;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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


            this.gui.setItem(2, 3, ItemBuilder.from(ItemGenerator.customModel(Material.PAPER, 2))
                    .name(this.lobby.getLanguageAPI().getMessage(language, "help.plots.name"))
                    .asGuiItem(event -> {
                        this.lobby.getBungeeConnector().sendToServer(player, this.lobby.getConfig().getString("server.Plot"), true);
                    }));


            this.gui.setItem(2, 5, ItemBuilder.from(Material.WOODEN_PICKAXE)
                    .name(this.lobby.getLanguageAPI().getMessage(language, "help.apply.name"))
                    .asGuiItem(event -> {
                        event.getInventory().close();
                        player.sendMessage(this.lobby.getLanguageAPI().getMessage(language, "help.apply.message").clickEvent(net.kyori.adventure.text.event.ClickEvent.openUrl("https://buildtheearth.net/teams/de/apply")));
                    }));

            this.gui.setItem(2, 7, ItemBuilder.from(Material.DIAMOND_PICKAXE)
                    .name(Component.text("Coming soon...").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)/*this.lobby.getLanguageAPI().getMessage(language, "help.builder.name")*/)
                    .asGuiItem(event -> {
                        // TODO
                    }));


            this.gui.setItem(3, 9, ItemBuilder.from(Material.BOOK)
                    .name(this.lobby.getLanguageAPI().getMessage(language, "help.guides.name"))
                    .asGuiItem(event -> {
                        event.getInventory().close();
                        new TutorialsGUI(lobby, player);
                    }));


            this.gui.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());
            Bukkit.getScheduler().runTask(this.lobby, () -> this.gui.open(player));

        });
    }

    public Gui getGui() {
        return gui;
    }


}
