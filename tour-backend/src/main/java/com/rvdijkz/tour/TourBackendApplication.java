package com.rvdijkz.tour;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TourBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TourBackendApplication.class, args);
    }
}

