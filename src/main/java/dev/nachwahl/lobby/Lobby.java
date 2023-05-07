package dev.nachwahl.lobby;

import co.aikar.commands.PaperCommandManager;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import dev.nachwahl.lobby.commands.LanguageCommand;
import dev.nachwahl.lobby.commands.LobbyManageCommand;
import dev.nachwahl.lobby.commands.LocationCommand;
import dev.nachwahl.lobby.events.EnvironmentEvents;
import dev.nachwahl.lobby.events.InventoryClose;
import dev.nachwahl.lobby.events.ItemClick;
import dev.nachwahl.lobby.events.PlayerEvents;
import dev.nachwahl.lobby.storage.Database;
import dev.nachwahl.lobby.utils.*;
import dev.nachwahl.lobby.utils.language.LanguageAPI;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.ArrayList;

@Getter
public final class Lobby extends JavaPlugin implements PluginMessageListener {

    @Getter
    private static Lobby instance;
    private final ArrayList<Player> editModePlayers = new ArrayList<>();
    private PaperCommandManager manager;
    private Database database;
    private MiniMessage miniMessage;
    private LanguageAPI languageAPI;
    private HotbarItems hotbarItems;
    private LocationAPI locationAPI;
    private UserSettingsAPI userSettingsAPI;
    private BungeeConnector bungeeConnector;
    private RealTime realTime;

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

        Bukkit.getPluginManager().registerEvents(new PlayerEvents(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClose(this), this);
        Bukkit.getPluginManager().registerEvents(new ItemClick(this), this);
        Bukkit.getPluginManager().registerEvents(new EnvironmentEvents(this), this);

        this.miniMessage = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .resolver(Placeholder.parsed("prefix", this.getConfig().getString("prefix")))
                        .build())
                .build();

        this.languageAPI = new LanguageAPI(this);
        this.hotbarItems = new HotbarItems(this);
        this.locationAPI = new LocationAPI(this);
        this.locationAPI = new LocationAPI(this);
        this.userSettingsAPI = new UserSettingsAPI(this);
        this.realTime = new RealTime(this.getConfig().getString("time.timezone"), this.getConfig().getInt("time.updateInterval"), Bukkit.getWorld("world"));

        this.bungeeConnector = new BungeeConnector(this);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);


    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Das Lobby Plugin wurde deaktiviert.");
        this.database.disconnect();
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equals("SomeSubChannel")) {
            // Use the code sample in the 'Response' sections below to read
            // the data.
        }
    }

    public co.aikar.idb.Database getDatabase() {
        return this.database.getDatabase();
    }
}
