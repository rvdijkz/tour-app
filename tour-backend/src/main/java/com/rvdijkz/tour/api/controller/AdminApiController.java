package com.rvdijkz.tour.api.controller;

import com.rvdijkz.tour.api.model.CreateEditionRequest;
import com.rvdijkz.tour.api.model.CreateEditionResponse;
import com.rvdijkz.tour.api.model.CreatePlayerRequest;
import com.rvdijkz.tour.api.model.DeadlineConfigurationResponse;
import com.rvdijkz.tour.api.model.DoublePointsStagesConfigurationResponse;
import com.rvdijkz.tour.api.model.FinalResultsResponse;
import com.rvdijkz.tour.api.model.PlayerAdminResponse;
import com.rvdijkz.tour.api.model.RequiredNationalityConfigurationResponse;
import com.rvdijkz.tour.api.model.ReserveBudgetConfigurationResponse;
import com.rvdijkz.tour.api.model.ResetPasswordRequest;
import com.rvdijkz.tour.api.model.ResetPasswordResponse;
import com.rvdijkz.tour.api.model.RiderImportResponse;
import com.rvdijkz.tour.api.model.SaveFinalResultsRequest;
import com.rvdijkz.tour.api.model.SaveStageResultRequest;
import com.rvdijkz.tour.api.model.StageCalculationResponse;
import com.rvdijkz.tour.api.model.StageResultResponse;
import com.rvdijkz.tour.api.model.UpdateDeadlineRequest;
import com.rvdijkz.tour.api.model.UpdateDoublePointsStagesRequest;
import com.rvdijkz.tour.api.model.UpdateRequiredNationalityRequest;
import com.rvdijkz.tour.api.model.UpdateReserveBudgetRequest;
import com.rvdijkz.tour.application.AdminApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class AdminApiController implements AdminApi {

    private final AdminApiService adminApiService;

    public AdminApiController(AdminApiService adminApiService) {
        this.adminApiService = adminApiService;
    }

    @Override
    public ResponseEntity<CreateEditionResponse> createEdition(CreateEditionRequest createEditionRequest) {
        return ResponseEntity.status(201).body(adminApiService.createEdition(createEditionRequest));
    }

    @Override
    public ResponseEntity<PlayerAdminResponse> createPlayer(CreatePlayerRequest createPlayerRequest) {
        return ResponseEntity.status(201).body(adminApiService.createPlayer(createPlayerRequest));
    }

    @Override
    public ResponseEntity<ResetPasswordResponse> resetPlayerPassword(Long playerId, ResetPasswordRequest resetPasswordRequest) {
        return ResponseEntity.ok(adminApiService.resetPlayerPassword(playerId, resetPasswordRequest));
    }

    @Override
    public ResponseEntity<RiderImportResponse> importRiders(Long editionId, MultipartFile file) {
        return ResponseEntity.ok(adminApiService.importRiders(editionId, file));
    }

    @Override
    public ResponseEntity<DeadlineConfigurationResponse> updateEditionDeadline(
        Long editionId,
        UpdateDeadlineRequest updateDeadlineRequest
    ) {
        return ResponseEntity.ok(adminApiService.updateEditionDeadline(editionId, updateDeadlineRequest));
    }

    @Override
    public ResponseEntity<ReserveBudgetConfigurationResponse> updateReserveBudget(
        Long editionId,
        UpdateReserveBudgetRequest updateReserveBudgetRequest
    ) {
        return ResponseEntity.ok(adminApiService.updateReserveBudget(editionId, updateReserveBudgetRequest));
    }

    @Override
    public ResponseEntity<RequiredNationalityConfigurationResponse> updateRequiredNationality(
        Long editionId,
        UpdateRequiredNationalityRequest updateRequiredNationalityRequest
    ) {
        return ResponseEntity.ok(adminApiService.updateRequiredNationality(editionId, updateRequiredNationalityRequest));
    }

    @Override
    public ResponseEntity<DoublePointsStagesConfigurationResponse> updateDoublePointsStages(
        Long editionId,
        UpdateDoublePointsStagesRequest updateDoublePointsStagesRequest
    ) {
        return ResponseEntity.ok(adminApiService.updateDoublePointsStages(editionId, updateDoublePointsStagesRequest));
    }

    @Override
    public ResponseEntity<StageResultResponse> saveStageResult(
        Long editionId,
        Integer stageNumber,
        SaveStageResultRequest saveStageResultRequest
    ) {
        return ResponseEntity.ok(adminApiService.saveStageResult(editionId, stageNumber, saveStageResultRequest));
    }

    @Override
    public ResponseEntity<StageCalculationResponse> correctStageResult(
        Long editionId,
        Integer stageNumber,
        SaveStageResultRequest saveStageResultRequest
    ) {
        return ResponseEntity.ok(adminApiService.correctStageResult(editionId, stageNumber, saveStageResultRequest));
    }

    @Override
    public ResponseEntity<StageCalculationResponse> calculateStage(Long editionId, Integer stageNumber) {
        return ResponseEntity.ok(adminApiService.calculateStage(editionId, stageNumber));
    }

    @Override
    public ResponseEntity<FinalResultsResponse> saveFinalResults(
        Long editionId,
        SaveFinalResultsRequest saveFinalResultsRequest
    ) {
        return ResponseEntity.ok(adminApiService.saveFinalResults(editionId, saveFinalResultsRequest));
    }

    @Override
    public ResponseEntity<FinalResultsResponse> correctFinalResults(
        Long editionId,
        SaveFinalResultsRequest saveFinalResultsRequest
    ) {
        return ResponseEntity.ok(adminApiService.correctFinalResults(editionId, saveFinalResultsRequest));
    }
}

