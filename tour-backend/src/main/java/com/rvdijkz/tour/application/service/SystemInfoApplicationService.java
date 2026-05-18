package com.rvdijkz.tour.application.service;

import com.rvdijkz.tour.application.port.in.GetSystemInfoUseCase;
import com.rvdijkz.tour.config.ApplicationInfoProperties;
import com.rvdijkz.tour.domain.service.SystemInfoDomainService;
import org.springframework.stereotype.Service;

@Service
public class SystemInfoApplicationService implements GetSystemInfoUseCase {

    private final ApplicationInfoProperties applicationInfoProperties;
    private final SystemInfoDomainService systemInfoDomainService;

    public SystemInfoApplicationService(
            ApplicationInfoProperties applicationInfoProperties,
            SystemInfoDomainService systemInfoDomainService
    ) {
        this.applicationInfoProperties = applicationInfoProperties;
        this.systemInfoDomainService = systemInfoDomainService;
    }

    @Override
    public SystemInfo getSystemInfo() {
        return new SystemInfo(
                applicationInfoProperties.getServiceName(),
                systemInfoDomainService.normalizeVersion(applicationInfoProperties.getVersion())
        );
    }
}

