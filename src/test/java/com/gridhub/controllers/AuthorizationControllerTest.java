package com.gridhub.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gridhub.dtos.ResourceDeregistrationDTO;
import com.gridhub.dtos.ResourceInfo;
import com.gridhub.dtos.ResourceRegistrationDTO;
import com.gridhub.dtos.UserInfo;
import com.gridhub.enums.Role;
import com.gridhub.exceptions.EndpointPathDuplicatException;
import com.gridhub.exceptions.EntityNotFoundException;
import com.gridhub.exceptions.ForbiddenAccessException;
import com.gridhub.models.Resource;
import com.gridhub.services.AuthorizationService;
import com.gridhub.utilities.AuthorizationMessages;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AllArgsConstructor
@SpringJUnitConfig(AuthorizationControllerTest.TestConfig.class)
@WebMvcTest(controllers = AuthorizationController.class)
class AuthorizationControllerTest {
    public static final String SERVICE_NAME_REQUEST = "serviceName";
    public static final String ENDPOINT_PATH_REQUEST = "endpointPath";
    public static final String ROLE = "role";
    public static final String SERVICE_NAME_VALUE = "testName";
    public static final String ENDPOINT_PATH_VALUE = "api/test";
    public static final String ADMIN_VALUE = "ADMIN";
//    public static final String TEST_NAME_USER_SPEC = "testNameUserSpec";
    public static final String USER_SPECIFIC = "USER_SPECIFIC";
//    public static final String ROLES = "roles";
    @Autowired
    private AuthorizationController authorizationController;
    @Autowired
    private AuthorizationMessages authorizationMessages;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private final AuthorizationService mockService;

    @TestPropertySource("classpath:application.properties")
    @ConfigurationPropertiesScan("com.gridhub.utilities")
    @TestConfiguration
    static class TestConfig {}

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(
                        authorizationController)
                .alwaysExpect(status().isOk())
                .alwaysExpect(model().attributeExists("statusCode"))
                .alwaysExpect(model().attributeExists("body"))
                .alwaysExpect(model().attributeExists("resourcePath"))
                .build();
    }

    @Test
    void adminHasPermissionToAccessEveryResourceWhichExistsTest() throws Exception {
        when(mockService.hasPermissionToAccessResource(any(UserInfo.class), any(ResourceInfo.class))).thenReturn(true);

        mockMvc.perform(
                        get("/resources/resource")
                                .param(SERVICE_NAME_REQUEST, SERVICE_NAME_VALUE)
                                .param(ENDPOINT_PATH_REQUEST, ENDPOINT_PATH_VALUE)
                                .param(ROLE, ADMIN_VALUE)
                ).andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body").value(authorizationMessages.accessToResourceGranted()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.resourcePath").value(ENDPOINT_PATH_VALUE));

    }

    @Test
    void nonAdminDoesNotHavePermissionToAccessIfResourceIsNotSpecificToHimTest() throws Exception {
        when(mockService.hasPermissionToAccessResource(any(UserInfo.class), any(ResourceInfo.class))).thenReturn(false);

        mockMvc.perform(
                        get("/resources/resource")
                                .param(SERVICE_NAME_REQUEST, SERVICE_NAME_VALUE)
                                .param(ENDPOINT_PATH_REQUEST, ENDPOINT_PATH_VALUE)
                                .param(ROLE, USER_SPECIFIC)
                                .param("userSpecificId", String.valueOf(2))
                ).andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(403))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body").value(authorizationMessages.accessForbidden()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.resourcePath").value(null));

    }

    @Test
    void nonAdminDoesHavePermissionToAccessResourceNotSpecificToHimTest() throws Exception {
        when(mockService.hasPermissionToAccessResource(any(UserInfo.class), any(ResourceInfo.class))).thenReturn(true);

        mockMvc.perform(
                        get("/resources/resource")
                                .param(SERVICE_NAME_REQUEST, SERVICE_NAME_VALUE)
                                .param(ENDPOINT_PATH_REQUEST, ENDPOINT_PATH_VALUE)
                                .param(ROLE, USER_SPECIFIC)
                                .param("userSpecificId", String.valueOf(2))
                ).andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body").value(authorizationMessages.accessForbidden()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.resourcePath").value(ENDPOINT_PATH_VALUE));

    }

    @Test
    void getRequestGets404WhenResourceIsNotFoundTest() throws Exception {
        when(mockService.hasPermissionToAccessResource(any(UserInfo.class), any(ResourceInfo.class))).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(
                        get("/resources/resource")
                                .param(SERVICE_NAME_REQUEST, SERVICE_NAME_VALUE)
                                .param(ENDPOINT_PATH_REQUEST, ENDPOINT_PATH_VALUE)
                                .param(ROLE, ADMIN_VALUE)
                ).andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body").value(authorizationMessages.resourceNotFound()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.resourcePath").value(null));

    }

    @Test
    void afterGetRequestCode500WhenInternallErrorEccursTest() throws Exception {
        when(mockService.hasPermissionToAccessResource(any(UserInfo.class), any(ResourceInfo.class))).thenThrow(RuntimeException.class);

        mockMvc.perform(
                        get("/resources/resource")
                                .param(SERVICE_NAME_REQUEST, SERVICE_NAME_VALUE)
                                .param(ENDPOINT_PATH_REQUEST, ENDPOINT_PATH_VALUE)
                                .param(ROLE, ADMIN_VALUE)
                ).andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(500))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body").value(authorizationMessages.unknownError()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.resourcePath").value(null));

    }

    @Test
    void adminRegistersNewUniqueResourceTest() throws Exception {
        long userSpecificId = 15L;
        Role role = Role.ADMIN;
        ResourceRegistrationDTO resourceRegistrationDTO = new ResourceRegistrationDTO(
                SERVICE_NAME_VALUE,
                ENDPOINT_PATH_VALUE,
                List.of(Role.ADMIN, Role.USER_SPECIFIC),
                userSpecificId,
                role
        );
        mockMvc.perform(MockMvcRequestBuilders.post("/resources/resource")
                        .content(asJsonString(resourceRegistrationDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(201))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body").value(authorizationMessages.resourceWasSuccessfullyRegistered()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.resourcePath").value(ENDPOINT_PATH_VALUE));

        verify(mockService).registerResource(any(UserInfo.class), any(Resource.class));
//                .accept(MediaType.APPLICATION_JSON)
//                .body(new JSONObject()
//                        .put(SERVICE_NAME_REQUEST, SERVICE_NAME_VALUE)
//                        .put(ENDPOINT_PATH_REQUEST, ENDPOINT_PATH_VALUE)
//                        .put(ROLES, List.of(Role.ADMIN, Role.USER_SPECIFIC))
//                        .put(USER_SPECIFIC, null)
//                        .put(ROLE, ADMIN_VALUE)
//                        .toString()
//                )
    }

    @Test
    void nonAdminGets403WhileRegisteringNewResourceTest() throws Exception {
        long userSpecificId = 15L;
        Role role = Role.LOGGED_USER;
        ResourceRegistrationDTO resourceRegistrationDTO = new ResourceRegistrationDTO(
                SERVICE_NAME_VALUE,
                ENDPOINT_PATH_VALUE,
                List.of(Role.ADMIN, Role.USER_SPECIFIC),
                userSpecificId,
                role
        );
        doThrow(ForbiddenAccessException.class).when(mockService).registerResource(any(UserInfo.class), any(Resource.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/resources/resource")
                        .content(asJsonString(resourceRegistrationDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(403))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body").value(authorizationMessages.methodNotAllowed()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.resourcePath").value(null));

        verify(mockService).registerResource(any(UserInfo.class), any(Resource.class));
    }

    @Test
    void adminGets405WhileRegisteringDuplicateResourceTest() throws Exception {
        long userSpecificId = 15L;
        Role role = Role.ADMIN;
        ResourceRegistrationDTO resourceRegistrationDTO = new ResourceRegistrationDTO(
                SERVICE_NAME_VALUE,
                ENDPOINT_PATH_VALUE,
                List.of(Role.ADMIN, Role.USER_SPECIFIC),
                userSpecificId,
                role
        );
        doThrow(EndpointPathDuplicatException.class).when(mockService).registerResource(any(UserInfo.class), any(Resource.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/resources/resource")
                        .content(asJsonString(resourceRegistrationDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(405))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body").value(authorizationMessages.methodNotAllowed()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.resourcePath").value(null));

        verify(mockService).registerResource(any(UserInfo.class), any(Resource.class));
    }

    @Test
    void afterPostRequestCode500WhenInternallErrorEccursTest() throws Exception {
        long userSpecificId = 15L;
        Role role = Role.LOGGED_USER;
        ResourceRegistrationDTO resourceRegistrationDTO = new ResourceRegistrationDTO(
                SERVICE_NAME_VALUE,
                ENDPOINT_PATH_VALUE,
                List.of(Role.ADMIN, Role.USER_SPECIFIC),
                userSpecificId,
                role
        );
        doThrow(RuntimeException.class).when(mockService).registerResource(any(UserInfo.class), any(Resource.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/resources/resource")
                        .content(asJsonString(resourceRegistrationDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(500))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body").value(authorizationMessages.unknownError()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.resourcePath").value(null));

        verify(mockService).registerResource(any(UserInfo.class), any(Resource.class));
    }

    @Test
    void adminDeletesResourceTest() throws Exception {
        Role role = Role.ADMIN;
        ResourceDeregistrationDTO resourceDeregistrationDTO = new ResourceDeregistrationDTO(
                SERVICE_NAME_VALUE,
                ENDPOINT_PATH_VALUE,
                role,
                null
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/resources/resource")
                        .content(asJsonString(resourceDeregistrationDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(204))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body").value(authorizationMessages.resourceWasSuccessfullyUnregistered()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.resourcePath").value(ENDPOINT_PATH_VALUE));

        verify(mockService).unregisterResource(any(UserInfo.class), any(ResourceInfo.class));
    }

    @Test
    void nonAdminDeletesResourceAndGetsTest() throws Exception {
        Role role = Role.ADMIN;
        ResourceDeregistrationDTO resourceDeregistrationDTO = new ResourceDeregistrationDTO(
                SERVICE_NAME_VALUE,
                ENDPOINT_PATH_VALUE,
                role,
                null
        );
        doThrow(ForbiddenAccessException.class).when(mockService).unregisterResource(any(UserInfo.class), any(ResourceInfo.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/resources/resource")
                        .content(asJsonString(resourceDeregistrationDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(403))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body").value(authorizationMessages.accessForbidden()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.resourcePath").value(null));

        verify(mockService).unregisterResource(any(UserInfo.class), any(ResourceInfo.class));
    }

    @Test
    void adminGets404WhenDeletingNonExistingResourceTest() throws Exception {
        Role role = Role.ADMIN;
        ResourceDeregistrationDTO resourceDeregistrationDTO = new ResourceDeregistrationDTO(
                SERVICE_NAME_VALUE,
                ENDPOINT_PATH_VALUE,
                role,
                null
        );
        doThrow(EntityNotFoundException.class).when(mockService).unregisterResource(any(UserInfo.class), any(ResourceInfo.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/resources/resource")
                        .content(asJsonString(resourceDeregistrationDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body").value(authorizationMessages.resourceNotFound()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.resourcePath").value(null));

        verify(mockService).unregisterResource(any(UserInfo.class), any(ResourceInfo.class));
    }

    @Test
    void afterDeleteCode500WhenInternallErrorEccursTest() throws Exception {
        Role role = Role.ADMIN;
        ResourceDeregistrationDTO resourceDeregistrationDTO = new ResourceDeregistrationDTO(
                SERVICE_NAME_VALUE,
                ENDPOINT_PATH_VALUE,
                role,
                null
        );
        doThrow(EntityNotFoundException.class).when(mockService).unregisterResource(any(UserInfo.class), any(ResourceInfo.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/resources/resource")
                        .content(asJsonString(resourceDeregistrationDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body").value(authorizationMessages.resourceNotFound()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.resourcePath").value(null));

        verify(mockService).unregisterResource(any(UserInfo.class), any(ResourceInfo.class));
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}