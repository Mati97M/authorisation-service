package com.gridhub;

import com.gridhub.models.Resource;
import com.gridhub.repository.ResourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Set;

import static com.gridhub.enums.Role.ADMIN;
import static com.gridhub.enums.Role.LOGGED_USER;
import static com.gridhub.enums.Role.USER_SPECIFIC;

@ConfigurationPropertiesScan("com.gridhub.utilities")
@Slf4j
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(App.class, args);
        ResourceRepository repository = applicationContext.getBean(ResourceRepository.class);
        repository.saveAllAndFlush(
                List.of(
                        Resource.builder()
                                .serviceName("testNameUserSpec")
                                .endpointPath("api/test")
                                .roles(Set.of(USER_SPECIFIC))
                                .userSpecificId(1L)
                                .build(),
                        Resource.builder()
                                .serviceName("testNameLogged")
                                .endpointPath("api/test2")
                                .roles(Set.of(LOGGED_USER, ADMIN))
                                .build()
                )
        );
    }
}