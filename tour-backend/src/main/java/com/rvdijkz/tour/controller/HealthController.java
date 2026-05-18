package com.rvdijkz.tour.controller;

import com.rvdijkz.tour.application.port.in.GetHealthUseCase;
import com.rvdijkz.tour.application.port.in.GetSystemInfoUseCase;
import com.rvdijkz.tour.generated.api.SystemApi;
import com.rvdijkz.tour.generated.model.HealthResponse;
import com.rvdijkz.tour.generated.model.SystemInfoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController implements SystemApi {

    private final GetHealthUseCase getHealthUseCase;
    private final GetSystemInfoUseCase getSystemInfoUseCase;

    public HealthController(GetHealthUseCase getHealthUseCase, GetSystemInfoUseCase getSystemInfoUseCase) {
        this.getHealthUseCase = getHealthUseCase;
        this.getSystemInfoUseCase = getSystemInfoUseCase;
    }

    @Override
    @GetMapping("/api/v1/health")
    @PreAuthorize("hasAuthority('SCOPE_tour.read')")
    public ResponseEntity<HealthResponse> getHealth() {
        GetHealthUseCase.HealthState healthState = getHealthUseCase.getHealth();
        HealthResponse response = new HealthResponse(healthState.status(), healthState.timestamp());
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/api/v1/info")
    @PreAuthorize("hasAuthority('SCOPE_tour.read')")
    public ResponseEntity<SystemInfoResponse> getSystemInfo() {
        GetSystemInfoUseCase.SystemInfo systemInfo = getSystemInfoUseCase.getSystemInfo();
        SystemInfoResponse response = new SystemInfoResponse(systemInfo.serviceName(), systemInfo.version());
        return ResponseEntity.ok(response);
    }
}


