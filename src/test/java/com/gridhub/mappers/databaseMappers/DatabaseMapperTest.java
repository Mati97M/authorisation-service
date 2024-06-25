package com.gridhub.mappers.databaseMappers;

import com.gridhub.enums.Role;
import com.gridhub.models.Resource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabaseMapperTest {
    public static final Resource TEST_SERVICE = new Resource(null, "testService", "api/test", 1L, List.of(Role.ADMIN));

    @Mock
    private ResultSet resultSet;

    @SneakyThrows
    @Test
    void mapToResourceTest() {
        when(resultSet.getLong("id")).thenReturn(1L, 1L);
        when(resultSet.getString("serviceName")).thenReturn(TEST_SERVICE.getServiceName());
        when(resultSet.getString("endpointPath")).thenReturn(TEST_SERVICE.getEndpointPath());
        when(resultSet.getLong("userSpecificId")).thenReturn(TEST_SERVICE.getUserSpecificId());
        when(resultSet.getString("role")).thenReturn(TEST_SERVICE.getRoles().get(0).name());
        when(resultSet.next()).thenReturn(false);

        Resource resource = DatabaseMapper.mapToResource().apply(resultSet);

        assertEquals(resource.getServiceName(), TEST_SERVICE.getServiceName());
        assertEquals(resource.getEndpointPath(), TEST_SERVICE.getEndpointPath());
        assertEquals(resource.getUserSpecificId(), TEST_SERVICE.getUserSpecificId());
        assertEquals(resource.getRoles(), TEST_SERVICE.getRoles());
    }
}