package com.rvdijkz.tour.application;

import com.rvdijkz.tour.api.error.ApiException;
import com.rvdijkz.tour.api.model.CalculationStatus;
import com.rvdijkz.tour.api.model.ClassificationType;
import com.rvdijkz.tour.api.model.EntryStatus;
import com.rvdijkz.tour.api.model.FinalResultsResponse;
import com.rvdijkz.tour.api.model.StageCalculationResponse;
import com.rvdijkz.tour.persistence.entity.EditionEntity;
import com.rvdijkz.tour.persistence.entity.DoublePointsStageEntity;
import com.rvdijkz.tour.persistence.entity.FinalResultEntity;
import com.rvdijkz.tour.persistence.entity.PlayerAccountEntity;
import com.rvdijkz.tour.persistence.entity.PlayerEntryVersionEntity;
import com.rvdijkz.tour.persistence.entity.PlayerPredictionVersionEntity;
import com.rvdijkz.tour.persistence.entity.RiderEntity;
import com.rvdijkz.tour.persistence.entity.StageHistoryEntity;
import com.rvdijkz.tour.persistence.entity.StageResultEntity;
import com.rvdijkz.tour.persistence.entity.StandingEntity;
import com.rvdijkz.tour.persistence.repository.EditionRepository;
import com.rvdijkz.tour.persistence.repository.DoublePointsStageRepository;
import com.rvdijkz.tour.persistence.repository.FinalResultRepository;
import com.rvdijkz.tour.persistence.repository.PlayerAccountRepository;
import com.rvdijkz.tour.persistence.repository.PlayerEntryVersionRepository;
import com.rvdijkz.tour.persistence.repository.PlayerPredictionVersionRepository;
import com.rvdijkz.tour.persistence.repository.StageHistoryRepository;
import com.rvdijkz.tour.persistence.repository.StageResultRepository;
import com.rvdijkz.tour.persistence.repository.StandingRepository;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StandingsCalculationService {

    private static final int FINAL_STAGE_NUMBER = 21;
    private static final int TOP_FIVE_POINTS = 3;
    private static final int EXACT_PLACE_BONUS = 1;

    private final EditionRepository editionRepository;
    private final DoublePointsStageRepository doublePointsStageRepository;
    private final StageResultRepository stageResultRepository;
    private final FinalResultRepository finalResultRepository;
    private final PlayerEntryVersionRepository playerEntryVersionRepository;
    private final PlayerPredictionVersionRepository playerPredictionVersionRepository;
    private final PlayerAccountRepository playerAccountRepository;
    private final StageHistoryRepository stageHistoryRepository;
    private final StandingRepository standingRepository;
    private final StageLineupResolver stageLineupResolver;
    private final StageScoringService stageScoringService;

    public StandingsCalculationService(
        EditionRepository editionRepository,
        DoublePointsStageRepository doublePointsStageRepository,
        StageResultRepository stageResultRepository,
        FinalResultRepository finalResultRepository,
        PlayerEntryVersionRepository playerEntryVersionRepository,
        PlayerPredictionVersionRepository playerPredictionVersionRepository,
        PlayerAccountRepository playerAccountRepository,
        StageHistoryRepository stageHistoryRepository,
        StandingRepository standingRepository,
        StageLineupResolver stageLineupResolver,
        StageScoringService stageScoringService
    ) {
        this.editionRepository = editionRepository;
        this.doublePointsStageRepository = doublePointsStageRepository;
        this.stageResultRepository = stageResultRepository;
        this.finalResultRepository = finalResultRepository;
        this.playerEntryVersionRepository = playerEntryVersionRepository;
        this.playerPredictionVersionRepository = playerPredictionVersionRepository;
        this.playerAccountRepository = playerAccountRepository;
        this.stageHistoryRepository = stageHistoryRepository;
        this.standingRepository = standingRepository;
        this.stageLineupResolver = stageLineupResolver;
        this.stageScoringService = stageScoringService;
    }

    @Transactional
    public StageCalculationResponse calculateStage(long editionId, int stageNumber) {
        EditionEntity edition = getEdition(editionId);
        StageResultEntity stageResult = stageResultRepository.findByEditionIdAndStageNumber(editionId, stageNumber)
            .orElseThrow(() -> ApiException.conflict("STAGE_CALCULATION_CONFLICT", "Stage input must be saved before calculation can run."));
        validateStageIsLatestSaved(editionId, stageNumber);

        List<PlayerStandingSnapshot> standings = buildStageStandings(editionId, stageNumber, stageResult);
        replaceStageProjection(editionId, stageNumber, standings);

        OffsetDateTime calculatedAt = OffsetDateTime.now();
        stageResult.setCalculatedAt(calculatedAt);
        stageResult.setPublished(true);
        stageResultRepository.save(stageResult);

        edition.setCurrentStage(Math.max(edition.getCurrentStage(), Math.min(FINAL_STAGE_NUMBER, stageNumber + 1)));
        editionRepository.save(edition);

        return new StageCalculationResponse(editionId, stageNumber, CalculationStatus.SUCCESS).published(true);
    }

    @Transactional
    public FinalResultsResponse calculateFinalResults(long editionId) {
        getEdition(editionId);
        StageResultEntity finalStage = stageResultRepository.findByEditionIdAndStageNumber(editionId, FINAL_STAGE_NUMBER)
            .orElseThrow(() -> ApiException.conflict("FINAL_RESULTS_LOCKED", "Final results require a calculated stage 21 result."));
        if (!Boolean.TRUE.equals(finalStage.getPublished())) {
            throw ApiException.conflict("FINAL_RESULTS_LOCKED", "Final results require a calculated stage 21 result.");
        }

        FinalResultEntity finalResult = finalResultRepository.findById(editionId)
            .orElseThrow(() -> ApiException.conflict("FINAL_RESULTS_MISSING", "Final results must be saved before calculation can run."));

        List<FinalStandingSnapshot> standings = buildFinalStandings(editionId, finalResult);
        replaceFinalStandings(editionId, standings);

        finalResult.setCalculatedAt(OffsetDateTime.now());
        finalResult.setPublished(true);
        finalResultRepository.save(finalResult);

        return new FinalResultsResponse(editionId, CalculationStatus.SUCCESS, true);
    }

    private EditionEntity getEdition(long editionId) {
        return editionRepository.findById(editionId)
            .orElseThrow(() -> ApiException.notFound("EDITION_NOT_FOUND", "Requested edition was not found."));
    }

    private void validateStageIsLatestSaved(long editionId, int stageNumber) {
        StageResultEntity latestStage = stageResultRepository.findTopByEditionIdOrderByStageNumberDesc(editionId)
            .orElseThrow(() -> ApiException.conflict("STAGE_CALCULATION_CONFLICT", "Stage input must be saved before calculation can run."));
        if (!latestStage.getStageNumber().equals(stageNumber)) {
            throw ApiException.conflict("STAGE_CALCULATION_CONFLICT", "Only the latest saved stage can be calculated or recalculated.");
        }
    }

    private List<PlayerStandingSnapshot> buildStageStandings(long editionId, int stageNumber, StageResultEntity stageResult) {
        List<PlayerEntryVersionEntity> submittedEntries = playerEntryVersionRepository.findByIdEditionIdAndIdStatus(editionId, EntryStatus.SUBMITTED);
        List<PlayerStandingSnapshot> standings = new ArrayList<>();
        boolean doublePointsStage = isDoublePointsStage(editionId, stageNumber);
        StageResultEntity previousStageResult = getPreviousStageResult(editionId, stageNumber);

        for (PlayerEntryVersionEntity entry : submittedEntries) {
            long playerId = entry.getId().getPlayerId();
            PlayerAccountEntity player = playerAccountRepository.findById(playerId)
                .orElseThrow(() -> ApiException.notFound("PLAYER_NOT_FOUND", "Player account was not found."));
            StageLineupResolver.ResolvedStageLineup lineup = stageLineupResolver.resolveLineup(editionId, entry, stageNumber);
            int stagePoints = stageScoringService
                .calculateStageScore(entry, lineup, stageResult, previousStageResult, doublePointsStage)
                .totalPoints();
            int previousTotal = stageNumber == 1 ? 0 : getPreviousStageTotal(editionId, playerId, stageNumber - 1);
            standings.add(new PlayerStandingSnapshot(playerId, player.getDisplayName(), entry.getTeamName(), stagePoints, previousTotal + stagePoints));
        }

        return rankStageStandings(standings);
    }

    private StageResultEntity getPreviousStageResult(long editionId, int stageNumber) {
        if (stageNumber == 1) {
            return null;
        }

        return stageResultRepository.findByEditionIdAndStageNumber(editionId, stageNumber - 1)
            .orElseThrow(() -> ApiException.conflict(
                "STAGE_SEQUENCE_CONFLICT",
                "Stage scoring requires results for all earlier stages."
            ));
    }

    private boolean isDoublePointsStage(long editionId, int stageNumber) {
        return doublePointsStageRepository.findByIdEditionIdOrderByIdStageNumberAsc(editionId)
            .stream()
            .map(DoublePointsStageEntity::getId)
            .anyMatch(id -> id.getStageNumber().equals(stageNumber));
    }


    private int getPreviousStageTotal(long editionId, long playerId, int previousStage) {
        Optional<StageHistoryEntity> history = stageHistoryRepository.findByEditionIdAndPlayerIdAndStage(editionId, playerId, previousStage);
        return history.map(StageHistoryEntity::getTotalPoints).orElse(0);
    }

    private List<PlayerStandingSnapshot> rankStageStandings(List<PlayerStandingSnapshot> snapshots) {
        snapshots.sort(Comparator.comparingInt(PlayerStandingSnapshot::totalPoints).reversed().thenComparingLong(PlayerStandingSnapshot::playerId));
        int lastPoints = Integer.MIN_VALUE;
        int lastRank = 0;
        for (int index = 0; index < snapshots.size(); index++) {
            PlayerStandingSnapshot snapshot = snapshots.get(index);
            int rank = snapshot.totalPoints() == lastPoints ? lastRank : index + 1;
            snapshots.set(index, snapshot.withRank(rank));
            lastPoints = snapshot.totalPoints();
            lastRank = rank;
        }
        return snapshots;
    }

    private void replaceStageProjection(long editionId, int stageNumber, List<PlayerStandingSnapshot> standings) {
        Map<Long, StageHistoryEntity> historyByPlayer = new HashMap<>();
        for (StageHistoryEntity existingHistory : stageHistoryRepository.findByEditionIdAndStageOrderByPlayerIdAsc(editionId, stageNumber)) {
            historyByPlayer.put(existingHistory.getPlayerId(), existingHistory);
        }

        Map<Long, StandingEntity> standingByPlayer = new HashMap<>();
        for (StandingEntity existingStanding : standingRepository.findByEditionIdAndStageOrderByRankPositionAsc(editionId, stageNumber)) {
            standingByPlayer.put(existingStanding.getPlayerId(), existingStanding);
        }

        Set<Long> activePlayers = new HashSet<>();

        for (PlayerStandingSnapshot snapshot : standings) {
            long playerId = snapshot.playerId();
            activePlayers.add(playerId);

            StageHistoryEntity historyEntity = historyByPlayer.getOrDefault(playerId, new StageHistoryEntity());
            historyEntity.setEditionId(editionId);
            historyEntity.setPlayerId(playerId);
            historyEntity.setStage(stageNumber);
            historyEntity.setStagePoints(snapshot.stagePoints());
            historyEntity.setTotalPoints(snapshot.totalPoints());
            stageHistoryRepository.save(historyEntity);

            StandingEntity standingEntity = standingByPlayer.getOrDefault(playerId, new StandingEntity());
            standingEntity.setEditionId(editionId);
            standingEntity.setStage(stageNumber);
            standingEntity.setRankPosition(snapshot.rank());
            standingEntity.setPlayerId(playerId);
            standingEntity.setPlayerName(snapshot.playerName());
            standingEntity.setTeamName(snapshot.teamName());
            standingEntity.setTotalPoints(snapshot.totalPoints());
            standingRepository.save(standingEntity);
        }

        List<StageHistoryEntity> obsoleteHistory = historyByPlayer.values().stream()
            .filter(history -> !activePlayers.contains(history.getPlayerId()))
            .toList();
        if (!obsoleteHistory.isEmpty()) {
            stageHistoryRepository.deleteAllInBatch(obsoleteHistory);
        }

        List<StandingEntity> obsoleteStandings = standingByPlayer.values().stream()
            .filter(standing -> !activePlayers.contains(standing.getPlayerId()))
            .toList();
        if (!obsoleteStandings.isEmpty()) {
            standingRepository.deleteAllInBatch(obsoleteStandings);
        }
    }

    private List<FinalStandingSnapshot> buildFinalStandings(long editionId, FinalResultEntity finalResult) {
        List<PlayerEntryVersionEntity> submittedEntries = playerEntryVersionRepository.findByIdEditionIdAndIdStatus(editionId, EntryStatus.SUBMITTED);
        List<FinalStandingSnapshot> standings = new ArrayList<>();

        for (PlayerEntryVersionEntity entry : submittedEntries) {
            PlayerPredictionVersionEntity predictions = playerPredictionVersionRepository
                .findByIdEditionIdAndIdPlayerIdAndIdStatus(editionId, entry.getId().getPlayerId(), EntryStatus.SUBMITTED)
                .orElse(null);
            if (!isCompletePrediction(predictions)) {
                continue;
            }

            PlayerAccountEntity player = playerAccountRepository.findById(entry.getId().getPlayerId())
                .orElseThrow(() -> ApiException.notFound("PLAYER_NOT_FOUND", "Player account was not found."));
            int stageTotal = getPreviousStageTotal(editionId, entry.getId().getPlayerId(), FINAL_STAGE_NUMBER);
            int predictionPoints = scorePrediction(predictions, finalResult);
            int finisherDistance = Math.abs(entry.getFinisherCountPrediction() - finalResult.getFinisherCount());
            standings.add(new FinalStandingSnapshot(
                entry.getId().getPlayerId(),
                player.getDisplayName(),
                entry.getTeamName(),
                stageTotal + predictionPoints,
                finisherDistance,
                0
            ));
        }

        return rankFinalStandings(standings);
    }

    private boolean isCompletePrediction(PlayerPredictionVersionEntity predictions) {
        return predictions != null
            && predictions.getGeneralRiderIds().size() == EntryRules.PREDICTION_LIST_SIZE
            && predictions.getPointsRiderIds().size() == EntryRules.PREDICTION_LIST_SIZE
            && predictions.getMountainsRiderIds().size() == EntryRules.PREDICTION_LIST_SIZE;
    }

    private int scorePrediction(PlayerPredictionVersionEntity predictions, FinalResultEntity finalResult) {
        Map<ClassificationType, List<Long>> actualResults = new EnumMap<>(ClassificationType.class);
        actualResults.put(ClassificationType.GENERAL, finalResult.getGeneralTop5RiderIds());
        actualResults.put(ClassificationType.POINTS, finalResult.getPointsTop5RiderIds());
        actualResults.put(ClassificationType.MOUNTAINS, finalResult.getMountainsTop5RiderIds());

        int total = 0;
        total += scorePredictionList(predictions.getGeneralRiderIds(), actualResults.get(ClassificationType.GENERAL));
        total += scorePredictionList(predictions.getPointsRiderIds(), actualResults.get(ClassificationType.POINTS));
        total += scorePredictionList(predictions.getMountainsRiderIds(), actualResults.get(ClassificationType.MOUNTAINS));
        return total;
    }

    private int scorePredictionList(List<Long> predicted, List<Long> actual) {
        int score = 0;
        for (int index = 0; index < predicted.size(); index++) {
            Long riderId = predicted.get(index);
            int actualIndex = actual.indexOf(riderId);
            if (actualIndex >= 0) {
                score += TOP_FIVE_POINTS;
                if (actualIndex == index) {
                    score += EXACT_PLACE_BONUS;
                }
            }
        }
        return score;
    }

    private List<FinalStandingSnapshot> rankFinalStandings(List<FinalStandingSnapshot> standings) {
        standings.sort(
            Comparator.comparingInt(FinalStandingSnapshot::totalPoints).reversed()
                .thenComparingInt(FinalStandingSnapshot::finisherDistance)
                .thenComparingLong(FinalStandingSnapshot::playerId)
        );

        int lastPoints = Integer.MIN_VALUE;
        int lastDistance = Integer.MIN_VALUE;
        int lastRank = 0;
        for (int index = 0; index < standings.size(); index++) {
            FinalStandingSnapshot snapshot = standings.get(index);
            int rank = snapshot.totalPoints() == lastPoints && snapshot.finisherDistance() == lastDistance ? lastRank : index + 1;
            standings.set(index, snapshot.withRank(rank));
            lastPoints = snapshot.totalPoints();
            lastDistance = snapshot.finisherDistance();
            lastRank = rank;
        }
        return standings;
    }

    private void replaceFinalStandings(long editionId, List<FinalStandingSnapshot> standings) {
        Map<Long, StandingEntity> standingByPlayer = new HashMap<>();
        for (StandingEntity existingStanding : standingRepository.findByEditionIdAndStageOrderByRankPositionAsc(editionId, FINAL_STAGE_NUMBER)) {
            standingByPlayer.put(existingStanding.getPlayerId(), existingStanding);
        }

        Set<Long> activePlayers = new HashSet<>();

        for (FinalStandingSnapshot snapshot : standings) {
            long playerId = snapshot.playerId();
            activePlayers.add(playerId);

            StandingEntity standingEntity = standingByPlayer.getOrDefault(playerId, new StandingEntity());
            standingEntity.setEditionId(editionId);
            standingEntity.setStage(FINAL_STAGE_NUMBER);
            standingEntity.setRankPosition(snapshot.rank());
            standingEntity.setPlayerId(playerId);
            standingEntity.setPlayerName(snapshot.playerName());
            standingEntity.setTeamName(snapshot.teamName());
            standingEntity.setTotalPoints(snapshot.totalPoints());
            standingRepository.save(standingEntity);
        }

        List<StandingEntity> obsoleteStandings = standingByPlayer.values().stream()
            .filter(standing -> !activePlayers.contains(standing.getPlayerId()))
            .toList();
        if (!obsoleteStandings.isEmpty()) {
            standingRepository.deleteAllInBatch(obsoleteStandings);
        }
    }

    private record PlayerStandingSnapshot(
        long playerId,
        String playerName,
        String teamName,
        int stagePoints,
        int totalPoints,
        int rank
    ) {
        private PlayerStandingSnapshot(long playerId, String playerName, String teamName, int stagePoints, int totalPoints) {
            this(playerId, playerName, teamName, stagePoints, totalPoints, 0);
        }

        private PlayerStandingSnapshot withRank(int updatedRank) {
            return new PlayerStandingSnapshot(playerId, playerName, teamName, stagePoints, totalPoints, updatedRank);
        }
    }

    private record FinalStandingSnapshot(
        long playerId,
        String playerName,
        String teamName,
        int totalPoints,
        int finisherDistance,
        int rank
    ) {
        private FinalStandingSnapshot withRank(int updatedRank) {
            return new FinalStandingSnapshot(playerId, playerName, teamName, totalPoints, finisherDistance, updatedRank);
        }
    }
}





