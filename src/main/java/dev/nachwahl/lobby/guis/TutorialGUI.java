package dev.nachwahl.lobby.guis;

import dev.nachwahl.lobby.LobbyPlugin;
import dev.nachwahl.lobby.utils.ItemGenerator;
import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TutorialGUI {

    @lombok.Getter
    private Gui gui;
    private final LobbyPlugin lobbyPlugin;

    public TutorialGUI(LobbyPlugin lobbyPlugin, Player player) {
        this.lobbyPlugin = lobbyPlugin;
        this.lobbyPlugin.getLanguageAPI().getLanguage(player, language -> {
            this.gui = Gui.gui()
                    .title(this.lobbyPlugin.getLanguageAPI().getMessage(language, "help.title"))
                    .rows(3)
                    .disableAllInteractions()
                    .create();


            this.gui.setItem(2, 3, PaperItemBuilder.from(ItemGenerator.customModel(Material.PAPER, "plot"))
                    .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "help.plots.name"))
                    .asGuiItem(event -> this.lobbyPlugin.getBungeeConnector().sendToServer(player, this.lobbyPlugin.getConfig().getString("server.Plot"), true)));

            ItemStack item = new ItemStack(org.bukkit.Material.DIAMOND_PICKAXE);
            item.unsetData(DataComponentTypes.ATTRIBUTE_MODIFIERS);

            this.gui.setItem(2, 7, PaperItemBuilder.from(item)
                    .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "help.apply.name"))
                    .asGuiItem(event -> {
                        event.getInventory().close();
                        player.sendMessage(this.lobbyPlugin.getLanguageAPI().getMessage(language, "help.apply.message")
                                .clickEvent(net.kyori.adventure.text.event.ClickEvent.openUrl("https://buildtheearth.net/teams/de/apply")));
                    }));


            this.gui.setItem(3, 9, PaperItemBuilder.from(Material.BOOK)
                    .name(this.lobbyPlugin.getLanguageAPI().getMessage(language, "help.guides.name"))
                    .asGuiItem(event -> {
                        event.getInventory().close();
                        new TutorialsGUI(lobbyPlugin, player);
                    }));


            this.gui.getFiller().fill(PaperItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());
            Bukkit.getScheduler().runTask(this.lobbyPlugin, () -> this.gui.open(player));
        });
    }
}
