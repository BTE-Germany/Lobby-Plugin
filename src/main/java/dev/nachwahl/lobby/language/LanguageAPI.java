package dev.nachwahl.lobby.language;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.nachwahl.lobby.LobbyPlugin;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


public class LanguageAPI {
    public static final Language DEFAULT = Language.GERMAN;
    private final LobbyPlugin lobbyPlugin;
    private final HashMap<Language, HashMap<String, String>> messages = new HashMap<>();
    private final Cache<UUID, Language> languageCache = CacheBuilder.newBuilder()
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build();

    @SneakyThrows
    public LanguageAPI(LobbyPlugin lobbyPlugin) {
        this.lobbyPlugin = lobbyPlugin;

        File languageFilesFolder = new File(this.lobbyPlugin.getDataFolder(), "languageFiles");

        if (!languageFilesFolder.exists()) {
            this.lobbyPlugin.getLogger().info("Creating not existing language folder: " + languageFilesFolder.getAbsolutePath());
            languageFilesFolder.mkdirs();
        } else {
            this.lobbyPlugin.getLogger().info("Found existing language folder: " + languageFilesFolder.getAbsolutePath());
        }

        for (Language language : Language.values()) {
            this.messages.put(language, new HashMap<>());

            Properties properties = new Properties();
            File languageFile = new File(languageFilesFolder, "lang_" + language.getLang() + ".properties");
            InputStream stream;

            if (!languageFile.exists()) {
                stream = LobbyPlugin.class.getClassLoader().getResourceAsStream("lang_" + language.getLang() + ".properties");
            } else {
                stream = new FileInputStream(languageFile);
            }

            if (stream == null) {
                this.lobbyPlugin.getLogger().severe("Stream for language file of " + language.getLang() + " null!");
                continue;
            }

            properties.load(new InputStreamReader(stream, StandardCharsets.UTF_8));
            this.lobbyPlugin.getLogger().info("Loading language file: " + "lang_" + language.getLang() + ".properties");
            properties.forEach((key, message) -> this.messages.get(language).put((String) key, (String) message));
        }
    }

    public Component getMessage(Language language, String messageKey, TagResolver... placeholders) {
        if (this.messages.get(language).containsKey(messageKey)) {
            return this.lobbyPlugin.getMiniMessage().deserialize(this.messages.get(language).get(messageKey), placeholders);
        } else if (this.messages.get(DEFAULT).containsKey(messageKey)) {
            return this.lobbyPlugin.getMiniMessage().deserialize(this.messages.get(DEFAULT).get(messageKey), placeholders);
        } else {
            return Component.text("Missing translation: " + messageKey);
        }
    }

    // TODO: add placeholder serialization
    public String getMessageString(Language language, String messageKey) {
        if (this.messages.get(language).containsKey(messageKey)) {
            return this.messages.get(language).get(messageKey);
        } else if (this.messages.get(DEFAULT).containsKey(messageKey)) {
            return this.messages.get(DEFAULT).get(messageKey);
        } else {
            return "Missing translation: " + messageKey;
        }
    }

    /**
     * Holt die Sprache aus der Datenbank und führt das Callback aus. Gleichzeitig wird die Language Permission gesetzt,
     */
    public void getLanguage(@NotNull Player player, Consumer<Language> languageCallback) {
        LobbyPlugin.getInstance().getHologramAPI().sendDebugMsg(Component.text("Get Language Callback START"));
        Language cached = this.languageCache.getIfPresent(player.getUniqueId());
        if (cached != null) {
            setLocalePermission(player, cached);
            languageCallback.accept(cached);
            return;
        }
        this.lobbyPlugin.getDatabase().getFirstRowAsync("SELECT * FROM langUsers WHERE uuid = ?", player.getUniqueId().toString())
                .thenAccept(dbRow -> {
                    if (dbRow == null) {
                        setLocalePermission(player, Language.ENGLISH);
                        languageCallback.accept(Language.ENGLISH);
                        return;
                    }

                    Language language;
                    String selectedLanguage = dbRow.getString("lang");

                    if (selectedLanguage != null) {
                        try {
                            language = Language.fromString(selectedLanguage);
                            this.languageCache.put(player.getUniqueId(), language);
                        } catch (IllegalArgumentException e) {
                            language = Language.ENGLISH;
                        }
                        setLocalePermission(player, language);
                        languageCallback.accept(language);
                    } else {
                        setLocalePermission(player, Language.ENGLISH);
                        languageCallback.accept(Language.ENGLISH);
                    }
                });
    }

    public void getMessage(Player player, Consumer<Component> callback, String messageKey, TagResolver... placeholders) {
        getLanguage(player, language -> callback.accept(getMessage(language, messageKey, placeholders)));
    }

    public void sendMessageToPlayer(Player player, String messageKey, TagResolver... placeholders) {
        getMessage(player, player::sendMessage, messageKey, placeholders);
    }

    public Language getLanguage(@NotNull Player player) {
        Language language = this.languageCache.getIfPresent(player.getUniqueId());
        if (language == null) {
            language = Language.ENGLISH;
        }
        setLocalePermission(player, language);
        return language;
    }

    private void setLocalePermission(@NotNull Player player, @NotNull Language language) {
        String permission = getPermissionFromLanguage(language);
        LobbyPlugin.getInstance().getHologramAPI().sendDebugMsg(Component.text("Set Local Permission " + permission));
        if (player.hasPermission(permission)) return;
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            LobbyPlugin.getInstance().getHologramAPI().sendDebugMsg(Component.text("Set Local Permission - provider is not null & player dont have alread the permission"));
            LuckPerms api = provider.getProvider();
            String removePermission = getPermissionFromLanguage(getOppositeLanguage(language));

            // Load, modify, then save
            api.getUserManager().modifyUser(player.getUniqueId(), user -> {
                LobbyPlugin.getInstance().getHologramAPI().sendDebugMsg(Component.text("Set Local - Try add perm"));
                user.data().add(Node.builder(permission).build());
                LobbyPlugin.getInstance().getHologramAPI().sendDebugMsg(Component.text("Set Local - Try remove perm"));
                if (removePermission != null && player.hasPermission(removePermission))
                    user.data().remove(Node.builder(permission).build());
                LobbyPlugin.getInstance().getHologramAPI().sendDebugMsg(Component.text("Set Local - Everything worked fine"));
            });
        }
    }

    public String getPermissionFromLanguage(@NotNull Language language) {
        return "group." + language.name().toLowerCase(Locale.ROOT);
    }

    public Language getOppositeLanguage(@NotNull Language language) {
        if (language == Language.GERMAN) {
            return Language.ENGLISH;
        } else {
            return Language.GERMAN;
        }
    }

    public void setLanguage(Language language, @NotNull Player player) {
        this.lobbyPlugin.getDatabase().getFirstRowAsync("SELECT * FROM langUsers WHERE uuid = ?", player.getUniqueId().toString()).thenAccept(row -> {
            if (row == null) {
                this.lobbyPlugin.getDatabase().executeUpdateAsync("INSERT INTO langUsers (uuid, lang) VALUES (?, ?)", player.getUniqueId().toString(), language.getLang()).thenAccept(integer -> {
                    this.languageCache.put(player.getUniqueId(), language);
                    this.lobbyPlugin.getLanguageAPI().sendMessageToPlayer(player, "languageChanged");
                    LobbyPlugin.getInstance().getHologramAPI().sendDebugMsg(Component.text("INSERT LANG Set Language Permission to " + getPermissionFromLanguage(language)));
                    this.lobbyPlugin.getHotbarItems().setHotbarItems(player);
                });
            } else {
                this.lobbyPlugin.getDatabase().executeUpdateAsync("UPDATE langUsers SET lang = ? WHERE uuid = ?", language.getLang(), player.getUniqueId().toString()).thenAccept(integer -> {
                    this.languageCache.put(player.getUniqueId(), language);
                    this.lobbyPlugin.getLanguageAPI().sendMessageToPlayer(player, "languageChanged");
                    LobbyPlugin.getInstance().getHologramAPI().sendDebugMsg(Component.text("INSERT LANG Set Language Permission to " + getPermissionFromLanguage(language)));
                    this.lobbyPlugin.getHotbarItems().setHotbarItems(player);
                });
            }
        });
    }
}
