package com.rvdijkz.tour.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tour.app")
public class ApplicationInfoProperties {

    private String serviceName = "tour-backend";
    private String version = "0.0.1-SNAPSHOT";

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

