package com.gridhub.repositories.dao;

import com.gridhub.enums.Role;
import com.gridhub.mappers.databaseMappers.DatabaseMapper;
import com.gridhub.models.Resource;
import com.gridhub.repositories.Repository;
import com.gridhub.utilities.ConnectionProperties;
import com.gridhub.utilities.RepositoryConnection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class RepositoryDAO implements Repository {
    private static RepositoryDAO instance;
    private RepositoryConnection repositoryConnection;

    public static RepositoryDAO getInstance() throws SQLException {
        if (instance == null) {
            instance = new RepositoryDAO(new RepositoryConnection(ConnectionProperties.POSTGRES));
        }
        return instance;
    }

    public void saveResource(Resource resource) {
        if (resource == null) {
            return;
        }
        Resource foundResource = repositoryConnection.findOne(
                "SELECT * FROM resources WHERE endpointPath = ? AND serviceName = ?",
                DatabaseMapper.mapToResource(),
                resource.getEndpointPath(),
                resource.getServiceName()
        );
        if (foundResource != null) {
            return;
        }
        repositoryConnection.execute(
                "INSERT INTO resources VALUES ?, ?, ?",
                resource.getEndpointPath(),
                resource.getServiceName(),
                resource.getUserSpecificId()
        );
    }

    public void deleteResource(String serviceName, String endpointPath) {
        String deleteSQL = "DELETE FROM resources WHERE serviceName = ? AND endpointPath = ?";
        repositoryConnection.execute(
                deleteSQL,
                preparedStatement -> {
                    try {
                        preparedStatement.setString(1, serviceName);
                        preparedStatement.setString(2, endpointPath);
                    } catch (SQLException e) {
                        throw new InvalidParameterException("Unable to delete resource");
                    }
                });
    }

    public Optional<Resource> findResource(String serviceName, String endpointPath) {
        return Optional.ofNullable(repositoryConnection.findOne(
                "SELECT * FROM resources WHERE serviceName = ? AND endpointPath = ?",
                DatabaseMapper.mapToResource(),
                serviceName,
                endpointPath)
        );
    }

    public List<Resource> findAllResources() {
        return repositoryConnection.findMany(
                "SELECT * FROM resources r JOIN resources_roles rr ON r.id = rr.resource_id",
                DatabaseMapper.mapToResource()
        );
    }

    public List<Resource> findResourceByServiceName(String serviceName) {
        return repositoryConnection.findMany(
                "SELECT * FROM resources r JOIN resources_roles rr ON r.id = rr.resource_id WHERE r.serviceName = ?",
                DatabaseMapper.mapToResource(),
                serviceName
        );
    }

    public void updateResource(Resource resource) {
        String serviceName = resource.getServiceName();
        String endpointPath = resource.getEndpointPath();
        Long userSpecificId = resource.getUserSpecificId();
        Resource existingResource = repositoryConnection.findOne(
                "SELECT * FROM resources WHERE serviceName = ? AND endpointPath = ?",
                DatabaseMapper.mapToResource(),
                serviceName,
                endpointPath
        );
        if (existingResource == null) {
            return;
        }
        repositoryConnection.execute("UPDATE resources SET userSpecificId = ? WHERE serviceName = ? AND endpointPath = ?", userSpecificId, serviceName, endpointPath);
    }

    public List<Role> findAllRoles() {
        return repositoryConnection.findMany(
                "SELECT * FROM roles",
                DatabaseMapper.mapToRoleFromString()
        );
    }
}