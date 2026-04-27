package com.stemlink.skillmentor.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${CORS_ALLOWED_ORIGINS:http://localhost:3001,http://localhost:5173,http://localhost:8080,https://skill-mentor-frontend-final.vercel.app}")
    private String allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 1. Parse origins and allow for wildcards in subdomains
        List<String> origins = Arrays.asList(allowedOrigins.split(","));



        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3001",
                "http://localhost:5173",
                "https://skill-mentor-frontend-final.vercel.app",
                "https://skill-mentor-frontend-final-*.vercel.app"
        ));

        // 2. Standard HTTP Methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // 3. Headers - Be explicit when allowCredentials is true
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        // 4. Exposed Headers for the Frontend
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "X-Rate-Limit-Remaining",
                "X-Rate-Limit-Retry-After-Seconds",
                "Retry-After"
        ));

        // 5. Security & Performance
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // 1 hour cache for preflight

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}