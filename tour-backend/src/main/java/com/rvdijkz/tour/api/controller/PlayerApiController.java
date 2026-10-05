package com.rvdijkz.tour.api.controller;

import com.rvdijkz.tour.api.model.EntryResponse;
import com.rvdijkz.tour.api.model.PredictionsResponse;
import com.rvdijkz.tour.api.model.SaveEntryDraftRequest;
import com.rvdijkz.tour.api.model.SavePredictionsRequest;
import com.rvdijkz.tour.application.PlayerApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerApiController implements PlayerApi {

    private final PlayerApiService playerApiService;

    public PlayerApiController(PlayerApiService playerApiService) {
        this.playerApiService = playerApiService;
    }

    @Override
    public ResponseEntity<EntryResponse> getCurrentEntry(Long editionId) {
        return ResponseEntity.ok(playerApiService.getCurrentEntry(editionId));
    }

    @Override
    public ResponseEntity<EntryResponse> saveEntryDraft(Long editionId, SaveEntryDraftRequest saveEntryDraftRequest) {
        return ResponseEntity.ok(playerApiService.saveEntryDraft(editionId, saveEntryDraftRequest));
    }

    @Override
    public ResponseEntity<EntryResponse> submitEntry(Long editionId) {
        return ResponseEntity.ok(playerApiService.submitEntry(editionId));
    }

    @Override
    public ResponseEntity<PredictionsResponse> getCurrentPredictions(Long editionId) {
        return ResponseEntity.ok(playerApiService.getCurrentPredictions(editionId));
    }

    @Override
    public ResponseEntity<PredictionsResponse> savePredictions(Long editionId, SavePredictionsRequest savePredictionsRequest) {
        return ResponseEntity.ok(playerApiService.savePredictions(editionId, savePredictionsRequest));
    }
}

