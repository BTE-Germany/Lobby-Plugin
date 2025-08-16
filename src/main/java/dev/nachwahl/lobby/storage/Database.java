package dev.nachwahl.lobby.storage;

import co.aikar.idb.BukkitDB;
import co.aikar.idb.DB;
import co.aikar.idb.DatabaseOptions;
import co.aikar.idb.PooledDatabaseOptions;
import co.aikar.idb.DbRow;
import dev.nachwahl.lobby.Lobby;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.SQLException;
import java.util.List;

public class Database {

    private final Lobby lobby;
    @Getter
    private co.aikar.idb.Database database;

    public Database(Lobby lobby) {
        this.lobby = lobby;
    }

    public void connect() {
        FileConfiguration config = this.lobby.getConfig();
        //this.database = BukkitDB.createHikariDatabase(this.lobby, config.getString("mysql.username"), config.getString("mysql.password"), config.getString("mysql.database"), config.getString("mysql.host") + ":" + config.getString("mysql.port"));

        DatabaseOptions options = DatabaseOptions
                .builder()
                .poolName(this.lobby.getDescription().getName() + " DB")
                .logger(this.lobby.getLogger())
                .mysql(config.getString("mysql.username"), config.getString("mysql.password"), config.getString("mysql.database"), config.getString("mysql.host") + ":" + config.getString("mysql.port"))
                .build();
        PooledDatabaseOptions poolOptions = PooledDatabaseOptions
                .builder()
                .options(options)
                .build();

        this.database = new HikariPooledDatabaseCustom(poolOptions);
        DB.setGlobalDatabase(this.database);
        
        // Initialize and migrate database schema if needed
        try {
            initializeBOTMTable();
        } catch (SQLException e) {
            this.lobby.getLogger().severe("Failed to initialize BOTM database: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void disconnect() {
        if (this.database != null) {
            this.database.close();
        }
    }

    /**
     * Initializes and migrates the BOTM table schema if necessary.
     * Handles migration from old schema (uuid, score) to new schema (name, year, month, player1_uuid, player2_uuid, player3_uuid).
     */
    private void initializeBOTMTable() throws SQLException {
        // Check if botm table exists
        boolean tableExists = checkTableExists("botm");
        
        if (!tableExists) {
            // Create new table with current schema
            createNewBOTMTable();
            this.lobby.getLogger().info("Created new BOTM table with current schema.");
            return;
        }

        // Table exists, check if it has old or new structure
        boolean hasOldStructure = checkOldBOTMStructure();
        
        if (hasOldStructure) {
            // Migrate from old structure to new structure
            migrateBOTMTable();
            this.lobby.getLogger().info("Successfully migrated BOTM table from old schema to new schema.");
        } else {
            // Check if current structure is complete, add missing columns if needed
            ensureNewBOTMStructure();
            this.lobby.getLogger().info("BOTM table schema is up to date.");
        }
    }

    /**
     * Checks if a table exists in the database.
     */
    private boolean checkTableExists(String tableName) throws SQLException {
        List<DbRow> tables = this.database.getResults("SHOW TABLES LIKE ?", tableName);
        return !tables.isEmpty();
    }

    /**
     * Checks if the BOTM table has the old structure (uuid, score columns).
     */
    private boolean checkOldBOTMStructure() throws SQLException {
        try {
            List<DbRow> columns = this.database.getResults("SHOW COLUMNS FROM botm LIKE 'uuid'");
            boolean hasUuidColumn = !columns.isEmpty();
            
            List<DbRow> scoreColumns = this.database.getResults("SHOW COLUMNS FROM botm LIKE 'score'");
            boolean hasScoreColumn = !scoreColumns.isEmpty();
            
            return hasUuidColumn || hasScoreColumn;
        } catch (SQLException e) {
            // If we can't check columns, assume it's not old structure
            return false;
        }
    }

    /**
     * Creates a new BOTM table with the current schema.
     */
    private void createNewBOTMTable() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS botm (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "year INT NOT NULL, " +
                "month INT NOT NULL, " +
                "player1_uuid VARCHAR(36) NOT NULL, " +
                "player2_uuid VARCHAR(36) NULL, " +
                "player3_uuid VARCHAR(36) NULL, " +
                "UNIQUE KEY unique_month_year (year, month)" +
                ")";
        
        this.database.executeUpdate(createTableSQL);
    }

    /**
     * Migrates the BOTM table from old structure to new structure.
     */
    private void migrateBOTMTable() throws SQLException {
        // Create backup table with old data
        this.database.executeUpdate("CREATE TABLE IF NOT EXISTS botm_backup AS SELECT * FROM botm");
        
        // Drop the old table
        this.database.executeUpdate("DROP TABLE botm");
        
        // Create new table with current schema
        createNewBOTMTable();
        
        this.lobby.getLogger().info("Old BOTM data has been backed up to 'botm_backup' table. " +
                "The old simple scoring system has been replaced with the new monthly BOTM system. " +
                "Manual data migration may be required if you want to preserve historical data.");
    }

    /**
     * Ensures the BOTM table has all required columns for the new structure.
     */
    private void ensureNewBOTMStructure() throws SQLException {
        // Check and add missing columns
        addColumnIfNotExists("botm", "name", "VARCHAR(255) NOT NULL DEFAULT 'Unknown'");
        addColumnIfNotExists("botm", "year", "INT NOT NULL DEFAULT 0");
        addColumnIfNotExists("botm", "month", "INT NOT NULL DEFAULT 1");
        addColumnIfNotExists("botm", "player1_uuid", "VARCHAR(36) NOT NULL DEFAULT ''");
        addColumnIfNotExists("botm", "player2_uuid", "VARCHAR(36) NULL");
        addColumnIfNotExists("botm", "player3_uuid", "VARCHAR(36) NULL");
        
        // Add unique constraint if it doesn't exist
        try {
            this.database.executeUpdate("ALTER TABLE botm ADD UNIQUE KEY unique_month_year (year, month)");
        } catch (SQLException e) {
            // Constraint might already exist, ignore error
        }
    }

    /**
     * Adds a column to a table if it doesn't already exist.
     */
    private void addColumnIfNotExists(String tableName, String columnName, String columnDefinition) throws SQLException {
        List<DbRow> columns = this.database.getResults("SHOW COLUMNS FROM " + tableName + " LIKE ?", columnName);
        if (columns.isEmpty()) {
            this.database.executeUpdate("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnDefinition);
            this.lobby.getLogger().info("Added column '" + columnName + "' to table '" + tableName + "'");
        }
    }
}
