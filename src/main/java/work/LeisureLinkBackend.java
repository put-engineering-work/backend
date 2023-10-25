package work;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
public class LeisureLinkBackend {


    public static void main(String[] args) {
        SpringApplication.run(LeisureLinkBackend.class, args);
    }

}
