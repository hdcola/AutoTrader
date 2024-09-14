package org.hdcola.carnet;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class CarnetApplication {

    public static void main(String[] args) {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        log.debug("Environment variables loaded from .env file:");
        log.debug(dotenv.toString());
        // Set environment variables
        dotenv.entries().forEach(
                entry -> System.setProperty(entry.getKey(), entry.getValue())
        );

        SpringApplication.run(CarnetApplication.class, args);
    }

}
