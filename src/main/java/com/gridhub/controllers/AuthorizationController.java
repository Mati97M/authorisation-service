package com.gridhub.controllers;

import com.gridhub.dtos.ResourceAccessDTO;
import com.gridhub.dtos.ResourceDeregistrationDTO;
import com.gridhub.dtos.ResourceInfo;
import com.gridhub.dtos.ResourceRegistrationDTO;
import com.gridhub.dtos.UserInfo;
import com.gridhub.exceptions.EndpointPathDuplicatException;
import com.gridhub.exceptions.EntityNotFoundException;
import com.gridhub.exceptions.ForbiddenAccessException;
import com.gridhub.http.HttpResponse;
import com.gridhub.mappers.ResourceDTOMapper;
import com.gridhub.models.Resource;
import com.gridhub.services.AuthorizationService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Slf4j
public class AuthorizationController {
    private final AuthorizationService authorizationService = AuthorizationService.getInstance();
    private final ResourceDTOMapper resourceDTOMapper = new ResourceDTOMapper();

    public HttpResponse hasPermissionToAccessResource(ResourceAccessDTO resourceAccessDTO) {
        ResourceInfo resourceInfo = resourceDTOMapper.mapToResourceInfo(resourceAccessDTO);
        UserInfo userInfo = resourceDTOMapper.mapToUserInfo(resourceAccessDTO);
        boolean hasPermission = false;
        int statusCode;
        String body;
        String endpointPath = null;

        try {
            hasPermission = authorizationService.hasPermissionToAccessResource(userInfo, resourceInfo);
            if(hasPermission) {
                statusCode = 200;
                body = "Access to resource granted";
                endpointPath = resourceInfo.endpointPath();
            } else {
                statusCode = 403;
                body = "Access Forbidden";
            }
        } catch (EntityNotFoundException e) {
            statusCode = 404;
            body = "Resource not found";
            log.error(e.getMessage());
        }
        return new HttpResponse(statusCode, body, endpointPath);
    }

    public HttpResponse registerResource(ResourceRegistrationDTO resourceRegistrationDTO) {
        Resource resource = resourceDTOMapper.mapToResource(resourceRegistrationDTO);
        UserInfo userInfo = resourceDTOMapper.mapToUserInfo(resourceRegistrationDTO);
        int statusCode;
        String body = null;

        try {
            authorizationService.registerResource(userInfo, resource);
            statusCode = 200;
            body = "Resource was successfully registered";
        } catch (EndpointPathDuplicatException e) {
            statusCode = 405;
            body = "Method Not Allowed";
            log.error(e.getMessage());
        } catch (ForbiddenAccessException e) {
            statusCode = 403;
            body = "Access Forbidden";
            log.error(e.getMessage());
        }
        return new HttpResponse(statusCode, body, resourceRegistrationDTO.endpointPath());
    }

    public HttpResponse unregisterResource(ResourceDeregistrationDTO resourceDeregistrationDTO) {
        ResourceInfo resourceInfo = resourceDTOMapper.mapToResourceInfo(resourceDeregistrationDTO);
        UserInfo userInfo = resourceDTOMapper.mapToUserInfo(resourceDeregistrationDTO);
        int statusCode;
        String body = null;
        String endpointPath = null;

        try {
            authorizationService.unregisterResource(userInfo, resourceInfo);
            statusCode = 200;
            body = "Resource was successfully unregistered";
            endpointPath = resourceDeregistrationDTO.endpointPath();
        } catch (EntityNotFoundException e) {
            statusCode = 404;
            body = "Resource not found";
            log.error(e.getMessage());
        } catch (ForbiddenAccessException e) {
            statusCode = 403;
            body = "Access Forbidden";
            log.error(e.getMessage());
        }
        return new HttpResponse(statusCode, body, endpointPath);
    }

    //validate JWT token?
}