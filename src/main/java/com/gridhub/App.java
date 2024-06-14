package com.gridhub;

import com.gridhub.mappers.databaseMappers.DatabaseMapper;
import com.gridhub.models.Resource;
import com.gridhub.utilities.RepositoryConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@Slf4j
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(App.class, args);
        RepositoryConnection repositoryConnection = applicationContext.getBean(RepositoryConnection.class);
        Resource resource = repositoryConnection.findOne(
                        "SELECT r.id, r.endpointPath, r.serviceName, r.userSpecificId, rr.role " +
                        "from resources r " +
                        "join resources_roles rr " +
                        "on r.id = rr.resource_id " +
                        "WHERE id = 1;",
                DatabaseMapper.mapToResource()
        );
        if (resource == null) {
            log.error("Resource not found. Maybe Hikari does not work");
        } else {
            log.info("Resource found. Hikari is working");
        }
    }
}
