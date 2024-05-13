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
import com.gridhub.mappers.ResourceMapper;
import com.gridhub.mappers.UserInfoMapper;
import com.gridhub.models.Resource;
import com.gridhub.services.AuthorizationService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Slf4j
public class AuthorizationController {
    public static final String ACCESS_FORBIDDEN = "Access Forbidden";
    public static final String ACCESS_TO_RESOURCE_GRANTED = "Access to resource granted";
    public static final String RESOURCE_NOT_FOUND = "Resource not found";
    public static final String RESOURCE_WAS_SUCCESSFULLY_REGISTERED = "Resource was successfully registered";
    public static final String METHOD_NOT_ALLOWED = "Method Not Allowed";
    public static final String RESOURCE_WAS_SUCCESSFULLY_UNREGISTERED = "Resource was successfully unregistered";
    private final AuthorizationService authorizationService = AuthorizationService.getInstance();

    public HttpResponse hasPermissionToAccessResource(ResourceAccessDTO resourceAccessDTO) {
        ResourceInfo resourceInfo = ResourceMapper.INSTANCE.mapToResourceInfo(resourceAccessDTO);
        UserInfo userInfo = UserInfoMapper.INSTANCE.mapToUserInfo(resourceAccessDTO);
        boolean hasPermission = false;
        int statusCode;
        String body;
        String endpointPath = null;

        try {
            hasPermission = authorizationService.hasPermissionToAccessResource(userInfo, resourceInfo);
            if (hasPermission) {
                statusCode = 200;
                body = ACCESS_TO_RESOURCE_GRANTED;
                endpointPath = resourceInfo.endpointPath();
            } else {
                statusCode = 403;
                body = ACCESS_FORBIDDEN;
            }
        } catch (EntityNotFoundException e) {
            statusCode = 404;
            body = RESOURCE_NOT_FOUND;
            log.error(e.getMessage());
        }
        return new HttpResponse(statusCode, body, endpointPath);
    }

    public HttpResponse registerResource(ResourceRegistrationDTO resourceRegistrationDTO) {
        Resource resource = ResourceMapper.INSTANCE.mapToResource(resourceRegistrationDTO);
        UserInfo userInfo = UserInfoMapper.INSTANCE.mapToUserInfo(resourceRegistrationDTO);
        int statusCode;
        String body = null;

        try {
            authorizationService.registerResource(userInfo, resource);
            statusCode = 200;
            body = RESOURCE_WAS_SUCCESSFULLY_REGISTERED;
        } catch (EndpointPathDuplicatException e) {
            statusCode = 405;
            body = METHOD_NOT_ALLOWED;
            log.error(e.getMessage());
        } catch (ForbiddenAccessException e) {
            statusCode = 403;
            body = ACCESS_FORBIDDEN;
            log.error(e.getMessage());
        }
        return new HttpResponse(statusCode, body, resourceRegistrationDTO.endpointPath());
    }

    public HttpResponse unregisterResource(ResourceDeregistrationDTO resourceDeregistrationDTO) {
        ResourceInfo resourceInfo = ResourceMapper.INSTANCE.mapToResourceInfo(resourceDeregistrationDTO);
        UserInfo userInfo = UserInfoMapper.INSTANCE.mapToUserInfo(resourceDeregistrationDTO);
        int statusCode;
        String body = null;
        String endpointPath = null;

        try {
            authorizationService.unregisterResource(userInfo, resourceInfo);
            statusCode = 200;
            body = RESOURCE_WAS_SUCCESSFULLY_UNREGISTERED;
            endpointPath = resourceDeregistrationDTO.endpointPath();
        } catch (EntityNotFoundException e) {
            statusCode = 404;
            body = RESOURCE_NOT_FOUND;
            log.error(e.getMessage());
        } catch (ForbiddenAccessException e) {
            statusCode = 403;
            body = ACCESS_FORBIDDEN;
            log.error(e.getMessage());
        }
        return new HttpResponse(statusCode, body, endpointPath);
    }
}