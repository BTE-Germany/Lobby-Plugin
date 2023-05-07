package dev.nachwahl.lobby.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.nachwahl.lobby.Lobby;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;


class UserSetting {
    public UUID uuid;
    public String key;

    public UserSetting(UUID uuid, String key) {
        this.uuid = uuid;
        this.key = key;
    }
}

public class UserSettingsAPI {

    private final Cache<UserSetting, String> settingsCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();
    private Lobby lobby;

    public UserSettingsAPI(Lobby lobby) {
        this.lobby = lobby;
    }

    public void setSetting(Player player, String key, String value, Consumer<Integer> callback) {

        if (settingsCache.getIfPresent(new UserSetting(player.getUniqueId(), key)) != null) {
            settingsCache.invalidate(new UserSetting(player.getUniqueId(), key));
        }

        settingsCache.put(new UserSetting(player.getUniqueId(), key), value);

        this.lobby.getDatabase().getFirstRowAsync("SELECT * FROM playersettings WHERE uuid = ? AND `key` = ?", player.getUniqueId().toString(), key).thenAccept(dbRow -> {
            if (dbRow == null) {
                this.lobby.getDatabase().executeUpdateAsync("INSERT INTO playersettings (uuid, `key`, value) VALUES (?, ?, ?)", player.getUniqueId().toString(), key, value).exceptionally((e) -> {
                    this.lobby.getLogger().log(Level.SEVERE, e.getMessage());
                    return null;
                }).thenAccept(callback);
            } else {
                this.lobby.getDatabase().executeUpdateAsync("UPDATE playersettings SET value = ? WHERE uuid = ? AND `key` = ?", value, player.getUniqueId().toString(), key).exceptionally((e) -> {
                    this.lobby.getLogger().log(Level.SEVERE, e.getMessage());
                    return null;
                }).thenAccept(callback);
            }
        }).exceptionally((e) -> {
            this.lobby.getLogger().log(Level.SEVERE, e.getMessage());
            return null;
        });
    }

    public void setSettingIfNotExistant(Player player, String key, String value, Consumer<Integer> callback) {
        getSetting(player,key,(r) -> {
            if(r==null) {
                setSetting(player,key,value,callback);
            } else {
                callback.accept(null);
            }
        });
    }

    public void getSetting(Player player, String key, Consumer<String> callback) {
        try {
            String cache = settingsCache.getIfPresent(new UserSetting(player.getUniqueId(), key));

            if (cache == null) {
                this.lobby.getDatabase().getFirstRowAsync("SELECT * FROM playersettings WHERE uuid = ? AND `key` = ?", player.getUniqueId().toString(), key).thenAccept(dbRow -> {
                    if (dbRow == null) {
                        callback.accept(null);
                    } else {
                        settingsCache.put(new UserSetting(player.getUniqueId(), key), dbRow.getString("value"));
                        callback.accept(dbRow.getString("value"));
                    }
                }).exceptionally((e) -> {
                    this.lobby.getLogger().log(Level.SEVERE, e.getMessage());
                    return null;
                });
            } else {
                callback.accept(cache);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }


    public void toggleSetting(Player player, String key, Consumer<Integer> callback) {
        getSetting(player, key, (value) -> {
            if (Objects.equals(value, "1")) {
                setSetting(player, key, "0", callback);
            } else {
                setSetting(player, key, "1", callback);
            }
        });
    }

    public void getBooleanSetting(Player player, String key, Consumer<Boolean> callback) {
        try {
            getSetting(player, key, (value) -> {
                callback.accept(Objects.equals(value, "1") ? true : false);
            });
        } catch (Exception e) {
            throw e;
        }
    }

    public void setDefaultSettings(Player player) {
        this.setSettingIfNotExistant(player, "playerVisibility", "1", (i) -> {});
        this.setSettingIfNotExistant(player, "realTime", "1", (i) -> {});
        this.setSettingIfNotExistant(player, "playerPickup", "1", (i) -> {});
    }


}
