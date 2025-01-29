package org.betonquest.betonquest.database;

import com.zaxxer.hikari.HikariDataSource;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import java.util.SortedMap;

/**
 * Abstract Database class, serves as a base for any connection method (MySQL,
 * SQLite, etc.)
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.AvoidDuplicateLiterals"})
public abstract class Database {
    protected final Plugin plugin;

    protected final String prefix;

    protected final String profileInitialName;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    @Nullable
    protected HikariDataSource dataSource;

    @SuppressWarnings("PMD.AvoidUsingVolatile")
    private volatile boolean shuttingDown;

    protected Database(final BetonQuestLogger log, final BetonQuest plugin) {
        this.log = log;
        this.plugin = plugin;
        this.prefix = plugin.getPluginConfig().getString("mysql.prefix", "");
        this.profileInitialName = plugin.getPluginConfig().getString("profiles.initial_name", "");
    }

    public Connection getConnection() throws SQLException {
        if (shuttingDown) {
            throw new SQLException("Database is shutting down.");
        }
        if (dataSource == null) {
            throw new SQLException("DataSource is not initialized.");
        }
        return dataSource.getConnection();
    }

    public void closeConnection() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public final void createTables() {
        try (Connection connection = getConnection()) {
            final SortedMap<MigrationKey, DatabaseUpdate> migrations = getMigrations();
            final Set<MigrationKey> executedMigrations = queryExecutedMigrations(connection);
            executedMigrations.forEach(migrations::remove);

            while (!migrations.isEmpty()) {
                final MigrationKey key = migrations.firstKey();
                final DatabaseUpdate migration = migrations.remove(key);
                migration.executeUpdate(connection);
                markMigrationExecuted(connection, key);
            }
        } catch (final SQLException sqlException) {
            log.error("There was an exception with SQL while creating the database tables!", sqlException);
        }
    }

    /**
     * Returns Database is shutting down.
     *
     * @return true if the database is shutting down, false otherwise
     */
    public boolean isShuttingDown() {
        return shuttingDown;
    }

    public void setShuttingDown(final boolean shuttingDown) {
        this.shuttingDown = shuttingDown;
    }

    /**
     * Returns a SortedMap of all migrations with an identifier as {@link MigrationKey} and the migration function as
     * Value.
     *
     * @return the SortedMap of all migrations
     */
    protected abstract SortedMap<MigrationKey, DatabaseUpdate> getMigrations();

    /**
     * Queries the database for all migrations that have been executed. The function have to ensure that the table
     * containing the executed migrations exists.
     *
     * @param connection the connection to the database
     * @return a set of all migrations, in form of {@link MigrationKey}, that have been executed
     * @throws SQLException if something went wrong with the query
     */
    protected abstract Set<MigrationKey> queryExecutedMigrations(Connection connection) throws SQLException;

    /**
     * Marks the migration as executed in the database to have been executed.
     *
     * @param connection   the connection to the database
     * @param migrationKey the specific migration to mark as executed
     * @throws SQLException if the migration could not be marked as executed
     */
    protected abstract void markMigrationExecuted(Connection connection, MigrationKey migrationKey) throws SQLException;
}
