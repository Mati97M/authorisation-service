package com.gridhub.controllers;

import com.gridhub.dtos.ResourceAccessDTO;
import com.gridhub.dtos.ResourceDeregistrationDTO;
import com.gridhub.dtos.ResourceInfo;
import com.gridhub.dtos.ResourceRegistrationDTO;
import com.gridhub.dtos.UserInfo;
import com.gridhub.exceptions.EndpointPathDuplicatException;
import com.gridhub.exceptions.EntityNotFoundException;
import com.gridhub.exceptions.ForbiddenAccessException;
import com.gridhub.mappers.ResourceDTOMapper;
import com.gridhub.models.Resource;
import com.gridhub.services.AuthorizationService;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AuthorizationController {
    private final AuthorizationService authorizationService = AuthorizationService.getInstance();
    private final ResourceDTOMapper resourceDTOMapper = new ResourceDTOMapper();

    public boolean hasPermissionToAccessResource(ResourceAccessDTO resourceAccessDTO) {
        ResourceInfo resourceInfo = resourceDTOMapper.mapToResourceInfo(resourceAccessDTO);
        UserInfo userInfo = resourceDTOMapper.mapToUserInfo(resourceAccessDTO);
        boolean result = false;

        try {
            result = authorizationService.hasPermissionToAccessResource(userInfo, resourceInfo);
        } catch (EntityNotFoundException e) {
            System.out.println(e);
        }
        return result;
    }

    public boolean registerResource(ResourceRegistrationDTO resourceRegistrationDTO) {
        Resource resource = resourceDTOMapper.mapToResource(resourceRegistrationDTO);
        UserInfo userInfo = resourceDTOMapper.mapToUserInfo(resourceRegistrationDTO);
        boolean result = false;

        try {
            authorizationService.registerResource(userInfo, resource);
            result = true;
        } catch (EndpointPathDuplicatException | ForbiddenAccessException e) {
            System.out.println(e);
        }
        return result;
    }

    public boolean unregisterResource(ResourceDeregistrationDTO resourceDeregistrationDTO) {
        ResourceInfo resourceInfo = resourceDTOMapper.mapToResourceInfo(resourceDeregistrationDTO);
        UserInfo userInfo = resourceDTOMapper.mapToUserInfo(resourceDeregistrationDTO);
        boolean result = false;

        try {
            authorizationService.unregisterResource(userInfo, resourceInfo);
            result = true;
        } catch (EntityNotFoundException | ForbiddenAccessException e) {
            System.out.println(e);
        }
        return result;
    }

    //validate JWT token?
}