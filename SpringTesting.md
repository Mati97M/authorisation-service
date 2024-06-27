# Spring testing

## 1. How can we configure the application context when running tests with @SpringBootTest annotation?
* setting configuration classes in annotation `@SpringBootTest`
* setting properties with: property files and `@TestPropertySource`, or in test/resources/application.properties
* with `@Profile` annotation - tests classes are active only when the context has active	matching profile
* specifying custom configuration classes - `@ContextConfiguration` annotation

## 2. How can you exclude autoconfiguration from a test?
* by listing unwanted configuration classes to be excluded, as a value of `exclude` attribute of `@SpringBootTest`
* using `@ContextConfiguration` and providing custom `@Configuration` class, which can exclude given classes from auto configuration
  `@EnableAutoConfiguration(exclude = {})`

## 3. How many application contexts can be cached when running tests? What are the side effects if we increase the cache size ? What would be the side effects if there was no caching ?
The maximum size of cached application contexts is 32. Increasing the size of cache increases also CPU and memory usage.
On the other hand, when there is no cache, the tests can take long time.

## 4. Can @MockBean be used if the bean is not already defined in the application context?
Yes