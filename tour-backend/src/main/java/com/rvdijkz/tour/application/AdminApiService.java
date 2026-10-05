package com.rvdijkz.tour.application;

import com.rvdijkz.tour.api.error.ApiException;
import com.rvdijkz.tour.api.model.ClearedDataSummary;
import com.rvdijkz.tour.api.model.CreateEditionRequest;
import com.rvdijkz.tour.api.model.CreateEditionResponse;
import com.rvdijkz.tour.api.model.CreatePlayerRequest;
import com.rvdijkz.tour.api.model.DeadlineConfigurationResponse;
import com.rvdijkz.tour.api.model.DoublePointsStagesConfigurationResponse;
import com.rvdijkz.tour.api.model.FinalResultsResponse;
import com.rvdijkz.tour.api.model.ImportStatus;
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
import com.rvdijkz.tour.persistence.entity.DoublePointsStageEntity;
import com.rvdijkz.tour.persistence.entity.DoublePointsStageId;
import com.rvdijkz.tour.persistence.entity.EditionEntity;
import com.rvdijkz.tour.persistence.entity.FinalResultEntity;
import com.rvdijkz.tour.persistence.entity.PlayerAccountEntity;
import com.rvdijkz.tour.persistence.entity.RiderEntity;
import com.rvdijkz.tour.persistence.entity.StageResultEntity;
import com.rvdijkz.tour.persistence.repository.DoublePointsStageRepository;
import com.rvdijkz.tour.persistence.repository.EditionRepository;
import com.rvdijkz.tour.persistence.repository.FinalResultRepository;
import com.rvdijkz.tour.persistence.repository.PlayerAccountRepository;
import com.rvdijkz.tour.persistence.repository.PlayerEntryVersionRepository;
import com.rvdijkz.tour.persistence.repository.PlayerPredictionVersionRepository;
import com.rvdijkz.tour.persistence.repository.RiderRepository;
import com.rvdijkz.tour.persistence.repository.StageHistoryRepository;
import com.rvdijkz.tour.persistence.repository.StageResultRepository;
import com.rvdijkz.tour.persistence.repository.StandingRepository;
import com.rvdijkz.tour.persistence.value.PlacedBibData;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AdminApiService {

    private static final long ACTIVE_EDITION_ID = 1L;
    private static final String DEFAULT_REQUIRED_NATIONALITY = "FRA";
    private static final int DEFAULT_RESERVE_BUDGET = 20;
    private static final int FINAL_STAGE_NUMBER = 21;
    private static final int FINISHERS_COUNT = 20;
    private static final int LAST_FIVE_COUNT = 5;
    private static final String PASSWORD_CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%";

    private final EditionRepository editionRepository;
    private final PlayerAccountRepository playerAccountRepository;
    private final RiderRepository riderRepository;
    private final PlayerEntryVersionRepository playerEntryVersionRepository;
    private final PlayerPredictionVersionRepository playerPredictionVersionRepository;
    private final DoublePointsStageRepository doublePointsStageRepository;
    private final StageResultRepository stageResultRepository;
    private final FinalResultRepository finalResultRepository;
    private final StandingRepository standingRepository;
    private final StageHistoryRepository stageHistoryRepository;
    private final StandingsCalculationService standingsCalculationService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final SecureRandom secureRandom = new SecureRandom();

    public AdminApiService(
        EditionRepository editionRepository,
        PlayerAccountRepository playerAccountRepository,
        RiderRepository riderRepository,
        PlayerEntryVersionRepository playerEntryVersionRepository,
        PlayerPredictionVersionRepository playerPredictionVersionRepository,
        DoublePointsStageRepository doublePointsStageRepository,
        StageResultRepository stageResultRepository,
        FinalResultRepository finalResultRepository,
        StandingRepository standingRepository,
        StageHistoryRepository stageHistoryRepository,
        StandingsCalculationService standingsCalculationService
    ) {
        this.editionRepository = editionRepository;
        this.playerAccountRepository = playerAccountRepository;
        this.riderRepository = riderRepository;
        this.playerEntryVersionRepository = playerEntryVersionRepository;
        this.playerPredictionVersionRepository = playerPredictionVersionRepository;
        this.doublePointsStageRepository = doublePointsStageRepository;
        this.stageResultRepository = stageResultRepository;
        this.finalResultRepository = finalResultRepository;
        this.standingRepository = standingRepository;
        this.stageHistoryRepository = stageHistoryRepository;
        this.standingsCalculationService = standingsCalculationService;
    }

    @Transactional
    public CreateEditionResponse createEdition(CreateEditionRequest request) {
        Optional<EditionEntity> currentEdition = editionRepository.findTopByOrderByYearDescIdDesc();
        int retainedReserveBudget = currentEdition.map(EditionEntity::getReserveBudget).orElse(DEFAULT_RESERVE_BUDGET);
        String retainedRequiredNationality = currentEdition.map(EditionEntity::getRequiredNationality).orElse(DEFAULT_REQUIRED_NATIONALITY);
        List<Integer> retainedDoublePointsStages = currentEdition
            .map(edition -> doublePointsStageRepository.findByIdEditionIdOrderByIdStageNumberAsc(edition.getId())
                .stream()
                .map(stage -> stage.getId().getStageNumber())
                .toList())
            .orElse(List.of());

        finalResultRepository.deleteAllInBatch();
        stageResultRepository.deleteAllInBatch();
        standingRepository.deleteAllInBatch();
        stageHistoryRepository.deleteAllInBatch();
        playerPredictionVersionRepository.deleteAllInBatch();
        playerEntryVersionRepository.deleteAllInBatch();
        riderRepository.deleteAllInBatch();
        doublePointsStageRepository.deleteAllInBatch();
        editionRepository.deleteAllInBatch();

        EditionEntity edition = new EditionEntity();
        edition.setId(ACTIVE_EDITION_ID);
        edition.setYear(request.getYear());
        edition.setEntryDeadline(OffsetDateTime.now());
        edition.setReserveBudget(retainedReserveBudget);
        edition.setRequiredNationality(retainedRequiredNationality);
        edition.setCurrentStage(1);
        editionRepository.save(edition);

        for (Integer stageNumber : retainedDoublePointsStages) {
            doublePointsStageRepository.save(createDoublePointsStage(edition.getId(), stageNumber));
        }

        return new CreateEditionResponse(
            edition.getId(),
            edition.getYear(),
            OffsetDateTime.now(),
            new ClearedDataSummary(true, true, true, true)
        );
    }

    @Transactional
    public PlayerAdminResponse createPlayer(CreatePlayerRequest request) {
        if (playerAccountRepository.existsByUsername(request.getUsername())) {
            throw ApiException.conflict("PLAYER_ALREADY_EXISTS", "A player with this username already exists.");
        }

        OffsetDateTime passwordExpiresAt = OffsetDateTime.now().plus(EntryRules.PASSWORD_VALIDITY);
        PlayerAccountEntity player = new PlayerAccountEntity();
        player.setUsername(request.getUsername());
        player.setDisplayName(request.getUsername());
        player.setPasswordHash(passwordEncoder.encode(request.getTemporaryPassword()));
        player.setPasswordExpiresAt(passwordExpiresAt);

        PlayerAccountEntity savedPlayer = playerAccountRepository.save(player);
        return new PlayerAdminResponse(savedPlayer.getId(), savedPlayer.getUsername(), savedPlayer.getPasswordExpiresAt());
    }

    @Transactional
    public ResetPasswordResponse resetPlayerPassword(long playerId, ResetPasswordRequest request) {
        PlayerAccountEntity player = requireKnownPlayer(playerId);
        if (request.getReason() == null) {
            throw ApiException.badRequest("PASSWORD_RESET_REASON_REQUIRED", "Password reset reason is required.");
        }
        String temporaryPassword = generateTemporaryPassword();
        OffsetDateTime passwordExpiresAt = OffsetDateTime.now().plus(EntryRules.PASSWORD_VALIDITY);
        player.setPasswordHash(passwordEncoder.encode(temporaryPassword));
        player.setPasswordExpiresAt(passwordExpiresAt);
        playerAccountRepository.save(player);
        return new ResetPasswordResponse(playerId, temporaryPassword, passwordExpiresAt);
    }

    public RiderImportResponse importRiders(long editionId, MultipartFile file) {
        requireKnownEdition(editionId);
        if (playerEntryVersionRepository.existsByIdEditionId(editionId)) {
            throw ApiException.conflict(
                "RIDER_IMPORT_LOCKED",
                "Rider import is blocked because at least one player has already saved a team."
            );
        }
        if (file == null || file.isEmpty()) {
            throw ApiException.badRequest("RIDER_IMPORT_EMPTY", "The rider import file is required.");
        }
        return new RiderImportResponse(ImportStatus.ACCEPTED, 0);
    }

    @Transactional
    public DeadlineConfigurationResponse updateEditionDeadline(long editionId, UpdateDeadlineRequest request) {
        EditionEntity edition = requireKnownEdition(editionId);
        OffsetDateTime now = OffsetDateTime.now();
        boolean hasSavedStageResult = stageResultRepository.existsByEditionId(editionId);
        boolean reopeningRequested = !now.isBefore(edition.getEntryDeadline()) && request.getEntryDeadline().isAfter(now);
        if (hasSavedStageResult && reopeningRequested) {
            throw ApiException.conflict(
                "ENTRY_REOPEN_LOCKED",
                "Entry deadline cannot be reopened after stage results have been saved."
            );
        }

        edition.setEntryDeadline(request.getEntryDeadline());
        editionRepository.save(edition);
        return new DeadlineConfigurationResponse(editionId, edition.getEntryDeadline(), !hasSavedStageResult);
    }

    @Transactional
    public ReserveBudgetConfigurationResponse updateReserveBudget(long editionId, UpdateReserveBudgetRequest request) {
        EditionEntity edition = requireKnownEdition(editionId);
        requireUnlockedBySavedTeams(editionId, "RESERVE_BUDGET_LOCKED", "Reserve budget is locked after the first team draft has been saved.");
        edition.setReserveBudget(request.getReserveBudget());
        editionRepository.save(edition);
        return new ReserveBudgetConfigurationResponse(editionId, edition.getReserveBudget(), false);
    }

    @Transactional
    public RequiredNationalityConfigurationResponse updateRequiredNationality(
        long editionId,
        UpdateRequiredNationalityRequest request
    ) {
        EditionEntity edition = requireKnownEdition(editionId);
        requireUnlockedBySavedTeams(
            editionId,
            "REQUIRED_NATIONALITY_LOCKED",
            "Required nationality is locked after the first team draft has been saved."
        );
        edition.setRequiredNationality(request.getRequiredNationality());
        editionRepository.save(edition);
        return new RequiredNationalityConfigurationResponse(editionId, edition.getRequiredNationality(), false);
    }

    @Transactional
    public DoublePointsStagesConfigurationResponse updateDoublePointsStages(
        long editionId,
        UpdateDoublePointsStagesRequest request
    ) {
        EditionEntity edition = requireKnownEdition(editionId);
        if (!OffsetDateTime.now().isBefore(edition.getEntryDeadline())) {
            throw ApiException.forbidden(
                "DOUBLE_POINTS_LOCKED",
                "Double-points stage configuration is locked after the entry deadline."
            );
        }

        List<Integer> stageNumbers = request.getDoublePointsStages() == null ? List.of() : List.copyOf(request.getDoublePointsStages());
        ensureUniqueValues(stageNumbers, "DUPLICATE_DOUBLE_POINTS_STAGE", "A stage can only be designated once for double points.");

        doublePointsStageRepository.deleteByIdEditionId(editionId);
        for (Integer stageNumber : stageNumbers) {
            doublePointsStageRepository.save(createDoublePointsStage(editionId, stageNumber));
        }

        return new DoublePointsStagesConfigurationResponse(editionId, stageNumbers, false);
    }

    @Transactional
    public StageResultResponse saveStageResult(long editionId, int stageNumber, SaveStageResultRequest request) {
        EditionEntity edition = requireKnownEdition(editionId);
        validateStageSequenceForSave(edition, stageNumber);
        if (stageResultRepository.findByEditionIdAndStageNumber(editionId, stageNumber).isPresent()) {
            throw ApiException.conflict("STAGE_SEQUENCE_CONFLICT", "The stage result already exists and must be corrected through the correction endpoint.");
        }

        validateStageResultRequest(editionId, request);
        StageResultEntity stageResult = toStageResultEntity(editionId, stageNumber, request);
        stageResult.setSavedAt(OffsetDateTime.now());
        stageResult.setCalculatedAt(null);
        stageResult.setPublished(false);
        stageResultRepository.save(stageResult);
        return new StageResultResponse(editionId, stageNumber, stageResult.getSavedAt());
    }

    @Transactional
    public StageCalculationResponse correctStageResult(long editionId, int stageNumber, SaveStageResultRequest request) {
        requireKnownEdition(editionId);
        StageResultEntity stageResult = stageResultRepository.findByEditionIdAndStageNumber(editionId, stageNumber)
            .orElseThrow(() -> ApiException.notFound("STAGE_NOT_FOUND", "The requested stage result was not found."));
        StageResultEntity latestSavedStage = stageResultRepository.findTopByEditionIdOrderByStageNumberDesc(editionId)
            .orElseThrow(() -> ApiException.notFound("STAGE_NOT_FOUND", "The requested stage result was not found."));
        if (!latestSavedStage.getStageNumber().equals(stageNumber)) {
            throw ApiException.forbidden("STAGE_CORRECTION_FORBIDDEN", "Only the latest saved stage can be corrected.");
        }

        validateStageResultRequest(editionId, request);
        StageResultEntity correctedStage = toStageResultEntity(editionId, stageNumber, request);
        correctedStage.setSavedAt(OffsetDateTime.now());
        correctedStage.setCalculatedAt(stageResult.getCalculatedAt());
        correctedStage.setPublished(false);
        stageResultRepository.save(correctedStage);
        return standingsCalculationService.calculateStage(editionId, stageNumber);
    }

    public StageCalculationResponse calculateStage(long editionId, int stageNumber) {
        requireKnownEdition(editionId);
        return standingsCalculationService.calculateStage(editionId, stageNumber);
    }

    @Transactional
    public FinalResultsResponse saveFinalResults(long editionId, SaveFinalResultsRequest request) {
        requireKnownEdition(editionId);
        validateFinalResultsAllowed(editionId);
        validateFinalResultsRequest(editionId, request);

        FinalResultEntity finalResult = new FinalResultEntity();
        finalResult.setEditionId(editionId);
        applyFinalResultRequest(finalResult, request);
        finalResult.setSavedAt(OffsetDateTime.now());
        finalResult.setCalculatedAt(null);
        finalResult.setPublished(false);
        finalResultRepository.save(finalResult);
        return standingsCalculationService.calculateFinalResults(editionId);
    }

    @Transactional
    public FinalResultsResponse correctFinalResults(long editionId, SaveFinalResultsRequest request) {
        requireKnownEdition(editionId);
        validateFinalResultsAllowed(editionId);
        validateFinalResultsRequest(editionId, request);

        FinalResultEntity finalResult = finalResultRepository.findById(editionId)
            .orElseThrow(() -> ApiException.notFound("FINAL_RESULTS_NOT_FOUND", "Final results have not been saved yet."));
        applyFinalResultRequest(finalResult, request);
        finalResult.setSavedAt(OffsetDateTime.now());
        finalResult.setPublished(false);
        finalResultRepository.save(finalResult);
        return standingsCalculationService.calculateFinalResults(editionId);
    }

    private EditionEntity requireKnownEdition(long editionId) {
        return editionRepository.findById(editionId)
            .orElseThrow(() -> ApiException.notFound("EDITION_NOT_FOUND", "Requested edition was not found."));
    }

    private PlayerAccountEntity requireKnownPlayer(long playerId) {
        return playerAccountRepository.findById(playerId)
            .orElseThrow(() -> ApiException.notFound("PLAYER_NOT_FOUND", "Requested player was not found."));
    }

    private void requireUnlockedBySavedTeams(long editionId, String code, String message) {
        if (playerEntryVersionRepository.existsByIdEditionId(editionId)) {
            throw ApiException.conflict(code, message);
        }
    }

    private DoublePointsStageEntity createDoublePointsStage(long editionId, int stageNumber) {
        DoublePointsStageId id = new DoublePointsStageId();
        id.setEditionId(editionId);
        id.setStageNumber(stageNumber);

        DoublePointsStageEntity entity = new DoublePointsStageEntity();
        entity.setId(id);
        return entity;
    }

    private void validateStageSequenceForSave(EditionEntity edition, int stageNumber) {
        if (edition.getCurrentStage() != stageNumber) {
            throw ApiException.conflict(
                "STAGE_SEQUENCE_CONFLICT",
                "Stage result cannot be saved because the previous stage is not fully processed."
            );
        }

        if (stageNumber > 1) {
            StageResultEntity previousStage = stageResultRepository.findByEditionIdAndStageNumber(edition.getId(), stageNumber - 1)
                .orElseThrow(() -> ApiException.conflict(
                    "STAGE_SEQUENCE_CONFLICT",
                    "Stage result cannot be saved because the previous stage is not fully processed."
                ));
            if (!Boolean.TRUE.equals(previousStage.getPublished())) {
                throw ApiException.conflict(
                    "STAGE_SEQUENCE_CONFLICT",
                    "Stage result cannot be saved because the previous stage is not fully processed."
                );
            }
        }
    }

    private void validateStageResultRequest(long editionId, SaveStageResultRequest request) {
        if (request.getFinishers() == null || request.getFinishers().size() != FINISHERS_COUNT) {
            throw ApiException.badRequest("FINISHERS_INVALID", "Exactly 20 ordered finishers are required.");
        }
        if (request.getLastFive() == null || request.getLastFive().size() != LAST_FIVE_COUNT) {
            throw ApiException.badRequest("LAST_FIVE_INVALID", "Exactly 5 ordered last finishers are required.");
        }
        if (request.getJerseyLeaders() == null) {
            throw ApiException.badRequest("JERSEYS_REQUIRED", "All jersey leaders are required.");
        }

        ensureUniqueValues(
            request.getFinishers().stream().map(result -> result.getRaceBibNumber()).toList(),
            "DUPLICATE_FINISHER",
            "Each finisher race bib number can only appear once in the stage top 20."
        );
        ensureUniqueValues(
            request.getFinishers().stream().map(result -> result.getPlace()).toList(),
            "DUPLICATE_FINISHER_PLACE",
            "Each finishing place can only appear once in the stage top 20."
        );
        ensureUniqueValues(
            request.getLastFive().stream().map(result -> result.getRaceBibNumber()).toList(),
            "DUPLICATE_LAST_FIVE_RIDER",
            "Each race bib number can only appear once in the stage last-five list."
        );
        ensureUniqueValues(
            request.getLastFive().stream().map(result -> result.getPlace()).toList(),
            "DUPLICATE_LAST_FIVE_PLACE",
            "Each last-five placing can only appear once."
        );

        List<Integer> finishers = request.getFinishers().stream().map(result -> result.getRaceBibNumber()).toList();
        List<Integer> lastFive = request.getLastFive().stream().map(result -> result.getRaceBibNumber()).toList();
        HashSet<Integer> overlapCheck = new HashSet<>(finishers);
        overlapCheck.retainAll(lastFive);
        if (!overlapCheck.isEmpty()) {
            throw ApiException.badRequest("RESULT_OVERLAP_INVALID", "Top-20 and last-five stage results may not overlap.");
        }

        List<Integer> allRaceBibs = new java.util.ArrayList<>();
        allRaceBibs.addAll(finishers);
        allRaceBibs.addAll(lastFive);
        allRaceBibs.add(request.getJerseyLeaders().getYellow());
        allRaceBibs.add(request.getJerseyLeaders().getPoints());
        allRaceBibs.add(request.getJerseyLeaders().getMountains());
        allRaceBibs.add(request.getJerseyLeaders().getWhite());
        if (request.getMostCombativeRaceBibNumber() != null) {
            allRaceBibs.add(request.getMostCombativeRaceBibNumber());
        }
        if (request.getWithdrawals() != null) {
            allRaceBibs.addAll(request.getWithdrawals());
        }

        List<Integer> distinctRaceBibs = allRaceBibs.stream().distinct().toList();
        List<RiderEntity> matchedRiders = riderRepository.findByEditionIdAndRaceBibNumberIn(editionId, distinctRaceBibs);
        if (matchedRiders.size() != distinctRaceBibs.size()) {
            throw ApiException.badRequest("RIDER_NOT_FOUND", "One or more race bib numbers do not belong to this edition.");
        }
    }

    private StageResultEntity toStageResultEntity(long editionId, int stageNumber, SaveStageResultRequest request) {
        StageResultEntity entity = new StageResultEntity();
        entity.setEditionId(editionId);
        entity.setStageNumber(stageNumber);
        entity.setFinishers(request.getFinishers().stream().map(item -> new PlacedBibData(item.getPlace(), item.getRaceBibNumber())).toList());
        entity.setLastFive(request.getLastFive().stream().map(item -> new PlacedBibData(item.getPlace(), item.getRaceBibNumber())).toList());
        entity.setYellowRaceBib(request.getJerseyLeaders().getYellow());
        entity.setPointsRaceBib(request.getJerseyLeaders().getPoints());
        entity.setMountainsRaceBib(request.getJerseyLeaders().getMountains());
        entity.setWhiteRaceBib(request.getJerseyLeaders().getWhite());
        entity.setMostCombativeRaceBib(request.getMostCombativeRaceBibNumber());
        entity.setWithdrawals(request.getWithdrawals() == null ? List.of() : List.copyOf(request.getWithdrawals()));
        return entity;
    }

    private void validateFinalResultsAllowed(long editionId) {
        StageResultEntity finalStage = stageResultRepository.findByEditionIdAndStageNumber(editionId, FINAL_STAGE_NUMBER)
            .orElseThrow(() -> ApiException.conflict("FINAL_RESULTS_LOCKED", "Final results require a calculated stage 21 result."));
        if (!Boolean.TRUE.equals(finalStage.getPublished())) {
            throw ApiException.conflict("FINAL_RESULTS_LOCKED", "Final results require a calculated stage 21 result.");
        }
    }

    private void validateFinalResultsRequest(long editionId, SaveFinalResultsRequest request) {
        validateTopFiveList(request.getGeneralTop5RiderIds(), "GENERAL_RESULTS_INVALID");
        validateTopFiveList(request.getPointsTop5RiderIds(), "POINTS_RESULTS_INVALID");
        validateTopFiveList(request.getMountainsTop5RiderIds(), "MOUNTAINS_RESULTS_INVALID");
        if (request.getFinisherCount() == null || request.getFinisherCount() < 0) {
            throw ApiException.badRequest("FINISHER_COUNT_INVALID", "Final results require a non-negative finisher count.");
        }

        List<Long> allRiderIds = new java.util.ArrayList<>();
        allRiderIds.addAll(request.getGeneralTop5RiderIds());
        allRiderIds.addAll(request.getPointsTop5RiderIds());
        allRiderIds.addAll(request.getMountainsTop5RiderIds());
        List<RiderEntity> riders = riderRepository.findByEditionIdAndIdIn(editionId, allRiderIds.stream().distinct().toList());
        if (riders.size() != allRiderIds.stream().distinct().count()) {
            throw ApiException.badRequest("RIDER_NOT_FOUND", "One or more final-result riders do not belong to this edition.");
        }
    }

    private void validateTopFiveList(List<Long> riderIds, String code) {
        if (riderIds == null || riderIds.size() != EntryRules.PREDICTION_LIST_SIZE) {
            throw ApiException.badRequest(code, "Each final classification must contain exactly 5 riders.");
        }
        ensureUniqueValues(riderIds, code, "A rider can only appear once within a final classification top five.");
    }

    private void applyFinalResultRequest(FinalResultEntity finalResult, SaveFinalResultsRequest request) {
        finalResult.setGeneralTop5RiderIds(List.copyOf(request.getGeneralTop5RiderIds()));
        finalResult.setPointsTop5RiderIds(List.copyOf(request.getPointsTop5RiderIds()));
        finalResult.setMountainsTop5RiderIds(List.copyOf(request.getMountainsTop5RiderIds()));
        finalResult.setFinisherCount(request.getFinisherCount());
    }

    private String generateTemporaryPassword() {
        int length = 12;
        StringBuilder builder = new StringBuilder(length);
        for (int index = 0; index < length; index++) {
            int nextIndex = secureRandom.nextInt(PASSWORD_CHARACTERS.length());
            builder.append(PASSWORD_CHARACTERS.charAt(nextIndex));
        }
        return builder.toString();
    }

    private <T> void ensureUniqueValues(List<T> values, String code, String message) {
        if (values.size() != new HashSet<>(values).size()) {
            throw ApiException.badRequest(code, message);
        }
    }
}

