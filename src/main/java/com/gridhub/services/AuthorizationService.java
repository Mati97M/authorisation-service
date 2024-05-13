package com.gridhub.services;

import com.gridhub.dtos.ResourceInfo;
import com.gridhub.dtos.UserInfo;
import com.gridhub.enums.Role;
import com.gridhub.exceptions.EndpointPathDuplicatException;
import com.gridhub.exceptions.EntityNotFoundException;
import com.gridhub.exceptions.ForbiddenAccessException;
import com.gridhub.models.Resource;
import com.gridhub.repositories.ResourceRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorizationService {
    private final ResourceRepository resourceRepository = ResourceRepository.getInstance();
    private static AuthorizationService instance;

    public static AuthorizationService getInstance() {
        if (instance == null) {
            instance = new AuthorizationService();
        }
        return instance;
    }

    public boolean hasPermissionToAccessResource(UserInfo userInfo, ResourceInfo resourceInfo) throws EntityNotFoundException {
        Resource requestedResource = resourceRepository.getResource(resourceInfo.serviceName())
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

    public void registerResource(UserInfo userInfo, Resource resource) throws EndpointPathDuplicatException, ForbiddenAccessException {
        if (!userInfo.role().equals(Role.ADMIN)) {
            throw new ForbiddenAccessException();
        } else {
            resourceRepository.getResource(resource.getServiceName())
                    .ifPresentOrElse(
                            alreadyExistingResource -> {
                                throw new EndpointPathDuplicatException();
                            },
                            () -> resourceRepository.registerResource(resource)
                    );
        }
    }

    public void unregisterResource(UserInfo userInfo, ResourceInfo resourceInfo) throws EntityNotFoundException, ForbiddenAccessException {
        if (!userInfo.role().equals(Role.ADMIN)) {
            throw new ForbiddenAccessException();
        } else {
            resourceRepository.getResource(resourceInfo.serviceName())
                    .ifPresentOrElse(
                            alreadyExistingResource -> {
                                resourceRepository.unregisterResource(resourceInfo.serviceName());
                            },
                            () -> {
                                throw new EntityNotFoundException();
                            }
                    );
        }
    }
}