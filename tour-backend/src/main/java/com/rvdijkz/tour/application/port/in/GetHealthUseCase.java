package com.rvdijkz.tour.application.port.in;

import java.time.OffsetDateTime;

public interface GetHealthUseCase {

    HealthState getHealth();

    record HealthState(String status, OffsetDateTime timestamp) {
    }
}

