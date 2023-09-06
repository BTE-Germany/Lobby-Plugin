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
    private Hologram hologram;
    @Getter
    private Location location;

    @Getter
    private Lobby instance;

    public void update() {
        load();
    };

    public Leaderboard(Lobby lobby,String loc) throws SQLException {
        this.instance = lobby;
        this.location = instance.getLocationAPI().getLocation(loc);
        hologram = new Hologram(Position.of(location),new ArrayList<>(),new ArrayList<>());

        load();
    }

    public void load() {}

}
