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
import com.gridhub.utilities.AuthorizationMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/resources/resource")
@RestController
public class AuthorizationController {
    private final AuthorizationMessages authorizationMessages;
    private final AuthorizationService authorizationService;

    @GetMapping
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
                body = authorizationMessages.accessToResourceGranted();
                endpointPath = resourceInfo.endpointPath();
            } else {
                statusCode = 403;
                body = authorizationMessages.accessForbidden();
            }
        } catch (EntityNotFoundException e) {
            statusCode = 404;
            body = authorizationMessages.resourceNotFound();
            log.error(authorizationMessages.resourceNotFound().concat(": %s").formatted(resourceInfo.serviceName()), e);
        } catch (Exception e) {
            statusCode = 500;
            body = authorizationMessages.unknownError();
            log.error(authorizationMessages.unknownError().concat(": %s").formatted(e.getMessage()), e);
        }
        return new HttpResponse(statusCode, body, endpointPath);
    }

    @PostMapping
    public HttpResponse registerResource(ResourceRegistrationDTO resourceRegistrationDTO) {
        Resource resource = ResourceMapper.INSTANCE.mapToResource(resourceRegistrationDTO);
        UserInfo userInfo = UserInfoMapper.INSTANCE.mapToUserInfo(resourceRegistrationDTO);
        int statusCode;
        String body = null;
        String endpointPath = null;

        try {
            authorizationService.registerResource(userInfo, resource);
            statusCode = 201;
            body = authorizationMessages.resourceWasSuccessfullyRegistered();
            endpointPath = resourceRegistrationDTO.endpointPath();
        } catch (EndpointPathDuplicatException e) {
            statusCode = 405;
            body = authorizationMessages.methodNotAllowed();
            log.error(body.concat(": %s").formatted("registerResource"), e);
        } catch (ForbiddenAccessException e) {
            statusCode = 403;
            body = authorizationMessages.accessForbidden();
            log.error(body.concat(": %s").formatted(resource.getServiceName()), e);
        } catch (Exception e) {
            statusCode = 500;
            body = authorizationMessages.unknownError();
            log.error(body.concat(": %s").formatted(e.getMessage()), e);
        }
        return new HttpResponse(statusCode, body, endpointPath);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = { Exception.class })
    @DeleteMapping
    public HttpResponse unregisterResource(ResourceDeregistrationDTO resourceDeregistrationDTO) {
        ResourceInfo resourceInfo = ResourceMapper.INSTANCE.mapToResourceInfo(resourceDeregistrationDTO);
        UserInfo userInfo = UserInfoMapper.INSTANCE.mapToUserInfo(resourceDeregistrationDTO);
        int statusCode;
        String body = null;
        String endpointPath = null;

        try {
            authorizationService.unregisterResource(userInfo, resourceInfo);
            statusCode = 204;
            body = authorizationMessages.resourceWasSuccessfullyUnregistered();
            endpointPath = resourceDeregistrationDTO.endpointPath();
        } catch (EntityNotFoundException e) {
            statusCode = 404;
            body = authorizationMessages.resourceNotFound();
            log.error(body.concat(": %s").formatted(resourceInfo.serviceName()), e);
        } catch (ForbiddenAccessException e) {
            statusCode = 403;
            body = authorizationMessages.accessForbidden();
            log.error(body.concat(": %s").formatted(resourceInfo.serviceName()), e);
        } catch (Exception e) {
            statusCode = 500;
            body = authorizationMessages.unknownError();
            log.error(body.concat(": %s").formatted(e.getMessage()), e);
        }
        return new HttpResponse(statusCode, body, endpointPath);
    }
}