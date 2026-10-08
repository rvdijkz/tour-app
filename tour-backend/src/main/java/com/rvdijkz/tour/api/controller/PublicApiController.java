package com.rvdijkz.tour.api.controller;

import com.rvdijkz.tour.api.model.EditionSummary;
import com.rvdijkz.tour.api.model.PlayerStandingDetail;
import com.rvdijkz.tour.api.model.RiderListResponse;
import com.rvdijkz.tour.api.model.StandingsResponse;
import com.rvdijkz.tour.application.PublicApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicApiController implements PublicApi {

    private final PublicApiService publicApiService;

    public PublicApiController(PublicApiService publicApiService) {
        this.publicApiService = publicApiService;
    }

    @Override
    public ResponseEntity<EditionSummary> getCurrentEdition() {
        return ResponseEntity.ok(publicApiService.getCurrentEdition());
    }

    @Override
    public ResponseEntity<RiderListResponse> listRiders(Long editionId) {
        return ResponseEntity.ok(publicApiService.listRiders(editionId));
    }

    @Override
    public ResponseEntity<StandingsResponse> getStandings(Long editionId) {
        return ResponseEntity.ok(publicApiService.getStandings(editionId));
    }

    @Override
    public ResponseEntity<PlayerStandingDetail> getPlayerStandingDetail(Long editionId, Long playerId) {
        return ResponseEntity.ok(publicApiService.getPlayerStandingDetail(editionId, playerId));
    }
}

