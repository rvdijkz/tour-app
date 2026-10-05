package com.rvdijkz.tour.application;

import com.rvdijkz.tour.api.model.CalculationStatus;
import com.rvdijkz.tour.api.model.ClassificationPrediction;
import com.rvdijkz.tour.api.model.ClassificationType;
import com.rvdijkz.tour.api.model.ClearedDataSummary;
import com.rvdijkz.tour.api.model.CreateEditionResponse;
import com.rvdijkz.tour.api.model.DeadlineConfigurationResponse;
import com.rvdijkz.tour.api.model.DoublePointsStagesConfigurationResponse;
import com.rvdijkz.tour.api.model.EditionSummary;
import com.rvdijkz.tour.api.model.EntryResponse;
import com.rvdijkz.tour.api.model.EntryStatus;
import com.rvdijkz.tour.api.model.FinalResultsResponse;
import com.rvdijkz.tour.api.model.ImportStatus;
import com.rvdijkz.tour.api.model.PlayerAdminResponse;
import com.rvdijkz.tour.api.model.PlayerStandingDetail;
import com.rvdijkz.tour.api.model.PredictionsResponse;
import com.rvdijkz.tour.api.model.RequiredNationalityConfigurationResponse;
import com.rvdijkz.tour.api.model.ReserveBudgetConfigurationResponse;
import com.rvdijkz.tour.api.model.ResetPasswordResponse;
import com.rvdijkz.tour.api.model.RiderImportResponse;
import com.rvdijkz.tour.api.model.RiderListResponse;
import com.rvdijkz.tour.api.model.RiderSummary;
import com.rvdijkz.tour.api.model.StageCalculationResponse;
import com.rvdijkz.tour.api.model.StageHistoryItem;
import com.rvdijkz.tour.api.model.StageResultResponse;
import com.rvdijkz.tour.api.model.StandingRow;
import com.rvdijkz.tour.api.model.StandingsResponse;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public final class SampleDataFactory {

    public static final long DEFAULT_EDITION_ID = 1L;
    public static final long DEFAULT_PLAYER_ID = 1L;
    public static final String DEFAULT_NATIONALITY = "FRA";

    private static final int DEFAULT_YEAR = 2026;
    private static final int DEFAULT_RESERVE_BUDGET = 20;
    private static final int DEFAULT_CURRENT_STAGE = 0;

    private SampleDataFactory() {
    }

    public static EditionSummary editionSummary(long editionId) {
        return new EditionSummary(
            editionId,
            DEFAULT_YEAR,
            OffsetDateTime.now().plusDays(30),
            DEFAULT_RESERVE_BUDGET,
            DEFAULT_NATIONALITY,
            DEFAULT_CURRENT_STAGE
        ).doublePointsStages(List.of(5, 12));
    }

    public static RiderListResponse riderListResponse(long editionId) {
        RiderSummary riderOne = new RiderSummary(1L, 1, 1, "Rider One", 25, "NLD");
        RiderSummary riderTwo = new RiderSummary(2L, 2, 2, "Rider Two", 30, "BEL");
        return new RiderListResponse(editionId, List.of(riderOne, riderTwo));
    }

    public static StandingsResponse standingsResponse(long editionId) {
        StandingRow first = new StandingRow(1, DEFAULT_PLAYER_ID, "Player One", "Team One", 100);
        StandingRow second = new StandingRow(2, 2L, "Player Two", "Team Two", 95);
        return new StandingsResponse(editionId, 0, List.of(first, second));
    }

    public static PlayerStandingDetail playerStandingDetail(long editionId, long playerId) {
        StandingRow player = new StandingRow(1, playerId, "Player One", "Team One", 100);
        StageHistoryItem historyItem = new StageHistoryItem(1, 100, 100, List.of(), List.of());
        return new PlayerStandingDetail(editionId, 1, player, List.of(historyItem));
    }

    public static EntryResponse draftEntry(long editionId, long playerId, String teamName, List<Long> riderIds, int finisherCount) {
        return new EntryResponse(editionId, playerId, EntryStatus.DRAFT, teamName, riderIds)
            .finisherCountPrediction(finisherCount);
    }

    public static EntryResponse submittedEntry(long editionId, long playerId, String teamName, List<Long> riderIds, int finisherCount) {
        return new EntryResponse(editionId, playerId, EntryStatus.SUBMITTED, teamName, riderIds)
            .finisherCountPrediction(finisherCount)
            .submittedAt(OffsetDateTime.now());
    }

    public static PredictionsResponse predictionsResponse(long editionId, long playerId, List<ClassificationPrediction> predictions) {
        return new PredictionsResponse(editionId, playerId, predictions);
    }

    public static List<Long> defaultRiderIds() {
        List<Long> riderIds = new ArrayList<>();
        for (long i = 1; i <= 14; i++) {
            riderIds.add(i);
        }
        return riderIds;
    }

    public static List<ClassificationPrediction> defaultPredictions() {
        return List.of(
            prediction(ClassificationType.GENERAL, List.of(1L, 2L, 3L, 4L, 5L)),
            prediction(ClassificationType.POINTS, List.of(6L, 7L, 8L, 9L, 10L)),
            prediction(ClassificationType.MOUNTAINS, List.of(11L, 12L, 13L, 14L, 15L))
        );
    }

    public static CreateEditionResponse createEditionResponse(int year) {
        ClearedDataSummary cleared = new ClearedDataSummary(true, true, true, true);
        return new CreateEditionResponse(DEFAULT_EDITION_ID, year, OffsetDateTime.now(), cleared);
    }

    public static PlayerAdminResponse playerAdminResponse(long playerId, String username) {
        return new PlayerAdminResponse(playerId, username, OffsetDateTime.now().plusHours(12));
    }

    public static ResetPasswordResponse resetPasswordResponse(long playerId) {
        return new ResetPasswordResponse(playerId, "TempPass123!", OffsetDateTime.now().plusHours(12));
    }

    public static RiderImportResponse riderImportResponse() {
        return new RiderImportResponse(ImportStatus.ACCEPTED, 0);
    }

    public static DeadlineConfigurationResponse deadlineConfigurationResponse(long editionId, OffsetDateTime entryDeadline) {
        return new DeadlineConfigurationResponse(editionId, entryDeadline, true);
    }

    public static ReserveBudgetConfigurationResponse reserveBudgetConfigurationResponse(long editionId, int reserveBudget) {
        return new ReserveBudgetConfigurationResponse(editionId, reserveBudget, false);
    }

    public static RequiredNationalityConfigurationResponse requiredNationalityConfigurationResponse(long editionId, String nationality) {
        return new RequiredNationalityConfigurationResponse(editionId, nationality, false);
    }

    public static DoublePointsStagesConfigurationResponse doublePointsStagesConfigurationResponse(long editionId, List<Integer> stages) {
        return new DoublePointsStagesConfigurationResponse(editionId, stages, false);
    }

    public static StageResultResponse stageResultResponse(long editionId, int stageNumber) {
        return new StageResultResponse(editionId, stageNumber, OffsetDateTime.now());
    }

    public static StageCalculationResponse stageCalculationResponse(long editionId, int stageNumber) {
        return new StageCalculationResponse(editionId, stageNumber, CalculationStatus.SUCCESS).published(true);
    }

    public static FinalResultsResponse finalResultsResponse(long editionId) {
        return new FinalResultsResponse(editionId, CalculationStatus.SUCCESS, true);
    }

    private static ClassificationPrediction prediction(ClassificationType type, List<Long> riderIds) {
        return new ClassificationPrediction(type, riderIds);
    }
}

