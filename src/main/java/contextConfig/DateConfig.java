package contextConfig;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@AllArgsConstructor
@Configuration
public class DateConfig {
    private DateConverter dateConverter;

    @Bean
    Date date(@Value("${date}") String source) {
        return dateConverter.convert(source);
    }
}
