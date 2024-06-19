package com.gridhub;

import contextConfig.DateSupplier;
import contextConfig.ProdConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.Date;

@EnableConfigurationProperties
@ConfigurationPropertiesScan("contextConfig")
@ComponentScan("contextConfig")
@Slf4j
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(App.class, args);
        ProdConfig prodConfig = applicationContext.getBean(ProdConfig.class);
        log.info(prodConfig.toString());

//        DashPropertiesSupplier dashPropertiesSupplier = applicationContext.getBean(DashPropertiesSupplier.class);
//        log.info(Arrays.toString(dashPropertiesSupplier.get()));

        Date date = applicationContext.getBean(DateSupplier.class).get();
        log.info(date.toString());
    }
}