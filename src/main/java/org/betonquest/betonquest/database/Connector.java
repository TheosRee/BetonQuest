package org.betonquest.betonquest.database;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Connects to the database and queries it.
 */
public final class Connector {

    /**
     * The connector instance.
     */
    private static final Connector INSTANCE = new Connector();

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Table prefix.
     */
    private final String prefix;

    /**
     * Database connection management.
     */
    private final Database database;

    /**
     * Opens a new connection to the database.
     */
    private Connector() {
        final BetonQuest plugin = BetonQuest.getInstance();
        this.log = plugin.getLoggerFactory().create(Connector.class);
        this.prefix = plugin.getPluginConfig().getString("mysql.prefix", "");
        this.database = plugin.getDB();
    }

    /**
     * Returns the singleton instance of the Connector.
     *
     * @return the single instance of Connector
     */
    public static Connector getInstance() {
        return INSTANCE;
    }

    /**
     * Queries the database with the given type and arguments.
     *
     * @param type type of the query
     * @param args arguments
     * @return ResultSet with the requested data
     */
    public QueryResult querySQL(final QueryType type, final String... args) {
        return querySQL(type, statement -> {
            for (int i = 0; i < args.length; i++) {
                statement.setString(i + 1, args[i]);
            }
        });
    }

    /**
     * Queries the database with the given type and arguments.
     *
     * @param type             type of the query
     * @param variableResolver resolver for variables in prepared statements
     * @return ResultSet with the requested data
     */
    @SuppressWarnings("PMD.CloseResource")
    @SuppressFBWarnings({"ODR_OPEN_DATABASE_RESOURCE", "OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE"})
    public QueryResult querySQL(final QueryType type, final VariableResolver variableResolver) {
        final String sql = type.createSql(prefix);
        try {
            final Connection connection = database.getConnection();
            final PreparedStatement statement = connection.prepareStatement(sql);
            variableResolver.resolve(statement);
            final ResultSet resultSet = statement.executeQuery();
            return new QueryResult(connection, statement, resultSet);
        } catch (final SQLException e) {
            throw new IllegalStateException("There was an exception with SQL", e);
        }
    }

    /**
     * Updates the database with the given type and arguments.
     *
     * @param type type of the update
     * @param args arguments
     */
    public void updateSQL(final UpdateType type, final String... args) {
        final String sql = type.createSql(prefix);
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) {
                statement.setString(i + 1, args[i]);
            }
            statement.executeUpdate();
        } catch (final SQLException e) {
            log.error("There was an exception with SQL", e);
        }
    }

    /**
     * Get the prefix.
     *
     * @return the used prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Get the database.
     *
     * @return the used database
     */
    public Database getDatabase() {
        return database;
    }

    /**
     * Resolver for variables in prepared statements.
     */
    @FunctionalInterface
    public interface VariableResolver {
        /**
         * Resolves the variables in the prepared statement.
         *
         * @param statement the statement to resolve
         * @throws SQLException if there is an error resolving the variables
         */
        void resolve(PreparedStatement statement) throws SQLException;
    }
}
