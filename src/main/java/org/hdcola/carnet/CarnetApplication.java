package org.hdcola.carnet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(exclude = {
        com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration.class
})
public class CarnetApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarnetApplication.class, args);
    }

}
