package contextConfig;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Supplier;

@AllArgsConstructor
@Component
public class DateSupplier implements Supplier<Date> {
    private Date someDate;
    @Override
    public Date get() {
        return someDate;
    }
}
