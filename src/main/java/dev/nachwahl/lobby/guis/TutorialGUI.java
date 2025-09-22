package dev.nachwahl.lobby.guis;

import dev.nachwahl.lobby.Lobby;
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
    private final Lobby lobby;

    public TutorialGUI(Lobby lobby, Player player) {
        this.lobby = lobby;
        this.lobby.getLanguageAPI().getLanguage(player, language -> {
            this.gui = Gui.gui()
                    .title(this.lobby.getLanguageAPI().getMessage(language, "help.title"))
                    .rows(3)
                    .disableAllInteractions()
                    .create();


            this.gui.setItem(2, 3, PaperItemBuilder.from(ItemGenerator.customModel(Material.PAPER, "plot"))
                    .name(this.lobby.getLanguageAPI().getMessage(language, "help.plots.name"))
                    .asGuiItem(event -> this.lobby.getBungeeConnector().sendToServer(player, this.lobby.getConfig().getString("server.Plot"), true)));

            ItemStack item = new ItemStack(org.bukkit.Material.DIAMOND_PICKAXE);
            item.unsetData(DataComponentTypes.ATTRIBUTE_MODIFIERS);

            this.gui.setItem(2, 7, PaperItemBuilder.from(item)
                    .name(this.lobby.getLanguageAPI().getMessage(language, "help.apply.name"))
                    .asGuiItem(event -> {
                        event.getInventory().close();
                        player.sendMessage(this.lobby.getLanguageAPI().getMessage(language, "help.apply.message")
                            .clickEvent(net.kyori.adventure.text.event.ClickEvent.openUrl("https://buildtheearth.net/teams/de/apply")));
                    }));


            this.gui.setItem(3, 9, PaperItemBuilder.from(Material.BOOK)
                    .name(this.lobby.getLanguageAPI().getMessage(language, "help.guides.name"))
                    .asGuiItem(event -> {
                        event.getInventory().close();
                        new TutorialsGUI(lobby, player);
                    }));


            this.gui.getFiller().fill(PaperItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());
            Bukkit.getScheduler().runTask(this.lobby, () -> this.gui.open(player));
        });
    }
}
