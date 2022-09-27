package dev.nachwahl.lobby;

import co.aikar.commands.PaperCommandManager;
import dev.nachwahl.lobby.commands.LanguageCommand;
import dev.nachwahl.lobby.commands.LobbyManageCommand;
import dev.nachwahl.lobby.commands.LocationCommand;
import dev.nachwahl.lobby.events.InventoryClose;
import dev.nachwahl.lobby.events.ItemClick;
import dev.nachwahl.lobby.events.JoinEvent;
import dev.nachwahl.lobby.storage.Database;
import dev.nachwahl.lobby.utils.HotbarItems;
import dev.nachwahl.lobby.utils.LocationAPI;
import dev.nachwahl.lobby.utils.language.LanguageAPI;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class Lobby extends JavaPlugin {
    private static Lobby instance;
    private PaperCommandManager manager;
    private Database database;
    private MiniMessage miniMessage;
    private LanguageAPI languageAPI;
    private HotbarItems hotbarItems;
    private LocationAPI locationAPI;

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();
        this.database = new Database(this);
        this.database.connect();
        Bukkit.getLogger().info("Das Lobby Plugin wurde aktiviert.");
        this.manager = new PaperCommandManager(this);

        this.manager.registerCommand(new LanguageCommand());
        this.manager.registerCommand(new LobbyManageCommand());
        this.manager.registerCommand(new LocationCommand());

        Bukkit.getPluginManager().registerEvents(new JoinEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClose(this), this);
        Bukkit.getPluginManager().registerEvents(new ItemClick(this), this);

        this.miniMessage = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .resolver(Placeholder.parsed("prefix", this.getConfig().getString("prefix")))
                        .build())
                .build();

        this.languageAPI = new LanguageAPI(this);
        this.hotbarItems = new HotbarItems(this);
        this.locationAPI = new LocationAPI(this);
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Das Lobby Plugin wurde deaktiviert.");
        this.database.disconnect();
    }

    public co.aikar.idb.Database getDatabase() {
        return this.database.getDatabase();
    }
}
