package com.rvdijkz.tour.api.controller;

import com.rvdijkz.tour.api.model.HealthResponse;
import java.time.OffsetDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthApiController implements HealthApi {

    private static final String STATUS_UP = "UP";

    @Override
    public ResponseEntity<HealthResponse> getHealth() {
        HealthResponse response = new HealthResponse(STATUS_UP, OffsetDateTime.now());
        return ResponseEntity.ok(response);
    }
}

