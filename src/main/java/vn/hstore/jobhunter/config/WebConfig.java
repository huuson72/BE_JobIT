package vn.hstore.jobhunter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${hstore.upload-file.base-uri}")
    private String uploadBaseUri;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Xử lý đường dẫn từ file:///D:/Sping_REST/upload/
        String uploadDir = uploadBaseUri.replace("file:///", "file:/");
        
        registry.addResourceHandler("/api/uploads/**")
                .addResourceLocations(uploadDir);
    }
} 