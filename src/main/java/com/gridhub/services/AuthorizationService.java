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

import java.util.Optional;

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
        Optional<Resource> requestedResource = resourceRepository.getResource(resourceInfo.serviceName());
        if (requestedResource.isEmpty()) {
            throw new EntityNotFoundException();
        }

        if (userInfo.role().equals(Role.ADMIN) || requestedResource.get().getRoles().contains(Role.NOT_RESTRICTED)) {
            return true;
        }
        if (requestedResource.get().getRoles().contains(Role.USER_SPECIFIC)) {
            return userInfo.userSpecificId().equals(requestedResource.get().getUserSpecificId());
        }

        return requestedResource.get().getRoles().contains(userInfo.role());
    }

    public void registerResource(UserInfo userInfo, Resource resource) throws EndpointPathDuplicatException, ForbiddenAccessException {
        if (userInfo.role().equals(Role.ADMIN)) {
            if (resourceRepository.getResource(resource.getServiceName()).isPresent()) {
                throw new EndpointPathDuplicatException();
            }
            resourceRepository.registerResource(resource);
        } else {
            throw new ForbiddenAccessException();
        }
    }

    public void unregisterResource(UserInfo userInfo, ResourceInfo resourceInfo) throws EntityNotFoundException, ForbiddenAccessException {
        if (userInfo.role().equals(Role.ADMIN)) {
            if (resourceRepository.getResource(resourceInfo.serviceName()).isEmpty()) {
                throw new EntityNotFoundException();
            }
            resourceRepository.unregisterResource(resourceInfo.serviceName());
        } else {
            throw new ForbiddenAccessException();
        }
    }
}