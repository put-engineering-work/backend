package work;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableConfigurationProperties
@EnableCaching
public class LeisureLinkBackend {
    public static void main(String[] args) {
        SpringApplication.run(LeisureLinkBackend.class, args);
    }
}
