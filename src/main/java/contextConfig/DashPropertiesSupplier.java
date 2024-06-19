package contextConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@ConfigurationPropertiesScan
@Profile("dash")
@Component
public class DashPropertiesSupplier implements Supplier<String[]> {
    @Value("#{'${dashedProperty}'.split('-')}")
    private String[] array;

    @Override
    public String[] get() {
        return array;
    }
}