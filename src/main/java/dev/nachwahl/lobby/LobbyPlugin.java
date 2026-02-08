package dev.nachwahl.lobby;

import co.aikar.commands.PaperCommandManager;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import dev.nachwahl.cosmetics.Cosmetics;
import dev.nachwahl.lobby.commands.*;
import dev.nachwahl.lobby.events.*;
import dev.nachwahl.lobby.hologram.HologramAPI;
import dev.nachwahl.lobby.language.LanguageAPI;
import dev.nachwahl.lobby.leaderboards.JnRLeaderboard;
import dev.nachwahl.lobby.leaderboards.LeaderboardManager;
import dev.nachwahl.lobby.plan.PlanIntegration;
import dev.nachwahl.lobby.plan.QueryAPIAccessor;
import dev.nachwahl.lobby.quests.ArenaManager;
import dev.nachwahl.lobby.quests.PoolManager;
import dev.nachwahl.lobby.quests.QuestManager;
import dev.nachwahl.lobby.quests.listener.BlockBreakListener;
import dev.nachwahl.lobby.quests.listener.BlockPlaceEvent;
import dev.nachwahl.lobby.quests.listener.InteractListener;
import dev.nachwahl.lobby.quests.listener.JoinListener;
import dev.nachwahl.lobby.scoreboard.Scoreboard;
import dev.nachwahl.lobby.storage.Database;
import dev.nachwahl.lobby.utils.*;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Getter
public final class LobbyPlugin extends JavaPlugin implements PluginMessageListener {

    @Getter
    private static LobbyPlugin instance;
    private final ArrayList<Player> editModePlayers = new ArrayList<>();
    private final HashMap<UUID, ItemStack> elytraPlayers = new HashMap<>();
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
    private CinematicUtil cinematicUtil;
    private LeaderboardManager leaderboardManager;
    private BOTMScoreAPI botmScoreAPI;
    private JnRLeaderboard jnRLeaderboard;

    private QuestManager questManager;
    private PoolManager poolManager;
    private ArenaManager arenaManager;

    @Getter
    private Scoreboard scoreboard;

    @Getter
    private Cosmetics cosmeticsInstance;

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

        this.cinematicUtil = new CinematicUtil(this);
        cinematicUtil.createFile();

        if (Bukkit.getPluginManager().getPlugin("BTEG-Cosmetics") != null) {
            Cosmetics cosmetics = (Cosmetics) Bukkit.getServer().getPluginManager().getPlugin("BTEG-Cosmetics");
            this.cosmeticsInstance = cosmetics;
            scoreboard = new Scoreboard(this, cosmetics);
            Bukkit.getLogger().info("Cosmetics Plugin gefunden.");
        } else {
            Bukkit.getLogger().severe("Cosmetics Plugin nicht gefunden.");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new LobbyPlaceholderExpansion(this).register();
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            scoreboard.initScoreboard(player);
        }

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
        this.userSettingsAPI = new UserSettingsAPI(this);
        this.botmScoreAPI = new BOTMScoreAPI(this);
        this.jnRLeaderboard = new JnRLeaderboard(this);
        this.realTime = new RealTime(this.getConfig().getString("time.timezone"), this.getConfig().getInt("time.updateInterval"), Bukkit.getWorld("world"));
        this.vanish = new Vanish();
        this.hologramAPI = new HologramAPI(this);
        this.hologramAPI.loadData();
        this.miniGameBlockUtil = new MiniGameBlockUtil(this);
        MiniGameBlockUtil.reloadHolograms();
        try {
            Optional<QueryAPIAccessor> optionalQueryAPIAccessor = new PlanIntegration().hookIntoPlan();
            planQuery = optionalQueryAPIAccessor.get();
            Bukkit.getLogger().info("Plan ist installiert.");
        } catch (Exception e) {
            Bukkit.getLogger().info("Plan ist nicht installiert.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        try {
            this.leaderboardManager = new LeaderboardManager(this);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        this.bungeeConnector = new BungeeConnector(this);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);


        try {
            questManager.setPools();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        new BOTMPlaceholder(this).register();
        new MonthPlaceholders(this).register();

        // Update scoreboards
        getServer().getScheduler().runTaskTimer(this, () -> {
            scoreboard.updateScoreboards();
        }, 0, 100L);

    }

    @Override
    public void onDisable() {

        Bukkit.getLogger().info("Das Lobby Plugin wurde deaktiviert.");
        this.jnRLeaderboard.cancel();
        this.database.disconnect();
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
    }

    public void registerListeners() {
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

    public void registerCommand() {
        this.manager.registerCommand(new LanguageCommand());
        this.manager.registerCommand(new LobbyManageCommand());
        this.manager.registerCommand(new LocationCommand());
        this.manager.registerCommand(new VanishCommand());
        //Objects.requireNonNull(getCommand("quest")).setExecutor(new Quests());
        this.manager.registerCommand(new QuestsCommand());
        this.manager.registerCommand(new RegisterMiniGameBlockCommand());
        this.manager.registerCommand(new SpawnCommand());
        this.manager.registerCommand(new SettingsCommand());
        this.manager.registerCommand(new GamemodeCommand());
        this.manager.registerCommand(new NavigatorCommand());
        this.manager.registerCommand(new TutorialCommand());
        this.manager.registerCommand(new SoonCommand());
        this.manager.registerCommand(new VisitCommand());
        this.manager.registerCommand(new CinematicCommand(cinematicUtil));
        this.manager.registerCommand(new BOTMCommand(this));
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
