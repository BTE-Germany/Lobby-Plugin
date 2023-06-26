package dev.nachwahl.lobby;

import co.aikar.commands.PaperCommandManager;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import dev.nachwahl.lobby.commands.*;
import dev.nachwahl.lobby.events.*;
import dev.nachwahl.lobby.quests.ArenaManager;
import dev.nachwahl.lobby.quests.PoolManager;
import dev.nachwahl.lobby.quests.QuestManager;
import dev.nachwahl.lobby.quests.listener.BlockBreakListener;
import dev.nachwahl.lobby.quests.listener.BlockPlaceEvent;
import dev.nachwahl.lobby.quests.listener.InteractListener;
import dev.nachwahl.lobby.quests.listener.JoinListener;
import dev.nachwahl.lobby.storage.Database;
import dev.nachwahl.lobby.utils.*;
import dev.nachwahl.lobby.utils.hologram.HologramAPI;
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
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.sql.SQLException;
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
    private HologramAPI hologramAPI;
    private MiniGameBlockUtil miniGameBlockUtil;

    private QuestManager questManager;
    private PoolManager poolManager;
    private ArenaManager arenaManager;

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();
        this.database = new Database(this);
        this.database.connect();
        Bukkit.getLogger().info("Das Lobby Plugin wurde aktiviert.");
        this.manager = new PaperCommandManager(this);

        questManager = new QuestManager();
        poolManager = new PoolManager();
        arenaManager = new ArenaManager();

        registerListeners();
        registerCommand();

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
        this.hologramAPI = new HologramAPI(this);
        this.hologramAPI.loadData();
        this.miniGameBlockUtil = new MiniGameBlockUtil(this);

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


        try {
            questManager.setPools();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Das Lobby Plugin wurde deaktiviert.");
        this.database.disconnect();
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
    }

    public void registerListeners(){
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerEvents(this), this);
        pluginManager.registerEvents(new InventoryClose(this), this);
        pluginManager.registerEvents(new ItemClick(this), this);
        pluginManager.registerEvents(new EnvironmentEvents(this), this);
        pluginManager.registerEvents(new DoubleJumpEvent(this), this);
        pluginManager.registerEvents(new MiniGameBlockInteractEvent(), this);

        //Quests

        pluginManager.registerEvents(new JoinListener(), this);
        pluginManager.registerEvents(new BlockBreakListener(), this);
        pluginManager.registerEvents(new InteractListener(), this);
        pluginManager.registerEvents(new BlockPlaceEvent(), this);

    }

    public void registerCommand(){
        this.manager.registerCommand(new LanguageCommand());
        this.manager.registerCommand(new LobbyManageCommand());
        this.manager.registerCommand(new LocationCommand());
        this.manager.registerCommand(new VanishCommand());
        //Objects.requireNonNull(getCommand("quest")).setExecutor(new Quests());
        this.manager.registerCommand(new QuestsCommand());
        this.manager.registerCommand(new RegisterMiniGameBlockCommand());
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
