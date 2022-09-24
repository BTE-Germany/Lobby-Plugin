package dev.nachwahl.lobby.storage;

import co.aikar.idb.BukkitDB;
import dev.nachwahl.lobby.Lobby;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

public class Database {

    private final Lobby lobby;
    @Getter
    private co.aikar.idb.Database database;

    public Database(Lobby lobby) {
        this.lobby = lobby;
    }

    public void connect() {
        FileConfiguration config = this.lobby.getConfig();
        this.database = BukkitDB.createHikariDatabase(this.lobby, config.getString("mysql.username"), config.getString("mysql.password"), config.getString("mysql.database"), config.getString("mysql.host") + ":" + config.getString("mysql.port"));
    }


    public void disconnect() {
        if (this.database != null) {
            this.database.close();
        }
    }
}
