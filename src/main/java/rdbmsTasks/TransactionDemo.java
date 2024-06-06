package rdbmsTasks;

import com.gridhub.enums.Role;
import com.gridhub.utilities.ConnectionProperties;
import com.gridhub.utilities.RepositoryConnection;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class TransactionDemo {
    public static void main(String[] args) {
        try (RepositoryConnection repositoryConnection = new RepositoryConnection(ConnectionProperties.POSTGRES)) {
            demoWithTransactions(repositoryConnection.getConnection());
            demoNoTransactions(repositoryConnection.getConnection());
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void demoNoTransactions(Connection connection) {
        try {
            connection.setAutoCommit(true);
            insertResource(connection);
            //never does: updateResources_roles(connection);

        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void demoWithTransactions(Connection connection) {
        try {
            connection.setAutoCommit(false);
            insertResource(connection);
            updateResources_roles(connection);
//            never commits connection.commit();

        } catch (SQLException | IllegalStateException e) {
            try {
                log.error(e.getMessage(), e);
                connection.rollback();
            } catch (SQLException ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }

    private static void updateResources_roles(Connection connection) throws SQLException {
        String insertRoleADMIN = "INSERT INTO resources_roles VALUES ((SELECT id resources FROM resources WHERE endpointPath = ? AND serviceName = ?));";
        try (PreparedStatement preparedStatementADMIN = connection.prepareStatement(insertRoleADMIN)) {
            preparedStatementADMIN.setString(1, "/api/test");
            preparedStatementADMIN.setString(2, "Test Microservice");
            preparedStatementADMIN.executeUpdate();
        }

        String insertRoleUSER_SPECIFIC = "INSERT INTO resources_roles VALUES ((SELECT id resources FROM resources WHERE endpointPath = ? AND serviceName = ?), ?);";
        try (PreparedStatement preparedStatementUSER_SPECIFIC = connection.prepareStatement(insertRoleUSER_SPECIFIC)) {
            preparedStatementUSER_SPECIFIC.setString(1, "/api/test");
            preparedStatementUSER_SPECIFIC.setString(2, "Test Microservice");
            preparedStatementUSER_SPECIFIC.setString(3, Role.USER_SPECIFIC.toString());
            preparedStatementUSER_SPECIFIC.executeUpdate();
        }
    }

    private static void insertResource(Connection connection) throws SQLException {
        String insertResourceSQL = "INSERT INTO resources (endpointPath, serviceName, userSpecificId) VALUES ('/api/test', 'Test Microservice', 1);";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(insertResourceSQL);
        }
    }

}