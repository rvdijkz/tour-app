package com.rvdijkz.tour;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.rvdijkz.tour.api.error.ApiException;
import com.rvdijkz.tour.api.model.ClassificationPrediction;
import com.rvdijkz.tour.api.model.ClassificationType;
import com.rvdijkz.tour.api.model.SaveEntryDraftRequest;
import com.rvdijkz.tour.api.model.SavePredictionsRequest;
import com.rvdijkz.tour.application.PlayerApiService;
import com.rvdijkz.tour.application.StageLineupResolver;
import com.rvdijkz.tour.persistence.entity.PlayerEntryVersionEntity;
import com.rvdijkz.tour.persistence.entity.StageResultEntity;
import com.rvdijkz.tour.persistence.repository.PlayerEntryVersionRepository;
import com.rvdijkz.tour.persistence.repository.RiderRepository;
import com.rvdijkz.tour.persistence.repository.StageResultRepository;
import com.rvdijkz.tour.persistence.value.PlacedBibData;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StageLineupResolverTests {

    private static final long EDITION_ID = 1L;
    private static final long PLAYER_ID = 1L;
    private static final String TEST_DATASOURCE_URL = "jdbc:h2:mem:tourdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
    private static final String TEST_DATASOURCE_DRIVER = "org.h2.Driver";
    private static final String TEST_DATASOURCE_USERNAME = "sa";
    private static final String TEST_DATASOURCE_PASSWORD = "";

    @Autowired
    private PlayerApiService playerApiService;

    @Autowired
    private PlayerEntryVersionRepository playerEntryVersionRepository;

    @Autowired
    private StageLineupResolver stageLineupResolver;

    @Autowired
    private StageResultRepository stageResultRepository;

    @Autowired
    private RiderRepository riderRepository;

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
    void resolveLineupReturnsInitialActiveTeamForStageOne() {
        PlayerEntryVersionEntity submittedEntry = submitValidEntry();

        StageLineupResolver.ResolvedStageLineup lineup = stageLineupResolver.resolveLineup(EDITION_ID, submittedEntry, 1);

        assertThat(lineup.getRiderAtPosition(1)).extracting(rider -> rider.getId()).isEqualTo(3L);
        assertThat(lineup.getRiderAtPosition(11)).extracting(rider -> rider.getId()).isEqualTo(11L);
        assertThat(lineup.getRiderAtPosition(14)).extracting(rider -> rider.getId()).isEqualTo(18L);
        assertThat(lineup.isPositionOccupied(12)).isFalse();
        assertThat(lineup.isPositionOccupied(13)).isFalse();
    }

    @Test
    void resolveLineupAssignsReservesInOrderAfterStageWithdrawals() {
        PlayerEntryVersionEntity submittedEntry = submitValidEntry();
        saveStageResultWithWithdrawals(1, 5L, 10L);

        StageLineupResolver.ResolvedStageLineup lineup = stageLineupResolver.resolveLineup(EDITION_ID, submittedEntry, 2);

        assertThat(lineup.getRiderAtPosition(3)).extracting(rider -> rider.getId()).isEqualTo(16L);
        assertThat(lineup.getRiderAtPosition(8)).extracting(rider -> rider.getId()).isEqualTo(17L);
        assertThat(lineup.isPositionOccupied(12)).isFalse();
        assertThat(lineup.isPositionOccupied(13)).isFalse();
    }

    @Test
    void resolveLineupSkipsReserveThatWithdrewBeforeActivation() {
        PlayerEntryVersionEntity submittedEntry = submitValidEntry();
        saveStageResultWithWithdrawals(1, 16L, 5L);

        StageLineupResolver.ResolvedStageLineup lineup = stageLineupResolver.resolveLineup(EDITION_ID, submittedEntry, 2);

        assertThat(lineup.getRiderAtPosition(3)).extracting(rider -> rider.getId()).isEqualTo(17L);
        assertThat(lineup.activeRidersByPosition())
            .extractingByKey(3)
            .extracting(rider -> rider.getId())
            .isEqualTo(17L);
        assertThat(lineup.activeRidersByPosition().values())
            .extracting(rider -> rider.getId())
            .doesNotContain(16L);
    }

    @Test
    void resolveLineupReplacesWithdrawnActiveReserveInSamePosition() {
        PlayerEntryVersionEntity submittedEntry = submitValidEntry();
        saveStageResultWithWithdrawals(1, 5L);
        saveStageResultWithWithdrawals(2, 16L);

        StageLineupResolver.ResolvedStageLineup lineup = stageLineupResolver.resolveLineup(EDITION_ID, submittedEntry, 3);

        assertThat(lineup.getRiderAtPosition(3)).extracting(rider -> rider.getId()).isEqualTo(17L);
    }

    @Test
    void resolveLineupLeavesKlunsEmptyAfterWithdrawal() {
        PlayerEntryVersionEntity submittedEntry = submitValidEntry();
        saveStageResultWithWithdrawals(1, 18L);

        StageLineupResolver.ResolvedStageLineup lineup = stageLineupResolver.resolveLineup(EDITION_ID, submittedEntry, 2);

        assertThat(lineup.isPositionOccupied(14)).isFalse();
    }

    @Test
    void resolveLineupRequiresResultsForEarlierStages() {
        PlayerEntryVersionEntity submittedEntry = submitValidEntry();

        assertThatThrownBy(() -> stageLineupResolver.resolveLineup(EDITION_ID, submittedEntry, 2))
            .isInstanceOf(ApiException.class)
            .extracting(error -> ((ApiException) error).getCode())
            .isEqualTo("STAGE_SEQUENCE_CONFLICT");
    }

    private PlayerEntryVersionEntity submitValidEntry() {
        playerApiService.saveEntryDraft(EDITION_ID, validEntryDraftRequest());
        playerApiService.savePredictions(EDITION_ID, validPredictionsRequest());
        playerApiService.submitEntry(EDITION_ID);
        return playerEntryVersionRepository.findByIdEditionIdAndIdPlayerIdAndIdStatus(
            EDITION_ID,
            PLAYER_ID,
            com.rvdijkz.tour.api.model.EntryStatus.SUBMITTED
        ).orElseThrow();
    }

    private void saveStageResultWithWithdrawals(int stageNumber, Long... withdrawnRiderIds) {
        StageResultEntity stageResult = new StageResultEntity();
        stageResult.setEditionId(EDITION_ID);
        stageResult.setStageNumber(stageNumber);
        stageResult.setFinishers(List.of(new PlacedBibData(1, raceBibOf(3L))));
        stageResult.setLastFive(List.of(new PlacedBibData(25, raceBibOf(18L))));
        stageResult.setYellowRaceBib(raceBibOf(3L));
        stageResult.setPointsRaceBib(raceBibOf(4L));
        stageResult.setMountainsRaceBib(raceBibOf(5L));
        stageResult.setWhiteRaceBib(raceBibOf(6L));
        stageResult.setMostCombativeRaceBib(null);
        stageResult.setWithdrawals(List.of(withdrawnRiderIds).stream().map(this::raceBibOf).toList());
        stageResult.setSavedAt(OffsetDateTime.now());
        stageResult.setCalculatedAt(null);
        stageResult.setPublished(false);
        stageResultRepository.save(stageResult);
    }

    private Integer raceBibOf(Long riderId) {
        return riderRepository.findById(riderId).orElseThrow().getRaceBibNumber();
    }

    private SaveEntryDraftRequest validEntryDraftRequest() {
        return new SaveEntryDraftRequest("Resolver Team")
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
}

