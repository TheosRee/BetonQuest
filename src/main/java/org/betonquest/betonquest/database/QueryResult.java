package org.betonquest.betonquest.database;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("PMD.CommentRequired")
public class QueryResult implements AutoCloseable {

    private final BetonQuestLogger log = BetonQuest.getInstance().getLoggerFactory().create(getClass());

    private final Connection connection;

    private final PreparedStatement statement;

    private final ResultSet resultSet;

    public QueryResult(final Connection connection, final PreparedStatement statement, final ResultSet resultSet) {
        this.connection = connection;
        this.statement = statement;
        this.resultSet = resultSet;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    @Override
    public void close() throws SQLException {
        closeQuietly(resultSet);
        closeQuietly(statement);
        closeQuietly(connection);
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private void closeQuietly(final AutoCloseable closeable) {
        try {
            closeable.close();
        } catch (final Exception ignored) {
            log.debug("Failed to close resource: " + closeable);
        }
    }
}
