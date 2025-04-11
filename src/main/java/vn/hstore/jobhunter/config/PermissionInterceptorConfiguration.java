package vn.hstore.jobhunter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {

    @Bean
    PermissionInterceptor getPermissionInterceptor() {
        return new PermissionInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] whiteList = {
            "/", "/api/v1/auth/**", "/storage/**",
            "/api/v1/companies/**", "/api/v1/jobs/**", "/api/v1/skills/**", "/api/v1/files",
            "/api/v1/resumes/**", "/api/v1/gencv/export/**",
            "/api/v1/subscribers/**", "/api/v1/users/profile", "/api/v1/users/change-password",
            "/api/v1/employers/verification-status"
        };
        registry.addInterceptor(getPermissionInterceptor())
                .excludePathPatterns(whiteList)
                .addPathPatterns("/api/v1/employers/verification-status")
                .excludePathPatterns("/api/v1/employers/verification-status");
    }
}
