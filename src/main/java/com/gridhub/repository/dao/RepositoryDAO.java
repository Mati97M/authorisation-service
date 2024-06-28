package com.gridhub.repository.dao;

import com.gridhub.enums.Role;
import com.gridhub.mappers.databaseMappers.DatabaseMapper;
import com.gridhub.models.Resource;
import com.gridhub.repository.Repository;
import com.gridhub.utilities.RepositoryConnection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component("RepositoryDAO")
@RequiredArgsConstructor
public class RepositoryDAO implements Repository {
    private final RepositoryConnection repositoryConnection;

    public void saveResource(Resource resource) {
        if (resource == null) {
            return;
        }
        Resource foundResource = repositoryConnection.findOne(
                "SELECT * FROM resources WHERE endpoint_path = ? AND service_name = ?",
                DatabaseMapper.mapToResource(),
                resource.getEndpointPath(),
                resource.getServiceName()
        );
        if (foundResource != null) {
            return;
        }
        repositoryConnection.execute(
                "INSERT INTO resources (user_specific_id, endpoint_path, service_name) VALUES (?, ?, ?)",
                resource.getUserSpecificId(),
                resource.getEndpointPath(),
                resource.getServiceName()
        );
    }

    public void deleteResource(String serviceName, String endpointPath) {
        String deleteSQL = "DELETE FROM resources WHERE service_name = ? AND endpoint_path = ?";
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
                "SELECT r.id, r.endpoint_path, r.service_name, r.user_specific_id, rr.role " +
                        "from resources r " +
                        "join resources_roles rr " +
                        "on r.id = rr.resource_id " +
                        "WHERE service_name = ? AND endpoint_path = ?;",
                DatabaseMapper.mapToResource(),
                serviceName,
                endpointPath)
        );
    }

    public List<Resource> findAllResources() {
        return repositoryConnection.findMany(
                "SELECT r.id, r.endpoint_path, r.service_name, r.user_specific_id, rr.role " +
                        "from resources r " +
                        "join resources_roles rr " +
                        "on r.id = rr.resource_id",
                DatabaseMapper.mapToResource()
        );
    }

    public List<Resource> findResourceByServiceName(String serviceName) {
        return repositoryConnection.findMany(
                "SELECT r.id, r.endpoint_path, r.service_name, r.user_specific_id, rr.role " +
                        "from resources r " +
                        "join resources_roles rr " +
                        "on r.id = rr.resource_id " +
                        "WHERE service_name = ?",
                DatabaseMapper.mapToResource(),
                serviceName
        );
    }

    public void updateResource(Resource resource) {
        String serviceName = resource.getServiceName();
        String endpointPath = resource.getEndpointPath();
        Long userSpecificId = resource.getUserSpecificId();
        Resource existingResource = repositoryConnection.findOne(
                "SELECT * FROM resources WHERE service_name = ? AND endpoint_path = ?",
                DatabaseMapper.mapToResource(),
                serviceName,
                endpointPath
        );
        if (existingResource == null) {
            return;
        }
        repositoryConnection.execute("UPDATE resources SET user_specific_id = ? WHERE service_name = ? AND endpoint_path = ?", userSpecificId, serviceName, endpointPath);
    }

    public List<Role> findAllRoles() {
        return repositoryConnection.findMany(
                "SELECT * FROM roles",
                DatabaseMapper.mapToRoleFromString()
        );
    }
}