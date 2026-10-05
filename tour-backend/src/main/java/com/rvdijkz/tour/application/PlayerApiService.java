package com.rvdijkz.tour.application;

import com.rvdijkz.tour.api.error.ApiException;
import com.rvdijkz.tour.api.model.ClassificationPrediction;
import com.rvdijkz.tour.api.model.ClassificationType;
import com.rvdijkz.tour.api.model.EntryResponse;
import com.rvdijkz.tour.api.model.EntryStatus;
import com.rvdijkz.tour.api.model.PredictionsResponse;
import com.rvdijkz.tour.api.model.SaveEntryDraftRequest;
import com.rvdijkz.tour.api.model.SavePredictionsRequest;
import com.rvdijkz.tour.persistence.entity.EditionEntity;
import com.rvdijkz.tour.persistence.entity.PlayerAccountEntity;
import com.rvdijkz.tour.persistence.entity.PlayerEditionVersionId;
import com.rvdijkz.tour.persistence.entity.PlayerEntryVersionEntity;
import com.rvdijkz.tour.persistence.entity.PlayerPredictionVersionEntity;
import com.rvdijkz.tour.persistence.entity.RiderEntity;
import com.rvdijkz.tour.persistence.repository.EditionRepository;
import com.rvdijkz.tour.persistence.repository.PlayerEntryVersionRepository;
import com.rvdijkz.tour.persistence.repository.PlayerPredictionVersionRepository;
import com.rvdijkz.tour.persistence.repository.RiderRepository;
import java.time.OffsetDateTime;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlayerApiService {

    private final EditionRepository editionRepository;
    private final RiderRepository riderRepository;
    private final PlayerEntryVersionRepository playerEntryVersionRepository;
    private final PlayerPredictionVersionRepository playerPredictionVersionRepository;
    private final CurrentPlayerProvider currentPlayerProvider;

    public PlayerApiService(
        EditionRepository editionRepository,
        RiderRepository riderRepository,
        PlayerEntryVersionRepository playerEntryVersionRepository,
        PlayerPredictionVersionRepository playerPredictionVersionRepository,
        CurrentPlayerProvider currentPlayerProvider
    ) {
        this.editionRepository = editionRepository;
        this.riderRepository = riderRepository;
        this.playerEntryVersionRepository = playerEntryVersionRepository;
        this.playerPredictionVersionRepository = playerPredictionVersionRepository;
        this.currentPlayerProvider = currentPlayerProvider;
    }

    public EntryResponse getCurrentEntry(long editionId) {
        EditionEntity edition = getEdition(editionId);
        PlayerAccountEntity currentPlayer = currentPlayerProvider.getCurrentPlayer();
        PlayerEntryVersionEntity entry = findCurrentEntryVersion(edition.getId(), currentPlayer.getId())
            .orElseThrow(() -> ApiException.notFound("ENTRY_NOT_FOUND", "No saved entry was found for the current player."));
        return toEntryResponse(entry);
    }

    @Transactional
    public EntryResponse saveEntryDraft(long editionId, SaveEntryDraftRequest request) {
        EditionEntity edition = getEdition(editionId);
        requireEntryWindowOpen(edition);

        PlayerAccountEntity currentPlayer = currentPlayerProvider.getCurrentPlayer();
        List<Long> riderIds = request.getRiderIds() == null ? List.of() : List.copyOf(request.getRiderIds());
        Integer finisherCountPrediction = unwrap(request.getFinisherCountPrediction());
        validateDraftEntryRequest(edition, request.getTeamName(), riderIds, finisherCountPrediction);

        PlayerEntryVersionEntity draftEntry = playerEntryVersionRepository
            .findByIdEditionIdAndIdPlayerIdAndIdStatus(editionId, currentPlayer.getId(), EntryStatus.DRAFT)
            .orElseGet(() -> createEntryVersion(editionId, currentPlayer.getId(), EntryStatus.DRAFT));
        draftEntry.setTeamName(request.getTeamName());
        draftEntry.setRiderIds(riderIds);
        draftEntry.setFinisherCountPrediction(finisherCountPrediction);
        draftEntry.setSubmittedAt(null);
        draftEntry.setUpdatedAt(OffsetDateTime.now());

        return toEntryResponse(playerEntryVersionRepository.save(draftEntry));
    }

    @Transactional
    public EntryResponse submitEntry(long editionId) {
        EditionEntity edition = getEdition(editionId);
        requireEntryWindowOpen(edition);

        PlayerAccountEntity currentPlayer = currentPlayerProvider.getCurrentPlayer();
        PlayerEntryVersionEntity sourceEntry = findCurrentEntryVersion(editionId, currentPlayer.getId())
            .orElseThrow(() -> ApiException.badRequest("ENTRY_INCOMPLETE", "A saved draft entry is required before final submission."));
        PlayerPredictionVersionEntity sourcePrediction = findCurrentPredictionVersion(editionId, currentPlayer.getId())
            .orElseThrow(() -> ApiException.badRequest("PREDICTIONS_INCOMPLETE", "Saved predictions are required before final submission."));

        validateSubmittedEntry(edition, sourceEntry);
        validateSubmittedPredictions(editionId, sourcePrediction);

        OffsetDateTime submittedAt = OffsetDateTime.now();
        PlayerEntryVersionEntity submittedEntry = playerEntryVersionRepository
            .findByIdEditionIdAndIdPlayerIdAndIdStatus(editionId, currentPlayer.getId(), EntryStatus.SUBMITTED)
            .orElseGet(() -> createEntryVersion(editionId, currentPlayer.getId(), EntryStatus.SUBMITTED));
        copyEntry(sourceEntry, submittedEntry);
        submittedEntry.setSubmittedAt(submittedAt);
        submittedEntry.setUpdatedAt(submittedAt);
        playerEntryVersionRepository.save(submittedEntry);

        PlayerPredictionVersionEntity submittedPrediction = playerPredictionVersionRepository
            .findByIdEditionIdAndIdPlayerIdAndIdStatus(editionId, currentPlayer.getId(), EntryStatus.SUBMITTED)
            .orElseGet(() -> createPredictionVersion(editionId, currentPlayer.getId(), EntryStatus.SUBMITTED));
        copyPrediction(sourcePrediction, submittedPrediction);
        submittedPrediction.setUpdatedAt(submittedAt);
        playerPredictionVersionRepository.save(submittedPrediction);

        return toEntryResponse(submittedEntry);
    }

    public PredictionsResponse getCurrentPredictions(long editionId) {
        getEdition(editionId);
        PlayerAccountEntity currentPlayer = currentPlayerProvider.getCurrentPlayer();
        PlayerPredictionVersionEntity predictions = findCurrentPredictionVersion(editionId, currentPlayer.getId())
            .orElseThrow(() -> ApiException.notFound("PREDICTIONS_NOT_FOUND", "No saved predictions were found for the current player."));
        return toPredictionsResponse(predictions);
    }

    @Transactional
    public PredictionsResponse savePredictions(long editionId, SavePredictionsRequest request) {
        EditionEntity edition = getEdition(editionId);
        requireEntryWindowOpen(edition);

        PlayerAccountEntity currentPlayer = currentPlayerProvider.getCurrentPlayer();
        List<ClassificationPrediction> predictions = request.getPredictions() == null ? List.of() : List.copyOf(request.getPredictions());
        Map<ClassificationType, List<Long>> predictionMap = normalizePredictionMap(predictions);
        validateDraftPredictions(editionId, predictionMap);

        PlayerPredictionVersionEntity draftPrediction = playerPredictionVersionRepository
            .findByIdEditionIdAndIdPlayerIdAndIdStatus(editionId, currentPlayer.getId(), EntryStatus.DRAFT)
            .orElseGet(() -> createPredictionVersion(editionId, currentPlayer.getId(), EntryStatus.DRAFT));
        draftPrediction.setGeneralRiderIds(predictionMap.get(ClassificationType.GENERAL));
        draftPrediction.setPointsRiderIds(predictionMap.get(ClassificationType.POINTS));
        draftPrediction.setMountainsRiderIds(predictionMap.get(ClassificationType.MOUNTAINS));
        draftPrediction.setUpdatedAt(OffsetDateTime.now());

        return toPredictionsResponse(playerPredictionVersionRepository.save(draftPrediction));
    }

    private EditionEntity getEdition(long editionId) {
        return editionRepository.findById(editionId)
            .orElseThrow(() -> ApiException.notFound("EDITION_NOT_FOUND", "Requested edition was not found."));
    }

    private Optional<PlayerEntryVersionEntity> findCurrentEntryVersion(long editionId, long playerId) {
        Optional<PlayerEntryVersionEntity> draft = playerEntryVersionRepository
            .findByIdEditionIdAndIdPlayerIdAndIdStatus(editionId, playerId, EntryStatus.DRAFT);
        if (draft.isPresent()) {
            return draft;
        }
        return playerEntryVersionRepository.findByIdEditionIdAndIdPlayerIdAndIdStatus(editionId, playerId, EntryStatus.SUBMITTED);
    }

    private Optional<PlayerPredictionVersionEntity> findCurrentPredictionVersion(long editionId, long playerId) {
        Optional<PlayerPredictionVersionEntity> draft = playerPredictionVersionRepository
            .findByIdEditionIdAndIdPlayerIdAndIdStatus(editionId, playerId, EntryStatus.DRAFT);
        if (draft.isPresent()) {
            return draft;
        }
        return playerPredictionVersionRepository.findByIdEditionIdAndIdPlayerIdAndIdStatus(editionId, playerId, EntryStatus.SUBMITTED);
    }

    private void requireEntryWindowOpen(EditionEntity edition) {
        if (!OffsetDateTime.now().isBefore(edition.getEntryDeadline())) {
            throw ApiException.badRequest("ENTRY_CLOSED", "Entry changes are closed at or after the configured deadline.");
        }
    }

    private void validateDraftEntryRequest(
        EditionEntity edition,
        String teamName,
        List<Long> riderIds,
        Integer finisherCountPrediction
    ) {
        if (teamName == null || teamName.isBlank()) {
            throw ApiException.badRequest("TEAM_NAME_REQUIRED", "Team name is required.");
        }
        if (riderIds.size() > EntryRules.TEAM_SIZE) {
            throw ApiException.badRequest("TOO_MANY_RIDERS", "A team can contain at most 14 riders.");
        }
        if (finisherCountPrediction != null && finisherCountPrediction < 0) {
            throw ApiException.badRequest("FINISHER_COUNT_INVALID", "Finisher count prediction must be zero or higher.");
        }

        ensureUniqueValues(riderIds, "DUPLICATE_RIDER", "A rider can only be selected once in a team.");
        Map<Long, RiderEntity> ridersById = loadRiders(edition.getId(), riderIds);
        validateNationalitySelection(edition, riderIds, ridersById);
        validateBudgetRules(edition, riderIds, ridersById);
    }

    private void validateSubmittedEntry(EditionEntity edition, PlayerEntryVersionEntity entry) {
        if (entry.getRiderIds().size() != EntryRules.TEAM_SIZE) {
            throw ApiException.badRequest("ENTRY_INCOMPLETE", "Final submission requires a complete 14-rider team.");
        }
        if (entry.getFinisherCountPrediction() == null) {
            throw ApiException.badRequest("FINISHER_COUNT_REQUIRED", "Final submission requires a finisher-count prediction.");
        }
        validateDraftEntryRequest(edition, entry.getTeamName(), entry.getRiderIds(), entry.getFinisherCountPrediction());
    }

    private void validateDraftPredictions(long editionId, Map<ClassificationType, List<Long>> predictionMap) {
        List<Long> allRiderIds = predictionMap.values().stream().flatMap(List::stream).toList();
        loadRiders(editionId, allRiderIds);

        for (Map.Entry<ClassificationType, List<Long>> entry : predictionMap.entrySet()) {
            if (entry.getValue().size() > EntryRules.PREDICTION_LIST_SIZE) {
                throw ApiException.badRequest("PREDICTION_TOO_LONG", "Each classification prediction may contain at most 5 riders.");
            }
            ensureUniqueValues(
                entry.getValue(),
                "DUPLICATE_PREDICTION_RIDER",
                "A rider can only appear once within the same classification prediction list."
            );
        }
    }

    private void validateSubmittedPredictions(long editionId, PlayerPredictionVersionEntity predictions) {
        Map<ClassificationType, List<Long>> predictionMap = Map.of(
            ClassificationType.GENERAL, predictions.getGeneralRiderIds(),
            ClassificationType.POINTS, predictions.getPointsRiderIds(),
            ClassificationType.MOUNTAINS, predictions.getMountainsRiderIds()
        );
        validateDraftPredictions(editionId, predictionMap);
        boolean complete = predictionMap.values().stream().allMatch(riderIds -> riderIds.size() == EntryRules.PREDICTION_LIST_SIZE);
        if (!complete) {
            throw ApiException.badRequest(
                "PREDICTIONS_INCOMPLETE",
                "Final submission requires all three prediction lists with exactly 5 riders each."
            );
        }
    }

    private Map<Long, RiderEntity> loadRiders(long editionId, List<Long> riderIds) {
        if (riderIds.isEmpty()) {
            return Map.of();
        }

        List<RiderEntity> riders = riderRepository.findByEditionIdAndIdIn(editionId, riderIds);
        if (riders.size() != riderIds.stream().distinct().count()) {
            throw ApiException.badRequest("RIDER_NOT_FOUND", "One or more selected riders do not belong to this edition.");
        }
        return riders.stream().collect(java.util.stream.Collectors.toMap(RiderEntity::getId, rider -> rider));
    }

    private void validateNationalitySelection(EditionEntity edition, List<Long> riderIds, Map<Long, RiderEntity> ridersById) {
        if (riderIds.size() < EntryRules.NATIONALITY_POSITION) {
            return;
        }
        RiderEntity nationalityRider = ridersById.get(riderIds.get(EntryRules.NATIONALITY_POSITION - 1));
        if (nationalityRider == null || !edition.getRequiredNationality().equals(nationalityRider.getNationality())) {
            throw ApiException.badRequest(
                "REQUIRED_NATIONALITY_MISMATCH",
                "The rider in position 11 must match the configured required nationality."
            );
        }
    }

    private void validateBudgetRules(EditionEntity edition, List<Long> riderIds, Map<Long, RiderEntity> ridersById) {
        int mainTeamValue = sumValues(riderIds, ridersById, 0, Math.min(EntryRules.NATIONALITY_POSITION, riderIds.size()));
        if (mainTeamValue > EntryRules.MAIN_TEAM_BUDGET) {
            throw ApiException.badRequest("MAIN_TEAM_BUDGET_EXCEEDED", "The combined rider value for positions 1-11 may not exceed 100.");
        }

        int reserveEndExclusive = Math.min(EntryRules.KLUNS_POSITION - 1, riderIds.size());
        int reserveValue = sumValues(riderIds, ridersById, EntryRules.RESERVE_ONE_POSITION - 1, reserveEndExclusive);
        if (reserveValue > edition.getReserveBudget()) {
            throw ApiException.badRequest(
                "RESERVE_BUDGET_EXCEEDED",
                "The combined rider value for reserve positions 12-13 exceeds the configured reserve budget."
            );
        }
    }

    private int sumValues(List<Long> riderIds, Map<Long, RiderEntity> ridersById, int startIndexInclusive, int endIndexExclusive) {
        int total = 0;
        for (int index = startIndexInclusive; index < endIndexExclusive; index++) {
            RiderEntity rider = ridersById.get(riderIds.get(index));
            if (rider != null) {
                total += rider.getValue();
            }
        }
        return total;
    }

    private Map<ClassificationType, List<Long>> normalizePredictionMap(List<ClassificationPrediction> predictions) {
        Map<ClassificationType, List<Long>> predictionMap = new EnumMap<>(ClassificationType.class);
        predictionMap.put(ClassificationType.GENERAL, List.of());
        predictionMap.put(ClassificationType.POINTS, List.of());
        predictionMap.put(ClassificationType.MOUNTAINS, List.of());

        HashSet<ClassificationType> seenTypes = new HashSet<>();
        for (ClassificationPrediction prediction : predictions) {
            if (!seenTypes.add(prediction.getClassification())) {
                throw ApiException.badRequest("DUPLICATE_CLASSIFICATION", "Each classification can only be supplied once per request.");
            }
            predictionMap.put(
                prediction.getClassification(),
                prediction.getRiderIds() == null ? List.of() : List.copyOf(prediction.getRiderIds())
            );
        }

        return predictionMap;
    }

    private <T> void ensureUniqueValues(List<T> values, String code, String message) {
        if (values.size() != new HashSet<>(values).size()) {
            throw ApiException.badRequest(code, message);
        }
    }

    private PlayerEntryVersionEntity createEntryVersion(long editionId, long playerId, EntryStatus status) {
        PlayerEntryVersionEntity entity = new PlayerEntryVersionEntity();
        entity.setId(createVersionId(editionId, playerId, status));
        entity.setRiderIds(List.of());
        return entity;
    }

    private PlayerPredictionVersionEntity createPredictionVersion(long editionId, long playerId, EntryStatus status) {
        PlayerPredictionVersionEntity entity = new PlayerPredictionVersionEntity();
        entity.setId(createVersionId(editionId, playerId, status));
        entity.setGeneralRiderIds(List.of());
        entity.setPointsRiderIds(List.of());
        entity.setMountainsRiderIds(List.of());
        return entity;
    }

    private PlayerEditionVersionId createVersionId(long editionId, long playerId, EntryStatus status) {
        PlayerEditionVersionId id = new PlayerEditionVersionId();
        id.setEditionId(editionId);
        id.setPlayerId(playerId);
        id.setStatus(status);
        return id;
    }

    private void copyEntry(PlayerEntryVersionEntity source, PlayerEntryVersionEntity target) {
        target.setTeamName(source.getTeamName());
        target.setRiderIds(List.copyOf(source.getRiderIds()));
        target.setFinisherCountPrediction(source.getFinisherCountPrediction());
    }

    private void copyPrediction(PlayerPredictionVersionEntity source, PlayerPredictionVersionEntity target) {
        target.setGeneralRiderIds(List.copyOf(source.getGeneralRiderIds()));
        target.setPointsRiderIds(List.copyOf(source.getPointsRiderIds()));
        target.setMountainsRiderIds(List.copyOf(source.getMountainsRiderIds()));
    }

    private EntryResponse toEntryResponse(PlayerEntryVersionEntity entry) {
        EntryResponse response = new EntryResponse(
            entry.getId().getEditionId(),
            entry.getId().getPlayerId(),
            entry.getId().getStatus(),
            entry.getTeamName(),
            entry.getRiderIds()
        );
        if (entry.getFinisherCountPrediction() != null) {
            response.finisherCountPrediction(entry.getFinisherCountPrediction());
        }
        if (entry.getSubmittedAt() != null) {
            response.submittedAt(entry.getSubmittedAt());
        }
        return response;
    }

    private PredictionsResponse toPredictionsResponse(PlayerPredictionVersionEntity prediction) {
        return new PredictionsResponse(
            prediction.getId().getEditionId(),
            prediction.getId().getPlayerId(),
            List.of(
                new ClassificationPrediction(ClassificationType.GENERAL, prediction.getGeneralRiderIds()),
                new ClassificationPrediction(ClassificationType.POINTS, prediction.getPointsRiderIds()),
                new ClassificationPrediction(ClassificationType.MOUNTAINS, prediction.getMountainsRiderIds())
            )
        );
    }

    private Integer unwrap(JsonNullable<Integer> value) {
        if (value == null || value.isUndefined()) {
            return null;
        }
        return value.orElse(null);
    }
}

