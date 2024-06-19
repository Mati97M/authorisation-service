package contextConfig;

import lombok.SneakyThrows;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@ConfigurationPropertiesBinding
public class DateConverter implements Converter<String, Date> {
    @SneakyThrows
    @Override
    public Date convert(String source) {
        if (source == null) {
            return null;
        }
        return new SimpleDateFormat("yyyy-MM-dd").parse(source);
    }
}