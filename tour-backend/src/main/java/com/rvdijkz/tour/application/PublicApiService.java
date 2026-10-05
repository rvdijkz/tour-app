package com.rvdijkz.tour.application;

import com.rvdijkz.tour.api.error.ApiException;
import com.rvdijkz.tour.api.model.EditionSummary;
import com.rvdijkz.tour.api.model.PlayerStandingDetail;
import com.rvdijkz.tour.api.model.RiderStageScore;
import com.rvdijkz.tour.api.model.RiderListResponse;
import com.rvdijkz.tour.api.model.RiderSummary;
import com.rvdijkz.tour.api.model.ScoreContribution;
import com.rvdijkz.tour.api.model.StageRiderReference;
import com.rvdijkz.tour.api.model.StageHistoryItem;
import com.rvdijkz.tour.api.model.StageSubstitution;
import com.rvdijkz.tour.api.model.StandingRow;
import com.rvdijkz.tour.api.model.StandingsResponse;
import com.rvdijkz.tour.persistence.entity.EditionEntity;
import com.rvdijkz.tour.persistence.entity.PlayerEntryVersionEntity;
import com.rvdijkz.tour.persistence.entity.RiderEntity;
import com.rvdijkz.tour.persistence.entity.StageHistoryEntity;
import com.rvdijkz.tour.persistence.entity.StageResultEntity;
import com.rvdijkz.tour.persistence.entity.StandingEntity;
import com.rvdijkz.tour.persistence.repository.PlayerEntryVersionRepository;
import com.rvdijkz.tour.persistence.repository.DoublePointsStageRepository;
import com.rvdijkz.tour.persistence.repository.EditionRepository;
import com.rvdijkz.tour.persistence.repository.RiderRepository;
import com.rvdijkz.tour.persistence.repository.StageHistoryRepository;
import com.rvdijkz.tour.persistence.repository.StageResultRepository;
import com.rvdijkz.tour.persistence.repository.StandingRepository;
import com.rvdijkz.tour.api.model.EntryStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class PublicApiService {

    private final EditionRepository editionRepository;
    private final DoublePointsStageRepository doublePointsStageRepository;
    private final RiderRepository riderRepository;
    private final StandingRepository standingRepository;
    private final StageHistoryRepository stageHistoryRepository;
    private final StageResultRepository stageResultRepository;
    private final PlayerEntryVersionRepository playerEntryVersionRepository;
    private final StageLineupResolver stageLineupResolver;
    private final StageScoringService stageScoringService;

    public PublicApiService(
        EditionRepository editionRepository,
        DoublePointsStageRepository doublePointsStageRepository,
        RiderRepository riderRepository,
        StandingRepository standingRepository,
        StageHistoryRepository stageHistoryRepository,
        StageResultRepository stageResultRepository,
        PlayerEntryVersionRepository playerEntryVersionRepository,
        StageLineupResolver stageLineupResolver,
        StageScoringService stageScoringService
    ) {
        this.editionRepository = editionRepository;
        this.doublePointsStageRepository = doublePointsStageRepository;
        this.riderRepository = riderRepository;
        this.standingRepository = standingRepository;
        this.stageHistoryRepository = stageHistoryRepository;
        this.stageResultRepository = stageResultRepository;
        this.playerEntryVersionRepository = playerEntryVersionRepository;
        this.stageLineupResolver = stageLineupResolver;
        this.stageScoringService = stageScoringService;
    }

    public EditionSummary getCurrentEdition() {
        EditionEntity edition = editionRepository.findTopByOrderByYearDescIdDesc()
            .orElseThrow(() -> ApiException.notFound("EDITION_NOT_FOUND", "Requested edition was not found."));
        return toEditionSummary(edition);
    }

    public RiderListResponse listRiders(long editionId) {
        EditionEntity edition = getEdition(editionId);
        List<RiderSummary> riders = riderRepository.findByEditionIdOrderByGameRiderNumberAsc(editionId)
            .stream()
            .map(r -> new RiderSummary(
                r.getId(),
                r.getGameRiderNumber(),
                r.getRaceBibNumber(),
                r.getName(),
                r.getValue(),
                r.getNationality()
            ).withdrawn(r.getWithdrawn()))
            .toList();
        return new RiderListResponse(edition.getId(), riders);
    }

    public StandingsResponse getStandings(long editionId) {
        EditionEntity edition = getEdition(editionId);
        Integer stage = getLatestPublishedStage(editionId, edition);
        List<StandingRow> rows = standingRepository.findByEditionIdAndStageOrderByRankPositionAsc(editionId, stage)
            .stream()
            .map(this::toStandingRow)
            .toList();
        return new StandingsResponse(editionId, stage, rows);
    }

    public PlayerStandingDetail getPlayerStandingDetail(long editionId, long playerId) {
        EditionEntity edition = getEdition(editionId);
        Integer stage = getLatestPublishedStage(editionId, edition);
        StandingRow playerStanding = standingRepository.findByEditionIdAndStageAndPlayerId(editionId, stage, playerId)
            .map(this::toStandingRow)
            .orElseThrow(() -> ApiException.notFound("PLAYER_NOT_FOUND", "Requested player was not found for this edition."));

        PlayerEntryVersionEntity entry = playerEntryVersionRepository
            .findByIdEditionIdAndIdPlayerIdAndIdStatus(editionId, playerId, EntryStatus.SUBMITTED)
            .orElseThrow(() -> ApiException.notFound("PLAYER_NOT_FOUND", "Submitted team for the requested player was not found."));

        List<StageHistoryItem> stageHistory = stageHistoryRepository
            .findByEditionIdAndPlayerIdOrderByStageAsc(editionId, playerId)
            .stream()
            .map(history -> toStageHistoryItem(editionId, entry, history))
            .toList();

        return new PlayerStandingDetail(editionId, stage, playerStanding, stageHistory);
    }

    private StageHistoryItem toStageHistoryItem(long editionId, PlayerEntryVersionEntity entry, StageHistoryEntity history) {
        int stageNumber = history.getStage();
        StageResultEntity stageResult = getStageResult(editionId, stageNumber);
        StageResultEntity previousStageResult = stageNumber == 1 ? null : getStageResult(editionId, stageNumber - 1);
        StageLineupResolver.ResolvedStageLineup lineup = stageLineupResolver.resolveLineup(editionId, entry, stageNumber);
        StageLineupResolver.ResolvedStageLineup previousLineup = stageNumber == 1 ? null : stageLineupResolver.resolveLineup(editionId, entry, stageNumber - 1);
        StageScoringService.StageScoreBreakdown breakdown = stageScoringService.calculateStageScore(
            entry,
            lineup,
            stageResult,
            previousStageResult,
            isDoublePointsStage(editionId, stageNumber)
        );

        return new StageHistoryItem(
            history.getStage(),
            history.getStagePoints(),
            history.getTotalPoints(),
            toStageSubstitutions(stageNumber, entry, previousLineup, lineup),
            toRiderStageScores(breakdown.riderScores())
        );
    }

    private List<StageSubstitution> toStageSubstitutions(
        int stageNumber,
        PlayerEntryVersionEntity entry,
        StageLineupResolver.ResolvedStageLineup previousLineup,
        StageLineupResolver.ResolvedStageLineup currentLineup
    ) {
        if (previousLineup == null) {
            return List.of();
        }

        Set<Long> reserveRiderIds = reserveRiderIds(entry);
        List<StageSubstitution> substitutions = new ArrayList<>();
        for (int position = 1; position <= EntryRules.NATIONALITY_POSITION; position++) {
            RiderEntity previousRider = previousLineup.getRiderAtPosition(position);
            RiderEntity currentRider = currentLineup.getRiderAtPosition(position);
            if (previousRider == null || currentRider == null) {
                continue;
            }
            if (previousRider.getId().equals(currentRider.getId())) {
                continue;
            }
            if (!reserveRiderIds.contains(currentRider.getId())) {
                continue;
            }

            substitutions.add(new StageSubstitution(stageNumber, position, toStageRiderReference(previousRider), toStageRiderReference(currentRider)));
        }
        return substitutions;
    }

    private List<RiderStageScore> toRiderStageScores(List<StageScoringService.RiderStageBreakdown> riderScores) {
        return riderScores.stream()
            .map(riderScore -> new RiderStageScore(
                riderScore.position(),
                toStageRiderReference(riderScore.rider()),
                riderScore.substitute(),
                riderScore.points(),
                toScoreContributions(riderScore.contributions())
            ))
            .toList();
    }

    private List<ScoreContribution> toScoreContributions(List<StageScoringService.ScoreContributionDetail> contributions) {
        return contributions.stream()
            .map(contribution -> new ScoreContribution(
                contribution.rule().code(),
                contribution.rule().description(),
                contribution.points()
            ))
            .toList();
    }

    private StageRiderReference toStageRiderReference(RiderEntity rider) {
        return new StageRiderReference(rider.getId(), rider.getName(), rider.getRaceBibNumber());
    }

    private StageResultEntity getStageResult(long editionId, int stageNumber) {
        return stageResultRepository.findByEditionIdAndStageNumber(editionId, stageNumber)
            .orElseThrow(() -> ApiException.conflict("STAGE_SEQUENCE_CONFLICT", "Stage detail requires results for all published stages."));
    }

    private Integer getLatestPublishedStage(long editionId, EditionEntity edition) {
        return stageResultRepository.findTopByEditionIdAndPublishedTrueOrderByStageNumberDesc(editionId)
            .map(StageResultEntity::getStageNumber)
            .orElse(edition.getCurrentStage());
    }

    private boolean isDoublePointsStage(long editionId, int stageNumber) {
        return doublePointsStageRepository.findByIdEditionIdOrderByIdStageNumberAsc(editionId)
            .stream()
            .map(stage -> stage.getId().getStageNumber())
            .anyMatch(number -> number.equals(stageNumber));
    }

    private Set<Long> reserveRiderIds(PlayerEntryVersionEntity entry) {
        List<Long> riderIds = entry.getRiderIds() == null ? List.of() : entry.getRiderIds();
        return Set.of(
            riderIds.get(EntryRules.RESERVE_ONE_POSITION - 1),
            riderIds.get(EntryRules.RESERVE_TWO_POSITION - 1)
        );
    }

    private EditionEntity getEdition(long editionId) {
        return editionRepository.findById(editionId)
            .orElseThrow(() -> ApiException.notFound("EDITION_NOT_FOUND", "Requested edition was not found."));
    }

    private EditionSummary toEditionSummary(EditionEntity entity) {
        List<Integer> doublePointsStages = doublePointsStageRepository.findByIdEditionIdOrderByIdStageNumberAsc(entity.getId())
            .stream()
            .map(dp -> dp.getId().getStageNumber())
            .toList();

        return new EditionSummary(
            entity.getId(),
            entity.getYear(),
            entity.getEntryDeadline(),
            entity.getReserveBudget(),
            entity.getRequiredNationality(),
            entity.getCurrentStage()
        ).doublePointsStages(doublePointsStages);
    }

    private StandingRow toStandingRow(StandingEntity entity) {
        return new StandingRow(
            entity.getRankPosition(),
            entity.getPlayerId(),
            entity.getPlayerName(),
            entity.getTeamName(),
            entity.getTotalPoints()
        );
    }
}

