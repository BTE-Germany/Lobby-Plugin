package dev.nachwahl.lobby.leaderboards;

import dev.nachwahl.lobby.Lobby;
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
    private Lobby instance;

    public void update() {
        load();
    }

    public Leaderboard(Lobby lobby, String id) throws SQLException {
        this.instance = lobby;
        this.id = id;
        this.location = instance.getLocationAPI().getLocation(id);
        instance.getHologramAPI().addHologram(id, new dev.nachwahl.lobby.hologram.Hologram(location, new ArrayList<>(), new ArrayList<>(), id));

        load();
    }

    public void load() {
    }

    public dev.nachwahl.lobby.hologram.Hologram getHologram() {
        return instance.getHologramAPI().getHologram(id);
    }

}
