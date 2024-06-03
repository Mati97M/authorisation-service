package com.gridhub.services;

import com.gridhub.dtos.ResourceInfo;
import com.gridhub.dtos.UserInfo;
import com.gridhub.enums.Role;
import com.gridhub.exceptions.EndpointPathDuplicatException;
import com.gridhub.exceptions.EntityNotFoundException;
import com.gridhub.exceptions.ForbiddenAccessException;
import com.gridhub.exceptions.RepositoryInitializationException;
import com.gridhub.models.Resource;
import com.gridhub.repositories.Repository;
import com.gridhub.repositories.dao.RepositoryDAO;
import lombok.Setter;

import java.sql.SQLException;
import java.util.List;

public class AuthorizationService {
    @Setter
    private Repository repository;
    private static AuthorizationService instance;

    public static AuthorizationService getInstance() {
        if (instance == null) {
            try {
                instance = new AuthorizationService(RepositoryDAO.getInstance());
            } catch (SQLException e) {
                throw new RepositoryInitializationException();
            }
        }
        return instance;
    }

    private AuthorizationService(Repository repository) {
        this.repository = repository;
    }

    public boolean hasPermissionToAccessResource(UserInfo userInfo, ResourceInfo resourceInfo) {
        Resource requestedResource = repository.findResource(resourceInfo.serviceName(), resourceInfo.endpointPath())
                .orElseThrow(EntityNotFoundException::new);
        List<Role> requestedResourceRoles = requestedResource.getRoles();

        if (userInfo.role().equals(Role.ADMIN) || requestedResourceRoles.contains(Role.NOT_RESTRICTED)) {
            return true;
        }
        if (requestedResourceRoles.contains(Role.USER_SPECIFIC)) {
            return userInfo.userSpecificId().equals(requestedResource.getUserSpecificId());
        }

        return requestedResourceRoles.contains(userInfo.role());
    }

    public void registerResource(UserInfo userInfo, Resource resource) {
        if (!userInfo.role().equals(Role.ADMIN)) {
            throw new ForbiddenAccessException();
        } else {
            repository.findResource(resource.getServiceName(), resource.getEndpointPath())
                    .ifPresentOrElse(
                            alreadyExistingResource -> {
                                throw new EndpointPathDuplicatException();
                            },
                            () -> repository.saveResource(resource)
                    );
        }
    }

    public void unregisterResource(UserInfo userInfo, ResourceInfo resourceInfo) {
        if (!userInfo.role().equals(Role.ADMIN)) {
            throw new ForbiddenAccessException();
        } else {
            repository.findResource(resourceInfo.serviceName(), resourceInfo.endpointPath())
                    .ifPresentOrElse(
                            alreadyExistingResource -> {
                                repository.deleteResource(resourceInfo.serviceName());
                            },
                            () -> {
                                throw new EntityNotFoundException();
                            }
                    );
        }
    }
}