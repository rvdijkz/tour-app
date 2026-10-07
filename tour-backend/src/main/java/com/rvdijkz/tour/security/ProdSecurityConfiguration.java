package com.rvdijkz.tour.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("prod")
public class ProdSecurityConfiguration {

    private final String adminAuthority;

    public ProdSecurityConfiguration(@Value("${tour.security.admin-authority:ROLE_ADMIN}") String adminAuthority) {
        this.adminAuthority = adminAuthority;
    }

    @Bean
    SecurityFilterChain prodSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(SecurityRoutePatterns.PUBLIC_ENDPOINTS).permitAll()
                .requestMatchers(SecurityRoutePatterns.PLAYER_ENDPOINTS).authenticated()
                .requestMatchers(SecurityRoutePatterns.ADMIN_ENDPOINTS).hasAuthority(adminAuthority)
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return httpSecurity.build();
    }
}

