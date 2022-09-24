package dev.nachwahl.lobby.utils.language;

import co.aikar.idb.DbRow;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.nachwahl.lobby.Lobby;
import lombok.SneakyThrows;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class LanguageAPI {
    private final Lobby lobby;
    private final HashMap<Language, HashMap<String, String>> messages = new HashMap<>();
    private final Cache<UUID, Language> languageCache = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build();

    @SneakyThrows
    public LanguageAPI(Lobby lobby) {
        this.lobby = lobby;

        for (Language language : Language.values()) {
            this.messages.put(language, new HashMap<>());

            Properties properties = new Properties();
            InputStream stream = Lobby.class.getClassLoader().getResourceAsStream("lang_" + language.getLang() + ".properties");
            properties.load(new InputStreamReader(stream, StandardCharsets.UTF_8));
            this.lobby.getLogger().info("Loading language file: " + "lang_" + language.getLang() + ".properties");
            properties.forEach((key, message) -> this.messages.get(language).put((String) key, (String) message));
        }
    }

    private String getMessage(Language language, String messageKey) {
        return this.messages.get(language).get(messageKey);
    }

    public void sendMessageToPlayer(Player player, String messageKey, TagResolver... placeholders) {
        Language cached = this.languageCache.getIfPresent(player.getUniqueId());
        if (cached != null) {
            player.sendMessage(this.lobby.getMiniMessage().deserialize(getMessage(cached, messageKey), placeholders));
            return;
        }

        this.lobby.getDatabase().getFirstRowAsync("SELECT * FROM languages WHERE minecraftUUID = ?", player)
                .thenAccept(dbRow -> {
                    Language language;
                    String selectedLanguage = dbRow.getString("language");

                    if (selectedLanguage != null) {
                        try {
                            language = Language.valueOf(selectedLanguage);
                            this.languageCache.put(player.getUniqueId(), language);
                        } catch (IllegalArgumentException e) {
                            language = Language.ENGLISH;
                        }

                        player.sendMessage(this.lobby.getMiniMessage().deserialize(getMessage(language, messageKey), placeholders));
                    }
                });
    }

    public Language getLanguage(Player player) {
        Language language = this.languageCache.getIfPresent(player.getUniqueId());
        if(language == null) {
            language = Language.ENGLISH;
        }
        return language;
    }

    public void setLanguage(Language language, Player player) {
        try {
            DbRow row = this.lobby.getDatabase().getFirstRow("SELECT * FROM languages WHERE minecraftUUID = ?", player.getUniqueId().toString());
            if (row == null) {
                this.lobby.getDatabase().executeUpdateAsync("INSERT INTO languages (minecraftUUID, language) VALUES (?, ?)", player.getUniqueId().toString(), language.getLang()).thenAccept(integer -> {
                    this.languageCache.put(player.getUniqueId(), language);
                    this.sendMessageToPlayer(player, "languageChanged");
                });
            } else {
                this.lobby.getDatabase().executeUpdateAsync("UPDATE languages SET language = ? WHERE minecraftUUID = ?", language.getLang(), player.getUniqueId().toString()).thenAccept(integer -> {
                    this.languageCache.put(player.getUniqueId(), language);
                    this.sendMessageToPlayer(player, "languageChanged");
                });
            }


        } catch (SQLException exception) {
            exception.printStackTrace();
        }


    }
}
