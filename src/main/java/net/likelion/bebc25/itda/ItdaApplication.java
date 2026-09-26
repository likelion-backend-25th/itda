package net.likelion.bebc25.itda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ItdaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItdaApplication.class, args);
    }
}
