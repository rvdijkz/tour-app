package com.rvdijkz.tour.domain.service;

public class SystemInfoDomainService {

    public String normalizeVersion(String version) {
        if (version == null || version.isBlank()) {
            return "unknown";
        }
        return version;
    }
}


