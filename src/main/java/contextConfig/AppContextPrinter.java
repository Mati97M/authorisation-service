package contextConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Component
public class AppContextPrinter implements CommandLineRunner {
    private final ConfigurableEnvironment environment;
    private final ApplicationContext applicationContext;

    @Override
    public void run(String... args) throws Exception {
        printContext();
    }

    private void printContext() {
        environment.getPropertySources()
                .stream()
                .filter(ps -> ps instanceof MapPropertySource)
                .map(ps -> ((MapPropertySource) ps).getSource().keySet())
                .flatMap(Collection::stream)
                .distinct()
                .sorted()
                .forEach(key -> log.info("{}={}", key, environment.getProperty(key)));
        for (String beanName : applicationContext.getBeanDefinitionNames()) {
            log.info("beanName = " + beanName);
        }
    }
}