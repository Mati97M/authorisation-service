# AutoConfiguration

## 1. What is the difference between regular configuration and autoconfiguration?
* Regular configuration is written by a programmer himself, using @Configuration annotation. 
* Autoconfiguration is a feature of Spring Boot, which gives all dependencies and beans, necessary for the application to work.
It relies on predefined Configuration classes, which has @Conditional`s for creation of their beans. This mechanism
checks classpath, existing beans, and defined properties.

## 2. Would all conditional annotations on bean definitions work in regular configuration classes? Elaborate.
Generally, all in Spring Boot project we can use all of them. But since Spring Boot has his own more specific 
conditional annotations, e.g. @ConditionalOnMissingBean @ConditionalOnProperty, @ConditionalOnClass, we can not use them in Spring (they are restricted to Spring Boot).

## 3. How can we customize the autoconfiguration process?
* using application.properties or yaml files
* overriding SpringBoot`s @Configuration classes defining custom ones
* disabling autoconfiguration classes, by setting `exclude` property in `@SpringBootApplication`, or spring.autoconfigure.exclude in the properties/yaml files
* marking bean methods in @Configuration class with @Conditional annotation

## 4. What is the condition that causes a tomcat server to start on port 8080 when the application starts?
Port 8080 is used, when no other port is explicitly set.

### Find a way to debug autoconfiguration on application startup. What information can you see ? Try and find a way to disable the autoconfiguration based on the conditions.
To be able to debug autoconfiguration, the `debug` property has to be set on `true` (in application.properties or yaml file)
Then, after running the application, we get `CONDITIONS EVALUATION REPORT`