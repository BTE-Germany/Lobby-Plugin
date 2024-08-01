package dev.nachwahl.cosmetics.storage;

import co.aikar.idb.BukkitDB;
import dev.nachwahl.cosmetics.Cosmetics;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

public class Database {

    private final Cosmetics lobby;
    @Getter
    private co.aikar.idb.Database database;

    public Database(Cosmetics lobby) {
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
