package com.gridhub.repositories.dao;

import com.gridhub.enums.Role;
import com.gridhub.models.Resource;
import com.gridhub.utilities.RepositoryConnection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepositoryDAOTest {
    public static final  Resource TEST_SERVICE = new Resource("testService", "api/test", List.of(Role.ADMIN), 1L);

    @Mock
    private RepositoryConnection repositoryConnection;
    @InjectMocks
    private RepositoryDAO repositoryDAO;

    @Test
    void saveResourceWillDoNothingIfResourceIsNullTest() {
        repositoryDAO.saveResource(null);
        verify(repositoryConnection, never()).findOne(anyString(),any(),any());
    }

    @Test
    void saveResourceWillNotSaveResourceIfTheSameIsAlreadyPresentTest() {
        when(repositoryConnection.findOne(
                anyString(),
                any(),
                eq(TEST_SERVICE.getEndpointPath()),
                eq(TEST_SERVICE.getServiceName()))
        ).thenReturn(TEST_SERVICE);

        repositoryDAO.saveResource(TEST_SERVICE);

        verify(repositoryConnection).findOne(
                anyString(),
                any(),
                eq(TEST_SERVICE.getEndpointPath()),
                eq(TEST_SERVICE.getServiceName())
        );
        verify(repositoryConnection, never()).execute(
                anyString(),
                any(),
                eq(TEST_SERVICE.getEndpointPath()),
                eq(TEST_SERVICE.getServiceName())
        );
    }

    @Test
    void deleteResourceTest() {
        repositoryDAO.deleteResource(TEST_SERVICE.getServiceName(), TEST_SERVICE.getEndpointPath());

        verify(repositoryConnection).execute(
                anyString(),
                any(Consumer.class));
    }

    @Test
    void findResourceTest() {
        repositoryDAO.findResource(TEST_SERVICE.getServiceName(), TEST_SERVICE.getEndpointPath());

        verify(repositoryConnection).findOne(
                anyString(),
                any(),
                eq(TEST_SERVICE.getServiceName()),
                eq(TEST_SERVICE.getEndpointPath())
        );
    }

    @Test
    void findAllResourcesTest() {
        repositoryDAO.findAllResources();

        verify(repositoryConnection).findMany(
                anyString(),
                any()
        );
    }

    @Test
    void findResourceByServiceNameTest() {
        repositoryDAO.findResourceByServiceName(TEST_SERVICE.getServiceName());

        verify(repositoryConnection).findMany(
                anyString(),
                any(),
                eq(TEST_SERVICE.getServiceName())
        );
    }

    @Test
    void updateResourceWillDoNothingIfResourceDoesNotExistTest() {
        when(repositoryConnection.findOne(
                anyString(),
                any(),
                eq(TEST_SERVICE.getServiceName()),
                eq(TEST_SERVICE.getEndpointPath())
        )).thenReturn(null);

        repositoryDAO.updateResource(TEST_SERVICE);

        verifyNoMoreInteractions(repositoryConnection);
    }

    @Test
    void updateResourceTest() {
        repositoryDAO.updateResource(TEST_SERVICE);

        verify(repositoryConnection).findOne(
                anyString(),
                any(),
                eq(TEST_SERVICE.getServiceName()),
                eq(TEST_SERVICE.getEndpointPath())
        );
    }

    @Test
    void findAllRolesTest() {
        repositoryDAO.findAllRoles();

        verify(repositoryConnection).findMany(
                anyString(),
                any()
        );
    }
}