package iuh.fit.elearning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ELearningApplication {

    public static void main(String[] args) {
        SpringApplication.run(ELearningApplication.class, args);
    }

}
