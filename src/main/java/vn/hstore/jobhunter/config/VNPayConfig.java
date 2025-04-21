package vn.hstore.jobhunter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter
@Setter
public class VNPayConfig {
    
    @Value("${vnpay.tmnCode}")
    private String tmnCode;
    
    @Value("${vnpay.hashSecret}")
    private String hashSecret;
    
    @Value("${vnpay.url}")
    private String url;
    
    @Value("${vnpay.returnUrl}")
    private String returnUrl;
    
    @Value("${vnpay.frontendUrl}")
    private String frontendUrl;
    
    @Value("${vnpay.timeZone}")
    private String timeZone;
} 