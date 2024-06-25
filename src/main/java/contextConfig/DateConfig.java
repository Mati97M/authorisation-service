package contextConfig;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Date;

@AllArgsConstructor
@Configuration
public class DateConfig {

    @SneakyThrows
    @Bean
    Date date(@Value("${date}") String source) {
        return new SimpleDateFormat("yyyy-MM-dd").parse(source);
    }
}
