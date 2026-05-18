package com.rvdijkz.tour.application.service;

import com.rvdijkz.tour.application.port.in.GetHealthUseCase;
import com.rvdijkz.tour.domain.service.HealthDomainService;
import java.time.Clock;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Service;

@Service
public class HealthApplicationService implements GetHealthUseCase {

    private final HealthDomainService healthDomainService;
    private final Clock clock;

    public HealthApplicationService(HealthDomainService healthDomainService, Clock clock) {
        this.healthDomainService = healthDomainService;
        this.clock = clock;
    }

    @Override
    public HealthState getHealth() {
        return new HealthState(healthDomainService.currentStatus(), OffsetDateTime.now(clock));
    }
}

