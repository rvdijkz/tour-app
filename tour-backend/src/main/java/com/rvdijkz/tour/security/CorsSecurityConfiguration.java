package com.rvdijkz.tour.security;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsSecurityConfiguration {

    private static final String CORS_PATTERN_ALL = "/**";

    private static final List<String> ALLOWED_HEADERS = List.of(
        "Authorization",
        "Content-Type",
        "Accept",
        "X-Requested-With"
    );

    private static final List<String> ALLOWED_METHODS = List.of(
        "GET",
        "POST",
        "PUT",
        "PATCH",
        "DELETE",
        "OPTIONS"
    );

    private final List<String> allowedOrigins;

    public CorsSecurityConfiguration(
        @Value("${tour.security.cors.allowed-origins:http://localhost:5173}") String allowedOriginsCsv
    ) {
        this.allowedOrigins = parseAllowedOrigins(allowedOriginsCsv);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(ALLOWED_METHODS);
        configuration.setAllowedHeaders(ALLOWED_HEADERS);
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(CORS_PATTERN_ALL, configuration);
        return source;
    }

    private List<String> parseAllowedOrigins(String allowedOriginsCsv) {
        return Arrays.stream(allowedOriginsCsv.split(","))
            .map(String::trim)
            .filter(origin -> !origin.isBlank())
            .collect(Collectors.toList());
    }
}

