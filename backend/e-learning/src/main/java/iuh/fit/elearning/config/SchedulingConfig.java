package iuh.fit.elearning.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Bật @Scheduled (hết hạn đơn hàng PENDING quá 15 phút...). */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
