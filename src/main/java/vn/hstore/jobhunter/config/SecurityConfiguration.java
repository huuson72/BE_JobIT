package vn.hstore.jobhunter.config;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;

import vn.hstore.jobhunter.util.SecurityUtil;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    @Value("${hstore.jwt.base64-secret}")
    private String jwtKey;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint) throws Exception {

        String[] whiteList = {
            "/",
            "/api/v1/auth/login", "/api/v1/auth/refresh", "/api/v1/auth/register", "/api/v1/auth/register-employer", "/api/v1/auth/employer-register",
            "/api/status",
            "/storage/**",
            "/api/v1/email/**",
            "/api/v1/files", "/api/v1/files/**",
            "/api/v1/packages", "/api/v1/packages/*",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api/v1/payments/vnpay-callback"
        };

        http
                .csrf(c -> c.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authz -> authz
                // Whitelist - luôn cho phép
                .requestMatchers(whiteList).permitAll()
                // Public APIs - không cần xác thực
                .requestMatchers(HttpMethod.GET, "/api/v1/packages").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/packages/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/companies/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/jobs/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/skills/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/promotions/active").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/promotions").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/promotions/calculate-discount/{packageId}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/promotions/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/promotions/package/{packageId}").permitAll()
                // APIs cần xác thực người dùng
                .requestMatchers(HttpMethod.PUT, "/api/v1/users/{id}/info").authenticated()
                .requestMatchers("/api/v1/cvs/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/gencv/create").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/gencv/preview/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/gencv/download/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/employer/subscribe").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/employer/{userId}/subscriptions").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/employer/{userId}/company/{companyId}/status").authenticated()
                .requestMatchers("/api/favorites/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/reviews").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/jobs").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/v1/jobs").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/jobs/**").authenticated()
                // APIs chỉ dành cho admin
                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN", "HR", "MANAGER")
                .requestMatchers(HttpMethod.POST, "/api/v1/packages").hasAnyRole("ADMIN", "SUPER_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/packages/{id}").hasAnyRole("ADMIN", "SUPER_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/packages/{id}").hasAnyRole("ADMIN", "SUPER_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/packages/all").hasAnyRole("ADMIN", "SUPER_ADMIN")
                // Mặc định cần xác thực cho tất cả API còn lại
                .anyRequest().authenticated())
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults())
                .authenticationEntryPoint(customAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("permission");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
        return token -> {
            try {
                return jwtDecoder.decode(token);
            } catch (Exception e) {
                System.out.println(">>> JWT error: " + e.getMessage());
                throw e;
            }
        };
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length,
                SecurityUtil.JWT_ALGORITHM.getName());
    }

}
