## 5.3.1 Application Context configuration

### What is the difference between the @Configuration,@Component and @Service annotations?

@Configuration annotation marks a class which is supposed to have info about configuration e.g definitions of fabric method (Annotated with @Bean), setting profiles, properties etc.
All components will be created and loaded into the application context. Java configuration.
@Component and @Service are both for registering beans (java), with component scan, annotation based configuration. There is no practical difference 
between @Service and @Component, but improves code clarity.

### How can we customize the component scanning process?

By adding additional annotation, @ComponentScan, to the @Configuration class, and sending the packages to be scanned.

### What value will a property have if it is defined in two different profiles both of which are active?

It depends on the order of the active profiles listed in application.properties, but generally the last one overrides the previous.

### Why would we use factory beans instead of regular beans?

Generally, factory method and factory beans are handy because of:
* we do not have to know the exact types and dependencies of the objects your code should work with (easy to extension).
* we can bind the result types to the context and active profiles

### How can we override any property defined in any .properties file?

Properties can be overridden by another ones, from the higher level. And so it goes: 
1) `application.properties`
2) `RandomValuePropertySource`
3) OS env vars
4) Java System properties (`System.getProperties()`)
5) JNDI attributes from `java:comp/env`
6) `ServletContext` init parameters
7) `ServletConfig` init parameters
8) Properties from `SPRING_APPLICATION_JSON` (inline JSON embedded in an environment variable or system property)
9) Command line arguments (program arguments)
10) tests' properties, `@SpringBootTest`
11) `@DynamicPropertySource`(tests)
12) `@TestPropertySource` (tests)
13) Devtools global settings properties (`$HOME/.config/spring-boot`) when devtools are active

### Does PostDestruct get called for a prototype scope bean?
No