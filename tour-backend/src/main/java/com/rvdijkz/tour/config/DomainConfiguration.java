package com.rvdijkz.tour.config;

import com.rvdijkz.tour.domain.service.HealthDomainService;
import com.rvdijkz.tour.domain.service.SystemInfoDomainService;
import com.rvdijkz.tour.domain.service.WelcomeDomainService;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfiguration {

    @Bean
    public WelcomeDomainService welcomeDomainService() {
        return new WelcomeDomainService();
    }

    @Bean
    public HealthDomainService healthDomainService() {
        return new HealthDomainService();
    }

    @Bean
    public SystemInfoDomainService systemInfoDomainService() {
        return new SystemInfoDomainService();
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}

