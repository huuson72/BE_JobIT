package vn.hstore.jobhunter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StaticResourcesWebConfiguration
        implements WebMvcConfigurer {

    @Value("${hstore.upload-file.base-uri}")
    private String baseURI;

    @PostConstruct
    public void init() {
        try {
            // Chuyển đổi từ file:/// URI sang đường dẫn file system
            String path = baseURI.replace("file:///", "/");
            
            // Tạo các thư mục cần thiết
            createDirectoryIfNotExists(path);
            createDirectoryIfNotExists(path + "company");
            createDirectoryIfNotExists(path + "user");
            createDirectoryIfNotExists(path + "cv");
            
            System.out.println("Upload directories initialized successfully at: " + path);
        } catch (Exception e) {
            System.err.println("Error initializing upload directories: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createDirectoryIfNotExists(String directoryPath) {
        try {
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
                System.out.println("Created directory: " + directoryPath);
            }
        } catch (Exception e) {
            System.err.println("Error creating directory " + directoryPath + ": " + e.getMessage());
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/storage/**")
                .addResourceLocations(baseURI);
    }
}
