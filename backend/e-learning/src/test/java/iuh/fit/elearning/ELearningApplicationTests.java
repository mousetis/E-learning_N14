package iuh.fit.elearning;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Cần PostgreSQL + Redis đang chạy (xem .env.example). Bỏ @Disabled khi chạy tích hợp trên máy có đủ hạ tầng.
 */
@Disabled("Cần PostgreSQL và Redis; bỏ chú thích này khi chạy kiểm thử tích hợp")
@SpringBootTest
class ELearningApplicationTests {

    @Test
    void contextLoads() {
    }

}
