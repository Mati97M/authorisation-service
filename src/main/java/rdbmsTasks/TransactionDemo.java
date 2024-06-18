package rdbmsTasks;

import com.gridhub.utilities.ConnectionProperties;
import com.gridhub.utilities.RepositoryConnection;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class TransactionDemo {
    public static void main(String[] args) {
        RepositoryConnection repositoryConnection = null;
        Connection connection = null;
        try {
            repositoryConnection = new RepositoryConnection(ConnectionProperties.POSTGRES);
            connection = repositoryConnection.getConnection();

            log.info("demoWithTransactions");
            demoWithTransactions(connection);
            if(!logDbState(connection)) {
                revertChanges(connection);
            }
            log.info("demoNoTransactions");
            demoNoTransactions(connection);
            if(!logDbState(connection)) {
                revertChanges(connection);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        } finally {
            try {
                if (repositoryConnection != null) {
                    repositoryConnection.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                log.error(ex.getMessage(), ex);
            }

        }
    }

    private static void revertChanges(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
             statement.execute("DELETE FROM resources WHERE endpointPath = '/api/test' AND serviceName = 'Test Microservice';");
        }
    }

    private static boolean logDbState(Connection connection) throws SQLException {
        if (!checkTheStateOfDB(connection)) {
            log.error("Some changes on resources table detected - db is not clean");
            log.info("-".repeat(20).concat("\n"));
            return false;
        } else {
            log.info("No changes on resources table detected - db is clean");
            log.info("-".repeat(20).concat("\n"));
            return true;
        }
    }

    private static boolean checkTheStateOfDB(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT id FROM resources WHERE endpointPath = '/api/test' AND serviceName = 'Test Microservice';")) {
            return !resultSet.next();
        }
    }

    public static void demoNoTransactions(Connection connection) {
        doTransaction(true, connection);
    }

    public static void demoWithTransactions(Connection connection) {
        doTransaction(false, connection);
    }

    private static void doTransaction(boolean autoCommit, final Connection connection) {
        try {
            connection.setAutoCommit(autoCommit);
            insertResource(connection);
            log.info("Some error occurred!");
            throw new IllegalStateException();

        } catch (SQLException | IllegalStateException e) {
            try {
                connection.rollback();
                log.info("Rollback");
            } catch (SQLException ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }

    private static void insertResource(Connection connection) throws SQLException {
        String insertResourceSQL = "INSERT INTO resources (endpointPath, serviceName, userSpecificId) VALUES ('/api/test', 'Test Microservice', 1);";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(insertResourceSQL);
            log.info("Inserted Resource successfully");
        }
    }

}