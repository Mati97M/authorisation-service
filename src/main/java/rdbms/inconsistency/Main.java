package rdbms.inconsistency;

import com.gridhub.utilities.ConnectionProperties;
import com.gridhub.utilities.RepositoryConnection;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try (RepositoryConnection repositoryConnection = new RepositoryConnection(ConnectionProperties.POSTGRES)) {

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
