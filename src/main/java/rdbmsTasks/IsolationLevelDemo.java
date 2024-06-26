package rdbmsTasks;

import com.gridhub.utilities.RepositoryConnection;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class IsolationLevelDemo {
    public static void main(String[] args) {
        RepositoryConnection repositoryConnection = new RepositoryConnection(new HikariDataSource());
        try {
            demoWithBadIsolation(repositoryConnection);
            revertChangesOnTable(repositoryConnection);
            demoWithGoodIsolation(repositoryConnection);
            revertChangesOnTable(repositoryConnection);
        } catch (Exception e) {
            revertChangesOnTable(repositoryConnection);
            throw new IllegalStateException(e);
        } finally {
            repositoryConnection.close();
        }
    }

    private static void revertChangesOnTable(RepositoryConnection repositoryConnection) {
        try (Connection connection = repositoryConnection.getConnection()) {
            connection.setAutoCommit(false);

            String deleteSQL = "DELETE FROM resources WHERE endpointpath = '/api/test' AND serviceName = 'Blog Microservice';";

            try (PreparedStatement preparedStatementCount = connection.prepareStatement(deleteSQL)) {
                int rowsAffected = preparedStatementCount.executeUpdate();
                connection.commit();

                if (rowsAffected == 0) {
                    log.info("No rows affected");
                } else {
                    log.info(String.valueOf(rowsAffected).concat(" rows affected"));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void demoWithGoodIsolation(RepositoryConnection repositoryConnection) {
        final int isolationLevel = Connection.TRANSACTION_REPEATABLE_READ;
        runQueries(repositoryConnection, isolationLevel);
    }

    private static void demoWithBadIsolation(RepositoryConnection repositoryConnection) {
        final int isolationLevel = Connection.TRANSACTION_READ_COMMITTED;
        runQueries(repositoryConnection, isolationLevel);
    }

    private static void runQueries(RepositoryConnection repositoryConnection, int isolationLevel) {
        try (Connection connection1 = repositoryConnection.getConnection();
             Connection connection2 = repositoryConnection.getConnection()) {
            connection1.setAutoCommit(false);
            connection2.setAutoCommit(false);
            connection1.setTransactionIsolation(isolationLevel);
            connection2.setTransactionIsolation(isolationLevel);

            String countSQL = "SELECT COUNT(*) AS count FROM resources WHERE serviceName = ?;";
            String insertSQL = "INSERT INTO resources (endpointPath, serviceName, userSpecificId) VALUES ('/api/test', 'Blog Microservice', 4);";

            try (PreparedStatement preparedStatementInsert = connection2.prepareStatement(insertSQL);
                 PreparedStatement preparedStatementCount = connection1.prepareStatement(countSQL)) {
                preparedStatementCount.setString(1, "Blog Microservice");

                ResultSet resultSetFirstCount = preparedStatementCount.executeQuery();
                resultSetFirstCount.next();
                int firstCount = resultSetFirstCount.getInt("count");
                resultSetFirstCount.close();

                preparedStatementInsert.execute();
                connection2.commit();

                ResultSet resultSetSecondCount = preparedStatementCount.executeQuery();
                resultSetSecondCount.next();
                int secondCount = resultSetSecondCount.getInt("count");
                resultSetSecondCount.close();

                if (firstCount != secondCount) {
                    log.error("Inconsistency found");
                } else {
                    log.info("ok");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}