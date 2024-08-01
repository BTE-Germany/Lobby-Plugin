package dev.nachwahl.lobby.leaderboards;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.hologram.Hologram;
import lombok.Getter;
import me.filoghost.holographicdisplays.api.Position;
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

    ;

    public Leaderboard(Lobby lobby, String id) throws SQLException {
        this.instance = lobby;
        this.id = id;
        this.location = instance.getLocationAPI().getLocation(id);
        System.out.println("loc " + location.toString());
        instance.getHologramAPI().addHologram(id, new Hologram(Position.of(location), new ArrayList<>(), new ArrayList<>()));

        load();
    }

    public void load() {
    }

    public Hologram getHologram() {
        return instance.getHologramAPI().getHologram(id);
    }

}
