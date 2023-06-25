package dev.nachwahl.lobby.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.nachwahl.lobby.Lobby;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class LocationAPI {

    private final Cache<String, Location> locationCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();
    private final Lobby lobby;

    public LocationAPI(Lobby lobby) {
        this.lobby = lobby;
    }

    public void setLocation(Location location, String name) {
        locationCache.put(name, location);
        this.lobby.getDatabase().getFirstRowAsync("SELECT * FROM locations WHERE name = ?", name).thenAccept(dbRow -> {
            if (dbRow == null) {
                this.lobby.getDatabase().executeUpdateAsync("INSERT INTO locations (name, location) VALUES (?, ?)", name, stringifyLocation(location));
            } else {
                this.lobby.getDatabase().executeUpdateAsync("UPDATE locations SET location = ? WHERE name = ?", stringifyLocation(location), name);
            }
        });
    }

    public void getLocation(String name, Consumer<Location> callback) {
        Location cache = locationCache.getIfPresent(name);
        if (cache == null) {
            this.lobby.getDatabase().getFirstRowAsync("SELECT * FROM locations WHERE name = ?", name).thenAccept(dbRow -> {
                if (dbRow == null) {
                    callback.accept(new Location(this.lobby.getServer().getWorlds().get(0), 0, 0, 0));
                } else {
                    locationCache.put(name, parseLocation(dbRow.get("location")));
                    callback.accept(parseLocation(dbRow.get("location")));
                }
            });
        } else {
            callback.accept(cache);
        }
    }

    public void teleportToLocation(Player player, String name, Boolean showConfirmMessage) {
        Location cache = locationCache.getIfPresent(name);
        if (cache == null) {
            this.lobby.getDatabase().getFirstRowAsync("SELECT * FROM locations WHERE name = ?", name).thenAccept(dbRow -> {
                if (dbRow == null) {
                    this.lobby.getLanguageAPI().sendMessageToPlayer(player, "location.notFound", Placeholder.parsed("name", name));
                } else {
                    locationCache.put(name, parseLocation(dbRow.getString("location")));
                    Bukkit.getScheduler().runTask(this.lobby, () -> player.teleport(parseLocation(dbRow.getString("location"))));
                    if (showConfirmMessage) {
                        this.lobby.getLanguageAPI().sendMessageToPlayer(player, "location.teleport", Placeholder.parsed("name", name));
                    }
                }
            });
        } else {
            player.teleport(cache);
            if (showConfirmMessage) {
                this.lobby.getLanguageAPI().sendMessageToPlayer(player, "location.teleport", Placeholder.parsed("name", name));
            }

        }
    }

    public String stringifyLocation(Location location) {
        return location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getYaw() + ";" + location.getPitch();
    }

    public Location parseLocation(String location) {
        return parseLocation(location, ";");
    }

    public Location parseLocation(String location, String splitter) {
        String[] splitLocation = location.split(splitter);
        Location loc = new Location(
                this.lobby.getServer().getWorld(splitLocation[0]),
                Double.parseDouble(splitLocation[1]),
                Double.parseDouble(splitLocation[2]),
                Double.parseDouble(splitLocation[3])
        );
        if (splitLocation.length >= 5) {
            loc.setYaw(Float.parseFloat(splitLocation[4]));
            loc.setPitch(Float.parseFloat(splitLocation[5]));
        }

        return loc;
    }


}
