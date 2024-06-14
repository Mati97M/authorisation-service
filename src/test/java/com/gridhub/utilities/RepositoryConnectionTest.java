package com.gridhub.utilities;

import com.gridhub.models.Resource;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.ResultSet;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
@SpringBootTest
class RepositoryConnectionTest {
    private final RepositoryConnection repositoryConnection;
    @Mock
    private static Function<ResultSet, Resource> resourceMapper;

    @AfterEach
    void tearDownTest() {
        if (repositoryConnection != null) {
            repositoryConnection.close();
        }
    }

    @Test
    void executeInsertsNewRecordTest() {
        String serviceName = "TEST";
        String selectSQL = "SELECT * FROM resources WHERE serviceName = '" + serviceName + "'";
        assertNull(
                repositoryConnection.findOne(
                        selectSQL,
                        resourceMapper
                )
        );
        verifyNoInteractions(resourceMapper);

        String endpointPath = "api/test";
        int userSpecificId = 10;
        String insertSQL = "INSERT INTO resources (endpointPath, serviceName, userSpecificId) VALUES (?, ?, ?)";

        repositoryConnection.execute(insertSQL, endpointPath, serviceName, userSpecificId);

        when(resourceMapper.apply(any(ResultSet.class))).thenReturn(new Resource(null, serviceName, endpointPath, (long) userSpecificId, null));
        assertNotNull(
                repositoryConnection.findOne(
                        selectSQL,
                        resourceMapper
                )
        );
        verify(resourceMapper).apply(any(ResultSet.class));
    }

    @Test
    void findOneReturnsNullWhenThereIsNoRequestedRecordsTest() {
        String serviceName = "Test";
        String findResourcesSQL = "SELECT endpointPath FROM resources WHERE serviceName = ?;";

        assertNull(repositoryConnection.findOne(findResourcesSQL, resourceMapper, serviceName));
        verifyNoInteractions(resourceMapper);
    }

    @Test
    void findOneThrowsExceptionWhenThereIsMoreThanOneRecordTest() {
        String serviceName = "Blog Microservice";
        String findResourcesSQL = "SELECT endpointPath FROM resources WHERE serviceName = ?;";
        when(resourceMapper.apply(any())).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> repositoryConnection.findOne(findResourcesSQL, resourceMapper, serviceName));
        verify(resourceMapper).apply(any());
    }

    @Test
    void findManyReturnsEmptyListWhenRequestedRecordsNotInDBTest() {
        String tableName = "resources";
        String serviceName = "TEST";
        String findNotPresentResourcesSQL = "SELECT * FROM " + tableName + " WHERE serviceName = ?;";

        List<Resource> resultList = repositoryConnection.findMany(
                findNotPresentResourcesSQL,
                resourceMapper,
                serviceName);

        assertTrue(resultList.isEmpty());
        verifyNoInteractions(resourceMapper);
    }

    @Test
    void findManyReturnsNonEmptyListWhenRequestedRecordsIsInDBTest() {
        String tableName = "resources";
        String serviceName = "Blog Microservice";
        String findResourcesSQL = "SELECT * FROM " + tableName + " WHERE serviceName = ?;";
        int recordsNum = 2;
        when(resourceMapper.apply(any())).thenReturn(null);

        List<Resource> resultList = repositoryConnection.findMany(findResourcesSQL, resourceMapper, serviceName);
        assertEquals(recordsNum, resultList.size());
        verify(resourceMapper, times(recordsNum)).apply(any());
    }
}