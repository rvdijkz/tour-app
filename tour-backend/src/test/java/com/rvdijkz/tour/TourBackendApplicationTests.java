package com.rvdijkz.tour;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rvdijkz.tour.api.error.ApiException;
import com.rvdijkz.tour.api.model.BibPlacement;
import com.rvdijkz.tour.api.model.ClassificationPrediction;
import com.rvdijkz.tour.api.model.ClassificationType;
import com.rvdijkz.tour.api.model.EntryResponse;
import com.rvdijkz.tour.api.model.EntryStatus;
import com.rvdijkz.tour.api.model.FinalResultsResponse;
import com.rvdijkz.tour.api.model.JerseyLeaders;
import com.rvdijkz.tour.api.model.PlayerStandingDetail;
import com.rvdijkz.tour.api.model.PredictionsResponse;
import com.rvdijkz.tour.api.model.SaveEntryDraftRequest;
import com.rvdijkz.tour.api.model.SaveFinalResultsRequest;
import com.rvdijkz.tour.api.model.SavePredictionsRequest;
import com.rvdijkz.tour.api.model.SaveStageResultRequest;
import com.rvdijkz.tour.api.model.StageCalculationResponse;
import com.rvdijkz.tour.api.model.UpdateReserveBudgetRequest;
import com.rvdijkz.tour.application.AdminApiService;
import com.rvdijkz.tour.application.PlayerApiService;
import com.rvdijkz.tour.application.PublicApiService;
import com.rvdijkz.tour.persistence.entity.StageHistoryEntity;
import com.rvdijkz.tour.persistence.entity.StageResultEntity;
import com.rvdijkz.tour.persistence.repository.EditionRepository;
import com.rvdijkz.tour.persistence.repository.PlayerPredictionVersionRepository;
import com.rvdijkz.tour.persistence.repository.StageHistoryRepository;
import com.rvdijkz.tour.persistence.repository.StageResultRepository;
import com.rvdijkz.tour.persistence.repository.StandingRepository;
import com.rvdijkz.tour.persistence.value.PlacedBibData;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class TourBackendApplicationTests {

    private static final long EDITION_ID = 1L;
    private static final String TEST_DATASOURCE_URL = "jdbc:h2:mem:tourdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
    private static final String TEST_DATASOURCE_DRIVER = "org.h2.Driver";
    private static final String TEST_DATASOURCE_USERNAME = "sa";
    private static final String TEST_DATASOURCE_PASSWORD = "";

    @Autowired
    private PlayerApiService playerApiService;

    @Autowired
    private AdminApiService adminApiService;

    @Autowired
    private PublicApiService publicApiService;

    @Autowired
    private PlayerPredictionVersionRepository playerPredictionVersionRepository;

    @Autowired
    private StandingRepository standingRepository;

    @Autowired
    private EditionRepository editionRepository;

    @Autowired
    private StageResultRepository stageResultRepository;

    @Autowired
    private StageHistoryRepository stageHistoryRepository;

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void registerTestProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> TEST_DATASOURCE_URL);
        registry.add("spring.datasource.driver-class-name", () -> TEST_DATASOURCE_DRIVER);
        registry.add("spring.datasource.username", () -> TEST_DATASOURCE_USERNAME);
        registry.add("spring.datasource.password", () -> TEST_DATASOURCE_PASSWORD);
        registry.add("spring.liquibase.url", () -> TEST_DATASOURCE_URL);
        registry.add("spring.liquibase.user", () -> TEST_DATASOURCE_USERNAME);
        registry.add("spring.liquibase.password", () -> TEST_DATASOURCE_PASSWORD);
    }

    @Test
    void saveEntryDraftPersistsIncompleteDraftForCurrentPlayer() {
        SaveEntryDraftRequest request = new SaveEntryDraftRequest("Draft Team")
            .riderIds(List.of(3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L));

        EntryResponse response = playerApiService.saveEntryDraft(EDITION_ID, request);

        assertThat(response.getStatus()).isEqualTo(EntryStatus.DRAFT);
        assertThat(response.getRiderIds()).hasSize(10);
        assertThat(response.getFinisherCountPrediction().isPresent()).isFalse();
    }

    @Test
    void submitEntryPersistsSubmittedEntryAndPredictions() {
        playerApiService.saveEntryDraft(EDITION_ID, validEntryDraftRequest());
        PredictionsResponse savedPredictions = playerApiService.savePredictions(EDITION_ID, validPredictionsRequest());

        EntryResponse response = playerApiService.submitEntry(EDITION_ID);

        assertThat(savedPredictions.getPredictions()).hasSize(3);
        assertThat(response.getStatus()).isEqualTo(EntryStatus.SUBMITTED);
        assertThat(response.getSubmittedAt().isPresent()).isTrue();
        assertThat(playerPredictionVersionRepository.findByIdEditionIdAndIdPlayerIdAndIdStatus(EDITION_ID, 1L, EntryStatus.SUBMITTED)).isPresent();
    }

    @Test
    void updateReserveBudgetIsLockedAfterFirstSavedTeam() {
        playerApiService.saveEntryDraft(EDITION_ID, validEntryDraftRequest());

        assertThatThrownBy(() -> adminApiService.updateReserveBudget(EDITION_ID, new UpdateReserveBudgetRequest(25)))
            .isInstanceOf(ApiException.class)
            .extracting(error -> ((ApiException) error).getCode())
            .isEqualTo("RESERVE_BUDGET_LOCKED");
    }

    @Test
    void calculateStagePublishesStandingsForSubmittedPlayers() {
        playerApiService.saveEntryDraft(EDITION_ID, validEntryDraftRequest());
        playerApiService.savePredictions(EDITION_ID, validPredictionsRequest());
        playerApiService.submitEntry(EDITION_ID);
        adminApiService.saveStageResult(EDITION_ID, 1, validStageResultRequest());

        StageCalculationResponse response = adminApiService.calculateStage(EDITION_ID, 1);

        assertThat(response.getPublished()).isTrue();
        assertThat(editionRepository.findById(EDITION_ID)).get().extracting(edition -> edition.getCurrentStage()).isEqualTo(2);
        assertThat(standingRepository.findByEditionIdAndStageOrderByRankPositionAsc(EDITION_ID, 1))
            .extracting(standing -> standing.getPlayerId())
            .containsExactly(1L);
    }

    @Test
    void calculateStageReusesPreSeededStageHistoryRowWithoutDuplicates() {
        Long seededHistoryId = stageHistoryRepository.findByEditionIdAndPlayerIdAndStage(EDITION_ID, 1L, 1)
            .orElseThrow()
            .getId();

        playerApiService.saveEntryDraft(EDITION_ID, validEntryDraftRequest());
        playerApiService.savePredictions(EDITION_ID, validPredictionsRequest());
        playerApiService.submitEntry(EDITION_ID);
        adminApiService.saveStageResult(EDITION_ID, 1, validStageResultRequest());

        StageCalculationResponse firstRun = adminApiService.calculateStage(EDITION_ID, 1);
        StageCalculationResponse secondRun = adminApiService.calculateStage(EDITION_ID, 1);

        assertThat(firstRun.getPublished()).isTrue();
        assertThat(secondRun.getPublished()).isTrue();

        StageHistoryEntity stageHistory = stageHistoryRepository.findByEditionIdAndPlayerIdAndStage(EDITION_ID, 1L, 1)
            .orElseThrow();
        assertThat(stageHistory.getId()).isEqualTo(seededHistoryId);

        long stageOneRowsForPlayer = stageHistoryRepository.findByEditionIdAndPlayerIdOrderByStageAsc(EDITION_ID, 1L)
            .stream()
            .filter(history -> history.getStage() == 1)
            .count();
        assertThat(stageOneRowsForPlayer).isEqualTo(1L);
    }

    @Test
    void calculateStageAwardsOrdinaryPlacingPointsAndExactPositionBonus() {
        playerApiService.saveEntryDraft(EDITION_ID, validEntryDraftRequest());
        playerApiService.savePredictions(EDITION_ID, validPredictionsRequest());
        playerApiService.submitEntry(EDITION_ID);
        seedSavedStageResult(1, scoringFinishers());

        StageCalculationResponse response = adminApiService.calculateStage(EDITION_ID, 1);

        assertThat(response.getPublished()).isTrue();
        assertThat(stageHistoryRepository.findByEditionIdAndPlayerIdAndStage(EDITION_ID, 1L, 1))
            .get()
            .extracting(StageHistoryEntity::getStagePoints, StageHistoryEntity::getTotalPoints)
            .containsExactly(19, 19);
        assertThat(standingRepository.findByEditionIdAndStageOrderByRankPositionAsc(EDITION_ID, 1))
            .singleElement()
            .extracting(standing -> standing.getTotalPoints())
            .isEqualTo(19);
    }

    @Test
    void calculateStageDoublesOnlyPlacingPointsOnConfiguredDoublePointsStage() {
        playerApiService.saveEntryDraft(EDITION_ID, validEntryDraftRequest());
        playerApiService.savePredictions(EDITION_ID, validPredictionsRequest());
        playerApiService.submitEntry(EDITION_ID);

        for (int stageNumber = 1; stageNumber <= 5; stageNumber++) {
            seedSavedStageResult(stageNumber, scoringFinishers());
        }

        StageCalculationResponse response = adminApiService.calculateStage(EDITION_ID, 5);

        assertThat(response.getPublished()).isTrue();
        assertThat(stageHistoryRepository.findByEditionIdAndPlayerIdAndStage(EDITION_ID, 1L, 5))
            .get()
            .extracting(StageHistoryEntity::getStagePoints, StageHistoryEntity::getTotalPoints)
            .containsExactly(44, 44);
    }

    @Test
    void calculateStageAwardsJerseyPointsForPreviousLeadersAndMostCombativeRider() {
        playerApiService.saveEntryDraft(EDITION_ID, validEntryDraftRequest());
        playerApiService.savePredictions(EDITION_ID, validPredictionsRequest());
        playerApiService.submitEntry(EDITION_ID);

        seedSavedStageResult(
            1,
            scoringFinishers(),
            defaultLastFivePlacings(),
            new JerseyLeaders(3, 3, 4, 16),
            null,
            List.of(5)
        );
        seedSavedStageResult(
            2,
            scoringFinishers(),
            defaultLastFivePlacings(),
            defaultJerseyLeaders(),
            4,
            List.of()
        );

        StageCalculationResponse response = adminApiService.calculateStage(EDITION_ID, 2);

        assertThat(response.getPublished()).isTrue();
        assertThat(stageHistoryRepository.findByEditionIdAndPlayerIdAndStage(EDITION_ID, 1L, 2))
            .get()
            .extracting(StageHistoryEntity::getStagePoints)
            .isEqualTo(40);
    }

    @Test
    void calculateStageDoesNotAwardJerseyOrMostCombativePointsToWithdrawnActiveRider() {
        playerApiService.saveEntryDraft(EDITION_ID, validEntryDraftRequest());
        playerApiService.savePredictions(EDITION_ID, validPredictionsRequest());
        playerApiService.submitEntry(EDITION_ID);

        seedSavedStageResult(
            1,
            scoringFinishers(),
            defaultLastFivePlacings(),
            new JerseyLeaders(3, 1, 14, 15),
            null,
            List.of()
        );
        seedSavedStageResult(
            2,
            scoringFinishers(),
            defaultLastFivePlacings(),
            defaultJerseyLeaders(),
            3,
            List.of(3)
        );

        StageCalculationResponse response = adminApiService.calculateStage(EDITION_ID, 2);

        assertThat(response.getPublished()).isTrue();
        assertThat(stageHistoryRepository.findByEditionIdAndPlayerIdAndStage(EDITION_ID, 1L, 2))
            .get()
            .extracting(StageHistoryEntity::getStagePoints)
            .isEqualTo(3);
    }

    @Test
    void calculateStageAppliesWithdrawalPenaltyInsteadOfPlacingPointsForActiveRider() {
        playerApiService.saveEntryDraft(EDITION_ID, validEntryDraftRequest());
        playerApiService.savePredictions(EDITION_ID, validPredictionsRequest());
        playerApiService.submitEntry(EDITION_ID);
        seedSavedStageResult(1, scoringFinishers(), List.of(3));

        StageCalculationResponse response = adminApiService.calculateStage(EDITION_ID, 1);

        assertThat(response.getPublished()).isTrue();
        assertThat(stageHistoryRepository.findByEditionIdAndPlayerIdAndStage(EDITION_ID, 1L, 1))
            .get()
            .extracting(StageHistoryEntity::getStagePoints, StageHistoryEntity::getTotalPoints)
            .containsExactly(3, 3);
    }

    @Test
    void calculateStageIgnoresWithdrawalOfUnusedReserve() {
        playerApiService.saveEntryDraft(EDITION_ID, validEntryDraftRequest());
        playerApiService.savePredictions(EDITION_ID, validPredictionsRequest());
        playerApiService.submitEntry(EDITION_ID);
        seedSavedStageResult(1, scoringFinishers(), List.of(16));

        StageCalculationResponse response = adminApiService.calculateStage(EDITION_ID, 1);

        assertThat(response.getPublished()).isTrue();
        assertThat(stageHistoryRepository.findByEditionIdAndPlayerIdAndStage(EDITION_ID, 1L, 1))
            .get()
            .extracting(StageHistoryEntity::getStagePoints, StageHistoryEntity::getTotalPoints)
            .containsExactly(19, 19);
    }

    @Test
    void calculateStageAppliesExactWithdrawalPenaltyForActiveReserveOnDoublePointsStage() {
        playerApiService.saveEntryDraft(EDITION_ID, validEntryDraftRequest());
        playerApiService.savePredictions(EDITION_ID, validPredictionsRequest());
        playerApiService.submitEntry(EDITION_ID);

        seedSavedStageResult(1, scoringFinishers(), List.of(5));
        seedSavedStageResult(2, scoringFinishers(), List.of());
        seedSavedStageResult(3, scoringFinishers(), List.of());
        seedSavedStageResult(4, scoringFinishers(), List.of());
        seedSavedStageResult(5, scoringFinishers(), List.of(16));

        StageCalculationResponse response = adminApiService.calculateStage(EDITION_ID, 5);

        assertThat(response.getPublished()).isTrue();
        assertThat(stageHistoryRepository.findByEditionIdAndPlayerIdAndStage(EDITION_ID, 1L, 5))
            .get()
            .extracting(StageHistoryEntity::getStagePoints, StageHistoryEntity::getTotalPoints)
            .containsExactly(42, 42);
    }

    @Test
    void calculateStageAwardsKlunsLastPlaceBonus() {
        playerApiService.saveEntryDraft(EDITION_ID, validEntryDraftRequest());
        playerApiService.savePredictions(EDITION_ID, validPredictionsRequest());
        playerApiService.submitEntry(EDITION_ID);
        seedSavedStageResult(
            1,
            scoringFinishersWithoutKlunsInTopTwenty(),
            lastFivePlacingsWithKlunsLast(),
            defaultJerseyLeaders(),
            null,
            List.of()
        );

        StageCalculationResponse response = adminApiService.calculateStage(EDITION_ID, 1);

        assertThat(response.getPublished()).isTrue();
        assertThat(stageHistoryRepository.findByEditionIdAndPlayerIdAndStage(EDITION_ID, 1L, 1))
            .get()
            .extracting(StageHistoryEntity::getStagePoints, StageHistoryEntity::getTotalPoints)
            .containsExactly(34, 34);
    }

    @Test
    void calculateStageAwardsKlunsWithdrawalBonusInsteadOfPlacingPenalty() {
        playerApiService.saveEntryDraft(EDITION_ID, validEntryDraftRequest());
        playerApiService.savePredictions(EDITION_ID, validPredictionsRequest());
        playerApiService.submitEntry(EDITION_ID);
        seedSavedStageResult(1, scoringFinishers(), List.of(18));

        StageCalculationResponse response = adminApiService.calculateStage(EDITION_ID, 1);

        assertThat(response.getPublished()).isTrue();
        assertThat(stageHistoryRepository.findByEditionIdAndPlayerIdAndStage(EDITION_ID, 1L, 1))
            .get()
            .extracting(StageHistoryEntity::getStagePoints, StageHistoryEntity::getTotalPoints)
            .containsExactly(34, 34);
    }

    @Test
    void getPlayerStandingDetailIncludesSubstitutionsAndPerRiderScoreBreakdown() {
        playerApiService.saveEntryDraft(EDITION_ID, validEntryDraftRequest());
        playerApiService.savePredictions(EDITION_ID, validPredictionsRequest());
        playerApiService.submitEntry(EDITION_ID);

        seedSavedStageResult(
            1,
            scoringFinishers(),
            defaultLastFivePlacings(),
            new JerseyLeaders(3, 3, 4, 16),
            null,
            List.of(5)
        );
        adminApiService.calculateStage(EDITION_ID, 1);

        seedSavedStageResult(
            2,
            scoringFinishers(),
            defaultLastFivePlacings(),
            defaultJerseyLeaders(),
            4,
            List.of()
        );
        adminApiService.calculateStage(EDITION_ID, 2);

        PlayerStandingDetail detail = publicApiService.getPlayerStandingDetail(EDITION_ID, 1L);

        assertThat(detail.getStage()).isEqualTo(2);
        assertThat(detail.getStageHistory()).hasSize(2);

        var stageOne = detail.getStageHistory().getFirst();
        assertThat(stageOne.getSubstitutions()).isEmpty();
        assertThat(stageOne.getRiderScores())
            .extracting(score -> score.getPosition(), score -> score.getPoints())
            .contains(tuple(3, -2), tuple(14, -10));

        var stageTwo = detail.getStageHistory().get(1);
        assertThat(stageTwo.getStagePoints()).isEqualTo(40);
        assertThat(stageTwo.getSubstitutions()).singleElement().satisfies(substitution -> {
            assertThat(substitution.getEffectiveStage()).isEqualTo(2);
            assertThat(substitution.getPosition()).isEqualTo(3);
            assertThat(substitution.getReplacedRider().getRiderId()).isEqualTo(5L);
            assertThat(substitution.getSubstituteRider().getRiderId()).isEqualTo(16L);
        });
        assertThat(stageTwo.getRiderScores())
            .filteredOn(score -> score.getPosition().equals(3))
            .singleElement()
            .satisfies(score -> {
                assertThat(score.getSubstitute()).isTrue();
                assertThat(score.getPoints()).isEqualTo(11);
                assertThat(score.getScoreItems())
                    .extracting(item -> item.getRuleCode(), item -> item.getPoints())
                    .containsExactly(
                        tuple("STAGE_PLACING", 8),
                        tuple("EXACT_POSITION_BONUS", 2),
                        tuple("WHITE_JERSEY", 1)
                    );
            });
    }

    @Test
    void getPlayerStandingDetailEndpointReturnsScoreBreakdownPayload() throws Exception {
        playerApiService.saveEntryDraft(EDITION_ID, validEntryDraftRequest());
        playerApiService.savePredictions(EDITION_ID, validPredictionsRequest());
        playerApiService.submitEntry(EDITION_ID);

        seedSavedStageResult(
            1,
            scoringFinishers(),
            defaultLastFivePlacings(),
            new JerseyLeaders(3, 3, 4, 16),
            null,
            List.of(5)
        );
        adminApiService.calculateStage(EDITION_ID, 1);

        seedSavedStageResult(
            2,
            scoringFinishers(),
            defaultLastFivePlacings(),
            defaultJerseyLeaders(),
            4,
            List.of()
        );
        adminApiService.calculateStage(EDITION_ID, 2);

        mockMvc.perform(get("/editions/{editionId}/standings/{playerId}", EDITION_ID, 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.editionId").value(1))
            .andExpect(jsonPath("$.stage").value(2))
            .andExpect(jsonPath("$.player.playerId").value(1))
            .andExpect(jsonPath("$.stageHistory.length()").value(2))
            .andExpect(jsonPath("$.stageHistory[1].substitutions.length()").value(1))
            .andExpect(jsonPath("$.stageHistory[1].substitutions[0].effectiveStage").value(2))
            .andExpect(jsonPath("$.stageHistory[1].substitutions[0].position").value(3))
            .andExpect(jsonPath("$.stageHistory[1].substitutions[0].replacedRider.riderId").value(5))
            .andExpect(jsonPath("$.stageHistory[1].substitutions[0].substituteRider.riderId").value(16))
            .andExpect(jsonPath("$.stageHistory[1].riderScores[2].position").value(3))
            .andExpect(jsonPath("$.stageHistory[1].riderScores[2].substitute").value(true))
            .andExpect(jsonPath("$.stageHistory[1].riderScores[2].points").value(11))
            .andExpect(jsonPath("$.stageHistory[1].riderScores[2].scoreItems.length()").value(3))
            .andExpect(jsonPath("$.stageHistory[1].riderScores[2].scoreItems[0].ruleCode").value("STAGE_PLACING"))
            .andExpect(jsonPath("$.stageHistory[1].riderScores[2].scoreItems[0].points").value(8))
            .andExpect(jsonPath("$.stageHistory[1].riderScores[2].scoreItems[1].ruleCode").value("EXACT_POSITION_BONUS"))
            .andExpect(jsonPath("$.stageHistory[1].riderScores[2].scoreItems[1].points").value(2))
            .andExpect(jsonPath("$.stageHistory[1].riderScores[2].scoreItems[2].ruleCode").value("WHITE_JERSEY"))
            .andExpect(jsonPath("$.stageHistory[1].riderScores[2].scoreItems[2].points").value(1));
    }

    @Test
    void getPlayerStandingDetailEndpointReturnsNotFoundForUnknownPlayer() throws Exception {
        mockMvc.perform(get("/editions/{editionId}/standings/{playerId}", EDITION_ID, 999L))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("PLAYER_NOT_FOUND"))
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void saveFinalResultsCalculatesPredictionPointsAndPublishesStandings() {
        playerApiService.saveEntryDraft(EDITION_ID, validEntryDraftRequest());
        playerApiService.savePredictions(EDITION_ID, validPredictionsRequest());
        playerApiService.submitEntry(EDITION_ID);

        seedPublishedStageTwentyOneForCurrentPlayer();

        FinalResultsResponse response = adminApiService.saveFinalResults(EDITION_ID, validFinalResultsRequest());

        assertThat(response.getPublished()).isTrue();
        assertThat(standingRepository.findByEditionIdAndStageOrderByRankPositionAsc(EDITION_ID, 21))
            .singleElement()
            .extracting(standing -> standing.getTotalPoints())
            .isEqualTo(160);
    }

    @Test
    void saveFinalResultsRecalculationIsIdempotentForFinalStandings() {
        playerApiService.saveEntryDraft(EDITION_ID, validEntryDraftRequest());
        playerApiService.savePredictions(EDITION_ID, validPredictionsRequest());
        playerApiService.submitEntry(EDITION_ID);

        seedPublishedStageTwentyOneForCurrentPlayer();

        SaveFinalResultsRequest request = validFinalResultsRequest();
        adminApiService.saveFinalResults(EDITION_ID, request);

        var firstStandings = standingRepository.findByEditionIdAndStageOrderByRankPositionAsc(EDITION_ID, 21);
        assertThat(firstStandings).hasSize(1);
        var firstStanding = firstStandings.getFirst();
        Long firstStandingId = firstStanding.getId();
        Integer firstTotalPoints = firstStanding.getTotalPoints();
        Integer firstRank = firstStanding.getRankPosition();

        adminApiService.saveFinalResults(EDITION_ID, request);

        var secondStandings = standingRepository.findByEditionIdAndStageOrderByRankPositionAsc(EDITION_ID, 21);
        assertThat(secondStandings).hasSize(1);
        var secondStanding = secondStandings.getFirst();
        assertThat(secondStanding.getId()).isEqualTo(firstStandingId);
        assertThat(secondStanding.getTotalPoints()).isEqualTo(firstTotalPoints);
        assertThat(secondStanding.getRankPosition()).isEqualTo(firstRank);

        long stageTwentyOneRowsForPlayer = standingRepository.findByEditionIdAndStageOrderByRankPositionAsc(EDITION_ID, 21)
            .stream()
            .filter(standing -> standing.getPlayerId().equals(1L))
            .count();
        assertThat(stageTwentyOneRowsForPlayer).isEqualTo(1L);
    }

    private SaveEntryDraftRequest validEntryDraftRequest() {
        return new SaveEntryDraftRequest("Submitted Team")
            .riderIds(List.of(3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 12L, 13L, 11L, 16L, 17L, 18L))
            .finisherCountPrediction(150);
    }

    private SavePredictionsRequest validPredictionsRequest() {
        return new SavePredictionsRequest(List.of(
            new ClassificationPrediction(ClassificationType.GENERAL, List.of(3L, 4L, 5L, 6L, 7L)),
            new ClassificationPrediction(ClassificationType.POINTS, List.of(8L, 9L, 10L, 11L, 12L)),
            new ClassificationPrediction(ClassificationType.MOUNTAINS, List.of(13L, 14L, 15L, 16L, 17L))
        ));
    }

    private SaveStageResultRequest validStageResultRequest() {
        SaveStageResultRequest request = new SaveStageResultRequest(
            topFinishers(),
            lastFinishers(),
            new JerseyLeaders(1, 2, 3, 4)
        );
        request.setMostCombativeRaceBibNumber(5);
        request.setWithdrawals(List.of());
        return request;
    }

    private SaveFinalResultsRequest validFinalResultsRequest() {
        return new SaveFinalResultsRequest(
            List.of(3L, 4L, 5L, 6L, 7L),
            List.of(8L, 9L, 10L, 11L, 12L),
            List.of(13L, 14L, 15L, 16L, 17L),
            150
        );
    }

    private void seedPublishedStageTwentyOneForCurrentPlayer() {
        StageResultEntity stageResult = new StageResultEntity();
        stageResult.setEditionId(EDITION_ID);
        stageResult.setStageNumber(21);
        stageResult.setFinishers(List.of(new PlacedBibData(1, 1)));
        stageResult.setLastFive(List.of(new PlacedBibData(25, 25)));
        stageResult.setYellowRaceBib(1);
        stageResult.setPointsRaceBib(2);
        stageResult.setMountainsRaceBib(3);
        stageResult.setWhiteRaceBib(4);
        stageResult.setWithdrawals(List.of());
        stageResult.setSavedAt(OffsetDateTime.now());
        stageResult.setCalculatedAt(OffsetDateTime.now());
        stageResult.setPublished(true);
        stageResultRepository.save(stageResult);

        StageHistoryEntity stageHistory = new StageHistoryEntity();
        stageHistory.setEditionId(EDITION_ID);
        stageHistory.setPlayerId(1L);
        stageHistory.setStage(21);
        stageHistory.setStagePoints(10);
        stageHistory.setTotalPoints(100);
        stageHistoryRepository.save(stageHistory);
    }

    private void seedSavedStageResult(int stageNumber, List<PlacedBibData> finishers) {
        seedSavedStageResult(stageNumber, finishers, defaultLastFivePlacings(), defaultJerseyLeaders(), null, List.of());
    }

    private void seedSavedStageResult(int stageNumber, List<PlacedBibData> finishers, List<Integer> withdrawals) {
        seedSavedStageResult(stageNumber, finishers, defaultLastFivePlacings(), defaultJerseyLeaders(), null, withdrawals);
    }

    private void seedSavedStageResult(
        int stageNumber,
        List<PlacedBibData> finishers,
        List<PlacedBibData> lastFive,
        JerseyLeaders jerseyLeaders,
        Integer mostCombativeRaceBibNumber,
        List<Integer> withdrawals
    ) {
        StageResultEntity stageResult = new StageResultEntity();
        stageResult.setEditionId(EDITION_ID);
        stageResult.setStageNumber(stageNumber);
        stageResult.setFinishers(finishers);
        stageResult.setLastFive(lastFive);
        stageResult.setYellowRaceBib(jerseyLeaders.getYellow());
        stageResult.setPointsRaceBib(jerseyLeaders.getPoints());
        stageResult.setMountainsRaceBib(jerseyLeaders.getMountains());
        stageResult.setWhiteRaceBib(jerseyLeaders.getWhite());
        stageResult.setMostCombativeRaceBib(mostCombativeRaceBibNumber);
        stageResult.setWithdrawals(withdrawals);
        stageResult.setSavedAt(OffsetDateTime.now());
        stageResult.setCalculatedAt(null);
        stageResult.setPublished(false);
        stageResultRepository.save(stageResult);
    }

    private JerseyLeaders defaultJerseyLeaders() {
        return new JerseyLeaders(1, 2, 14, 15);
    }

    private List<PlacedBibData> defaultLastFivePlacings() {
        return List.of(
            new PlacedBibData(21, 21),
            new PlacedBibData(22, 22),
            new PlacedBibData(23, 23),
            new PlacedBibData(24, 24),
            new PlacedBibData(25, 25)
        );
    }

    private List<PlacedBibData> scoringFinishers() {
        return List.of(
            new PlacedBibData(1, 3),
            new PlacedBibData(2, 4),
            new PlacedBibData(3, 16),
            new PlacedBibData(4, 17),
            new PlacedBibData(5, 18),
            new PlacedBibData(6, 14),
            new PlacedBibData(7, 15),
            new PlacedBibData(8, 1),
            new PlacedBibData(9, 2),
            new PlacedBibData(10, 11),
            new PlacedBibData(11, 5),
            new PlacedBibData(12, 6),
            new PlacedBibData(13, 7),
            new PlacedBibData(14, 8),
            new PlacedBibData(15, 9),
            new PlacedBibData(16, 10),
            new PlacedBibData(17, 12),
            new PlacedBibData(18, 13),
            new PlacedBibData(19, 19),
            new PlacedBibData(20, 20)
        );
    }

    private List<PlacedBibData> scoringFinishersWithoutKlunsInTopTwenty() {
        return List.of(
            new PlacedBibData(1, 3),
            new PlacedBibData(2, 4),
            new PlacedBibData(3, 16),
            new PlacedBibData(4, 17),
            new PlacedBibData(5, 25),
            new PlacedBibData(6, 14),
            new PlacedBibData(7, 15),
            new PlacedBibData(8, 1),
            new PlacedBibData(9, 2),
            new PlacedBibData(10, 11),
            new PlacedBibData(11, 5),
            new PlacedBibData(12, 6),
            new PlacedBibData(13, 7),
            new PlacedBibData(14, 8),
            new PlacedBibData(15, 9),
            new PlacedBibData(16, 10),
            new PlacedBibData(17, 12),
            new PlacedBibData(18, 13),
            new PlacedBibData(19, 19),
            new PlacedBibData(20, 20)
        );
    }

    private List<PlacedBibData> lastFivePlacingsWithKlunsLast() {
        return List.of(
            new PlacedBibData(21, 21),
            new PlacedBibData(22, 22),
            new PlacedBibData(23, 23),
            new PlacedBibData(24, 24),
            new PlacedBibData(25, 18)
        );
    }

    private List<BibPlacement> topFinishers() {
        return List.of(
            new BibPlacement(1, 1), new BibPlacement(2, 2), new BibPlacement(3, 3), new BibPlacement(4, 4), new BibPlacement(5, 5),
            new BibPlacement(6, 6), new BibPlacement(7, 7), new BibPlacement(8, 8), new BibPlacement(9, 9), new BibPlacement(10, 10),
            new BibPlacement(11, 11), new BibPlacement(12, 12), new BibPlacement(13, 13), new BibPlacement(14, 14), new BibPlacement(15, 15),
            new BibPlacement(16, 16), new BibPlacement(17, 17), new BibPlacement(18, 18), new BibPlacement(19, 19), new BibPlacement(20, 20)
        );
    }

    private List<BibPlacement> lastFinishers() {
        return List.of(
            new BibPlacement(21, 21),
            new BibPlacement(22, 22),
            new BibPlacement(23, 23),
            new BibPlacement(24, 24),
            new BibPlacement(25, 25)
        );
    }
}

