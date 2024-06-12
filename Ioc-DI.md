# Inversion of control and Dependency Injection

## What is the ApplicationContext ? 
- It is an object, which manages the beans. Supports Constructor and Setter base Dependency Injection. Helps to detect configurations issues - but it costs some extra time on startup - every Bean is instantiated.
## What are the tradeoffs of different approaches to injecting beans ?
- Setter DI -> setter methods make objects of that class amenable to reconfiguration or re-injection later.
- Constructor DI -> lets you implement application components as immutable objects and ensures that required dependencies are not null
- Properties DI - very slow due to reflection usage, hard to test 
## Why do we need to use @Qualifier when multiple of the same type are defined ?
- Thanks to @Qualifier, Spring knows which component to choose from multiple options.
## How to avoid loading of heavy beans (like caches or other beans with heavy init logic) on startup and decrease startup time?
- By default, Spring creates all singleton beans eagerly at the startup/bootstrapping of the application context. To load beans lazily, we can put @Lazy on:
1) the method which are annotated with @Bean.
2) any component class -> and on @Autowired places
## What are Spring lifecycle stages and methods?
1) Bean creation
2) Populating properties and calling methods form Aware interfaces, if implemented
3) Pre-initialization with BeanPostProcessors for every bean
4) After initialization phase: calling methods (if implemented), in order:
* @PostConstruct annotated methods 
* afterPropertiesSet() of the InitializingBean interface 
* init() method (declared in the configuration file)
5) Post-Initialization phase: BeanPostProcessors methods for all beans. Method: postProcessAfterInitialization(Object bean, String beanName)
6) Bean is ready
7) Upon destruction of bean calling in order:
* @PreDestroy annotated method
* destroy() method from DisposableBean interface
* method from configuration file