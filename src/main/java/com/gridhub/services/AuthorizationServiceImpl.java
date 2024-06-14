package com.gridhub.services;

import com.gridhub.dtos.ResourceInfo;
import com.gridhub.dtos.UserInfo;
import com.gridhub.enums.Role;
import com.gridhub.exceptions.EndpointPathDuplicatException;
import com.gridhub.exceptions.EntityNotFoundException;
import com.gridhub.exceptions.ForbiddenAccessException;
import com.gridhub.models.Resource;
import com.gridhub.repository.Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthorizationServiceImpl implements AuthorizationService {
    private final Repository repository;

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
                            alreadyExistingResource -> repository.deleteResource(resourceInfo.serviceName()),
                            () -> {
                                throw new EntityNotFoundException();
                            }
                    );
        }
    }
}