package com.gridhub.controllers;

import com.gridhub.dtos.ResourceAccessDTO;
import com.gridhub.dtos.ResourceDeregistrationDTO;
import com.gridhub.dtos.ResourceRegistrationDTO;
import com.gridhub.enums.Role;
import com.gridhub.http.HttpResponse;
import com.gridhub.utilities.AuthorizationMessages;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringJUnitConfig(AuthorizationIntegrationTest.TestConfiguration.class)
class AuthorizationIntegrationTest {
    @Autowired
    public AuthorizationIntegrationTest(AuthorizationMessages authorizationMessages, AuthorizationController authorizationController) {
        this.authorizationMessages = authorizationMessages;
        this.authorizationController = authorizationController;
    }

    private final AuthorizationController authorizationController;
    private final AuthorizationMessages authorizationMessages;

    @Configuration
    @TestPropertySource(locations = "application.properties")
    @ComponentScan(basePackages = "com.gridhub")
    @ConfigurationPropertiesScan
    static class TestConfiguration {}

    @Test
    void hasPermissionToAccessResourceGrantsAccessToResourceIfTheRequestIsValidIT() {
        String endpointPath = "/api/authorisation/resources";
        ResourceAccessDTO request = new ResourceAccessDTO(
                "Authorisation Microservice",
                endpointPath,
                Role.ADMIN,
                null
        );
        HttpResponse httpResponse = authorizationController.hasPermissionToAccessResource(request);

        assertEquals(200, httpResponse.statusCode());
        assertEquals(authorizationMessages.accessToResourceGranted(), httpResponse.body());
        assertEquals(endpointPath, httpResponse.resourcePath());
    }

    @Test
    void hasPermissionToAccessResourceReturns403ResponseIfTheRoleDoesNotMatchIT() {
        String endpointPath = "/api/blog/pos";
        ResourceAccessDTO request = new ResourceAccessDTO(
                "Blog Microservice",
                endpointPath,
                Role.LOGGED_USER,
                null
        );
        HttpResponse httpResponse = authorizationController.hasPermissionToAccessResource(request);

        assertEquals(403, httpResponse.statusCode());
        assertEquals(authorizationMessages.accessForbidden(), httpResponse.body());
        assertNull(httpResponse.resourcePath());
    }

    @Test
    void hasPermissionToAccessResourceReturns404ResponseIfTheRequestedServiceDoesNotExistIT() {
        String endpointPath = "/api/missing";
        ResourceAccessDTO request = new ResourceAccessDTO(
                "Missing Service",
                endpointPath,
                Role.ADMIN,
                null
        );
        HttpResponse httpResponse = authorizationController.hasPermissionToAccessResource(request);

        assertEquals(404, httpResponse.statusCode());
        assertEquals(authorizationMessages.resourceNotFound(), httpResponse.body());
        assertNull(httpResponse.resourcePath());
    }

    @Test
    void registerResourceITRetunrs200ResponseIfRequestIsValidIT() {
        String endpointPath = "api/test";
        ResourceRegistrationDTO resourceRegistrationDTO = new ResourceRegistrationDTO(
                "Test Service",
                endpointPath,
                List.of(Role.ADMIN, Role.USER_SPECIFIC),
                15L,
                Role.LOGGED_USER
        );
        HttpResponse httpResponse = authorizationController.registerResource(resourceRegistrationDTO);

        assertEquals(200, httpResponse.statusCode());
        assertEquals(authorizationMessages.resourceWasSuccessfullyRegistered(), httpResponse.body());
        assertEquals(endpointPath, httpResponse.resourcePath());
    }

    @Test
    void registerResourceITReturns405ResponseIfTheRequestedResourceAlreadyExistsIT() {
        String endpointPath = "/api/authorisation/resources";
        ResourceRegistrationDTO resourceRegistrationDTO = new ResourceRegistrationDTO(
                "Authorisation Microservice",
                endpointPath,
                List.of(Role.ADMIN, Role.USER_SPECIFIC),
                15L,
                Role.LOGGED_USER
        );
        HttpResponse httpResponse = authorizationController.registerResource(resourceRegistrationDTO);

        assertEquals(405, httpResponse.statusCode());
        assertEquals(authorizationMessages.methodNotAllowed(), httpResponse.body());
        assertEquals(endpointPath, httpResponse.resourcePath());
    }

    @Test
    void registerResourceITReturns403ResponseIfAccessIsForbiddenIT() {
        String endpointPath = "/api/test";
        ResourceRegistrationDTO resourceRegistrationDTO = new ResourceRegistrationDTO(
                "Test Service",
                endpointPath,
                List.of(Role.ADMIN, Role.USER_SPECIFIC),
                15L,
                Role.LOGGED_USER
        );
        HttpResponse httpResponse = authorizationController.registerResource(resourceRegistrationDTO);

        assertEquals(403, httpResponse.statusCode());
        assertEquals(authorizationMessages.accessForbidden(), httpResponse.body());
        assertEquals(endpointPath, httpResponse.resourcePath());
    }

    @Test
    void unregisterResourceReturns200ResponseIfRequestIsValidIT() {
        String endpointPath = "/api/blog/post";
        ResourceDeregistrationDTO resourceDeregistrationDTO = new ResourceDeregistrationDTO(
                "Blog Microservice",
                endpointPath,
                Role.ADMIN,
                null
        );
        HttpResponse httpResponse = authorizationController.unregisterResource(resourceDeregistrationDTO);

        assertEquals(200, httpResponse.statusCode());
        assertEquals(authorizationMessages.resourceWasSuccessfullyUnregistered(), httpResponse.body());
        assertEquals(endpointPath, httpResponse.resourcePath());
    }

    @Test
    void unregisterResourceReturns200ResponseIfReturns404ResponseIfTheRequestedServiceDoesNotExistIT() {
        String endpointPath = "/api/test";
        ResourceDeregistrationDTO resourceDeregistrationDTO = new ResourceDeregistrationDTO(
                "Test Microservice",
                endpointPath,
                Role.ADMIN,
                null
        );
        HttpResponse httpResponse = authorizationController.unregisterResource(resourceDeregistrationDTO);

        assertEquals(404, httpResponse.statusCode());
        assertEquals(authorizationMessages.resourceNotFound(), httpResponse.body());
        assertNull(httpResponse.resourcePath());
    }

    @Test
    void unregisterResourceReturnsReturns403ResponseIfAccessIsForbiddenIT() {
        String endpointPath = "/api/test";
        ResourceDeregistrationDTO resourceDeregistrationDTO = new ResourceDeregistrationDTO(
                "Test Microservice",
                endpointPath,
                Role.USER_SPECIFIC,
                1L
        );
        HttpResponse httpResponse = authorizationController.unregisterResource(resourceDeregistrationDTO);

        assertEquals(403, httpResponse.statusCode());
        assertEquals(authorizationMessages.accessForbidden(), httpResponse.body());
        assertNull(httpResponse.resourcePath());
    }
}