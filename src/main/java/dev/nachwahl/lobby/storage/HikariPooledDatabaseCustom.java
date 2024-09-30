package dev.nachwahl.lobby.storage;

import co.aikar.idb.BaseDatabase;
import co.aikar.idb.DatabaseOptions;
import co.aikar.idb.PooledDatabaseOptions;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class HikariPooledDatabaseCustom extends BaseDatabase {

    DataSource dataSource;
    @Getter
    private final PooledDatabaseOptions poolOptions;

    public HikariPooledDatabaseCustom(PooledDatabaseOptions poolOptions) {
        super(poolOptions.getOptions());
        this.poolOptions = poolOptions;
        DatabaseOptions options = poolOptions.getOptions();

        HikariConfig config = new HikariConfig();
        config.setPoolName(options.getPoolName());
        if (options.getDataSourceClassName() != null) {
            config.setDataSourceClassName(options.getDataSourceClassName());
        }
        config.addDataSourceProperty("url", "jdbc:" + options.getDsn());

        if (options.getUser() != null) {
            config.addDataSourceProperty("user", options.getUser());
        }
        if (options.getPass() != null) {
            config.addDataSourceProperty("password", options.getPass());
        }

        if (options.isUseOptimizations() && options.getDsn().startsWith("mysql")) {
            //config.addDataSourceProperty("cachePrepStmts", true);
            //config.addDataSourceProperty("prepStmtCacheSize", 250);
            //config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
            //config.addDataSourceProperty("useServerPrepStmts", true);
            //config.addDataSourceProperty("cacheCallableStmts", true);
            //config.addDataSourceProperty("cacheResultSetMetadata", true);
            //config.addDataSourceProperty("cacheServerConfiguration", true);
            //config.addDataSourceProperty("useLocalSessionState", true);
            //config.addDataSourceProperty("elideSetAutoCommits", true);
            //config.addDataSourceProperty("alwaysSendSetIsolation", false);
        }
        if (poolOptions.getDataSourceProperties() != null) {
            for (Map.Entry<String, Object> entry : poolOptions.getDataSourceProperties().entrySet()) {
                config.addDataSourceProperty(entry.getKey(), entry.getValue());
            }
        }

        config.setConnectionTestQuery("SELECT 1");
        config.setMinimumIdle(poolOptions.getMinIdleConnections());
        config.setMaximumPoolSize(poolOptions.getMaxConnections());
        config.setTransactionIsolation(options.getDefaultIsolationLevel());

        this.dataSource = new HikariDataSource(config);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.dataSource != null ? this.dataSource.getConnection() : null;
    }

}