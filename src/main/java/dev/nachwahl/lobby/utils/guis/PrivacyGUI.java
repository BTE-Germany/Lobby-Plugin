package dev.nachwahl.lobby.utils.guis;

import dev.nachwahl.lobby.Lobby;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.sql.SQLException;

public class PrivacyGUI {

    private Gui gui;
    private Player player;
    private Lobby lobby;

    public PrivacyGUI(Player player, Lobby lobby) {
        this.player = player;
        this.lobby= lobby;
        var mm = MiniMessage.miniMessage();
        this.gui = Gui.gui()
                .title(mm.deserialize("<color:#383838>▶</color> <color:#0058e6><i>Datenschutz</i> </color> <color:#383838>/</color> <color:#0058e6><i>Privacy</i> </color> <color:#383838>◀</color>"))
                .rows(3)
                .disableAllInteractions()
                .create();

        this.gui.setItem(2, 5, ItemBuilder.from(Material.WRITABLE_BOOK).enchant(Enchantment.ARROW_INFINITE)
                .flags(ItemFlag.HIDE_ENCHANTS)
                .name(mm.deserialize("<color:#a3ceff><u>Datenschutzerklärung</u> </color><dark_gray>-</dark_gray> <color:#a3ceff><u>Privacy policy</u></color>"))
                .lore(
                        mm.deserialize("<gray>Durch das akzeptieren erklärst du dich </gray>"),
                        mm.deserialize("<gray>mit unserer Datenschutzerkärung einverstanden.</gray>"),
                        Component.text(""),
                        mm.deserialize("<gray>By accepting, you agree with our privacy policy.</gray>"),
                        Component.text(""),
                        mm.deserialize("<gray><i>https://buildthe.earth/privacy</i></gray>")).asGuiItem());

        this.gui.setItem(2, 3, ItemBuilder.skull().texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyZTMxZmZiNTljOTBhYjA4ZmM5ZGMxZmUyNjgwMjAzNWEzYTQ3YzQyZmVlNjM0MjNiY2RiNDI2MmVjYjliNiJ9fX0=")
                .name(mm.deserialize("<color:#3dff2b><b>Akzeptieren / Accept</b></color>"))
                .asGuiItem(event -> {
                    try {
                        this.lobby.getDatabase().executeInsert("INSERT INTO privacy (minecraftUUID) VALUES (?)", player.getUniqueId().toString());
                        this.gui.close(player);
                        LanguageGUI languageGUI = new LanguageGUI(this.lobby);
                        languageGUI.getGui().open(player);
                    }
                    catch (SQLException exception) {
                        exception.printStackTrace();
                    }
                }));
        this.gui.setItem(2, 7, ItemBuilder.skull().texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==")
                .name(mm.deserialize("<color:#ff3d40><b>Ablehnen / Decline</b></color>"))
                .asGuiItem(event -> {
                    player.kick(mm.deserialize("<red><b>Sorry :C</b></red>\n" +
                            "\n" +
                            "<gray>You have to accept the privacy policy.</gray>\n" +
                            "<gray>Du musst der Datenschutzerklärung zustimmen.</gray>"));
                }));
        this.gui.getFiller().fill(ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).name(Component.text("")).asGuiItem());
    }

    public Gui getGui() {
        return gui;
    }
}
