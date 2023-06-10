package dev.nachwahl.lobby;

import co.aikar.commands.PaperCommandManager;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import dev.nachwahl.lobby.commands.LanguageCommand;
import dev.nachwahl.lobby.commands.LobbyManageCommand;
import dev.nachwahl.lobby.commands.LocationCommand;
import dev.nachwahl.lobby.commands.VanishCommand;
import dev.nachwahl.lobby.events.*;
import dev.nachwahl.lobby.storage.Database;
import dev.nachwahl.lobby.utils.*;
import dev.nachwahl.lobby.utils.language.LanguageAPI;
import dev.nachwahl.lobby.utils.plan.PlanIntegration;
import dev.nachwahl.lobby.utils.plan.QueryAPIAccessor;
import dev.nachwahl.lobby.utils.plan.user.User;
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
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
    private QueryAPIAccessor planQuery;
    private Vanish vanish;

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
        this.manager.registerCommand(new VanishCommand());

        Bukkit.getPluginManager().registerEvents(new PlayerEvents(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClose(this), this);
        Bukkit.getPluginManager().registerEvents(new ItemClick(this), this);
        Bukkit.getPluginManager().registerEvents(new EnvironmentEvents(this), this);
        Bukkit.getPluginManager().registerEvents(new DoubleJumpEvent(this), this);

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
        this.vanish = new Vanish();

        try {
            Optional<QueryAPIAccessor> optionalQueryAPIAccessor = new PlanIntegration().hookIntoPlan();
            planQuery = optionalQueryAPIAccessor.get();
        } catch (Exception e) {
            Bukkit.getLogger().info("Plan ist nicht installiert.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("Testing: Top Playtime:");
        for(User user:planQuery.getTopPlaytimeOnAllServers(TimeUnit.DAYS.toMillis(30L),0)) {
            System.out.println(user.getPlayer()+": "+user.getPlaytime());
        }
        System.out.println("");
        System.out.println("");
        System.out.println("");

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
