package dev.nachwahl.lobby.leaderboards;

import dev.nachwahl.lobby.LobbyPlugin;
import dev.nachwahl.lobby.hologram.Hologram;
import lombok.Getter;
import org.bukkit.Location;

import java.sql.SQLException;
import java.util.ArrayList;

public abstract class Leaderboard {

    @Getter
    private Location location;
    @Getter
    private String id;

    @Getter
    private LobbyPlugin instance;

    public void update() {
        load();
    }

    public Leaderboard(LobbyPlugin lobbyPlugin, String id) throws SQLException {
        this.instance = lobbyPlugin;
        this.id = id;
        this.location = instance.getLocationAPI().getLocation(id);
        instance.getHologramAPI().addHologram(id, new Hologram(location, new ArrayList<>(), new ArrayList<>(), id));

        load();
    }

    public void load() {
    }

    public Hologram getHologram() {
        return instance.getHologramAPI().getHologram(id);
    }

}
