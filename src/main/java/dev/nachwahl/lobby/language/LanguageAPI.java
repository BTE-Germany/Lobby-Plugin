package dev.nachwahl.lobby.language;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.nachwahl.lobby.Lobby;
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
    private final Lobby lobby;
    private final HashMap<Language, HashMap<String, String>> messages = new HashMap<>();
    private final Cache<UUID, Language> languageCache = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build();

    @SneakyThrows
    public LanguageAPI(Lobby lobby) {
        this.lobby = lobby;

        File languageFilesFolder = new File(this.lobby.getDataFolder(), "languageFiles");

        if (!languageFilesFolder.exists()) {
            this.lobby.getLogger().info("Creating not existing language folder: " + languageFilesFolder.getAbsolutePath());
            languageFilesFolder.mkdirs();
        } else {
            this.lobby.getLogger().info("Found existing language folder: " + languageFilesFolder.getAbsolutePath());
        }

        for (Language language : Language.values()) {
            this.messages.put(language, new HashMap<>());

            Properties properties = new Properties();
            File languageFile = new File(languageFilesFolder, "lang_" + language.getLang() + ".properties");
            InputStream stream;

            if (!languageFile.exists()) {
                stream = Lobby.class.getClassLoader().getResourceAsStream("lang_" + language.getLang() + ".properties");
            } else {
                stream = new FileInputStream(languageFile);
            }

            if (stream == null) {
                this.lobby.getLogger().severe("Stream for language file of " + language.getLang() + " null!");
                continue;
            }

            properties.load(new InputStreamReader(stream, StandardCharsets.UTF_8));
            this.lobby.getLogger().info("Loading language file: " + "lang_" + language.getLang() + ".properties");
            properties.forEach((key, message) -> this.messages.get(language).put((String) key, (String) message));
        }
    }

    public Component getMessage(Language language, String messageKey, TagResolver... placeholders) {
        if (this.messages.get(language).containsKey(messageKey)) {
            return this.lobby.getMiniMessage().deserialize(this.messages.get(language).get(messageKey), placeholders);
        } else if (this.messages.get(DEFAULT).containsKey(messageKey)) {
            return this.lobby.getMiniMessage().deserialize(this.messages.get(DEFAULT).get(messageKey), placeholders);
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
        Language cached = this.languageCache.getIfPresent(player.getUniqueId());
        if (cached != null) {
            languageCallback.accept(cached);
            setLocalePermission(player, cached);
            return;
        }
        this.lobby.getDatabase().getFirstRowAsync("SELECT * FROM langUsers WHERE uuid = ?", player.getUniqueId().toString())
                .thenAccept(dbRow -> {
                    if (dbRow == null) {
                        languageCallback.accept(Language.ENGLISH);
                        setLocalePermission(player, Language.ENGLISH);
                        return;
                    }

                    Language language;
                    String selectedLanguage = dbRow.getString("lang");

                    if (selectedLanguage != null) {
                        try {
                            language = Language.valueOf(selectedLanguage);
                            this.languageCache.put(player.getUniqueId(), language);
                        } catch (IllegalArgumentException e) {
                            language = Language.ENGLISH;
                        }
                        languageCallback.accept(language);
                        setLocalePermission(player, language);
                    } else {
                        languageCallback.accept(Language.ENGLISH);
                        setLocalePermission(player, Language.ENGLISH);
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
        if (player.hasPermission(permission)) return;
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            LuckPerms api = provider.getProvider();
            String removePermission = getRemovePermissionFromLanguage(language);

            // Load, modify, then save
            api.getUserManager().modifyUser(player.getUniqueId(), user -> {
                user.data().add(Node.builder(permission).build());
                if (removePermission != null && player.hasPermission(removePermission)) user.data().remove(Node.builder(permission).build());
            });
        }
    }

    public String getPermissionFromLanguage(@NotNull Language language) {
        return "group." + language.name().toLowerCase(Locale.ROOT);
    }

    public String getRemovePermissionFromLanguage(@NotNull Language language) {
        if (language == Language.GERMAN) {
            return getPermissionFromLanguage(Language.ENGLISH);
        } else {
            return getPermissionFromLanguage(Language.GERMAN);
        }
    }

    public void setLanguage(Language language, @NotNull Player player) {
        this.lobby.getDatabase().getFirstRowAsync("SELECT * FROM langUsers WHERE uuid = ?", player.getUniqueId().toString()).thenAccept(row -> {
            if (row == null) {
                this.lobby.getDatabase().executeUpdateAsync("INSERT INTO langUsers (uuid, lang) VALUES (?, ?)", player.getUniqueId().toString(), language.getLang()).thenAccept(integer -> {
                    this.languageCache.put(player.getUniqueId(), language);
                    this.sendMessageToPlayer(player, "languageChanged");
                    this.lobby.getHotbarItems().setHotbarItems(player);
                });
            } else {
                this.lobby.getDatabase().executeUpdateAsync("UPDATE langUsers SET lang = ? WHERE uuid = ?", language.getLang(), player.getUniqueId().toString()).thenAccept(integer -> {
                    this.languageCache.put(player.getUniqueId(), language);
                    this.sendMessageToPlayer(player, "languageChanged");
                    this.lobby.getHotbarItems().setHotbarItems(player);
                });
            }
        });
    }
}
