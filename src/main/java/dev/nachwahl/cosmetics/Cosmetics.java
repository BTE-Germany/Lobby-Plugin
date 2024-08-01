package dev.nachwahl.cosmetics;

import co.aikar.commands.PaperCommandManager;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import dev.nachwahl.cosmetics.language.LanguageAPI;
import dev.nachwahl.cosmetics.storage.Database;
import dev.nachwahl.cosmetics.utils.BungeeConnector;
import dev.nachwahl.cosmetics.utils.UserSettingsAPI;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@Getter
public final class Cosmetics extends JavaPlugin implements PluginMessageListener {

    @Getter
    private static Cosmetics instance;
    private final ArrayList<Player> editModePlayers = new ArrayList<>();
    private final HashMap<UUID, ItemStack> elytraPlayers = new HashMap<>();
    private PaperCommandManager manager;
    private Database database;
    private MiniMessage miniMessage;
    private LanguageAPI languageAPI;
    private UserSettingsAPI userSettingsAPI;
    private BungeeConnector bungeeConnector;

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();
        this.database = new Database(this);
        this.database.connect();
        Bukkit.getLogger().info("Das Lobby Plugin wurde aktiviert.");
        this.manager = new PaperCommandManager(this);


        registerListeners();
        registerCommand();

        this.miniMessage = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .resolver(Placeholder.parsed("prefix", this.getConfig().getString("prefix")))
                        .build())
                .build();

        this.languageAPI = new LanguageAPI(this);
        this.userSettingsAPI = new UserSettingsAPI(this);
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

    public void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();
    }

    public void registerCommand() {
    }


    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equals("SomeSubChannel")) {
        }
    }

    public co.aikar.idb.Database getDatabase() {
        return this.database.getDatabase();
    }
}
