package vn.hstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import de.codecentric.boot.admin.server.config.EnableAdminServer;

@SpringBootApplication
@EnableAdminServer
public class JobitApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobitApplication.class, args);
    }
} 