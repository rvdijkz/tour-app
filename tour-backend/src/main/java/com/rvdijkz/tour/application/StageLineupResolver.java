package com.rvdijkz.tour.application;

import com.rvdijkz.tour.api.error.ApiException;
import com.rvdijkz.tour.persistence.entity.PlayerEntryVersionEntity;
import com.rvdijkz.tour.persistence.entity.RiderEntity;
import com.rvdijkz.tour.persistence.entity.StageResultEntity;
import com.rvdijkz.tour.persistence.repository.RiderRepository;
import com.rvdijkz.tour.persistence.repository.StageResultRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.springframework.stereotype.Service;

@Service
public class StageLineupResolver {

    private final RiderRepository riderRepository;
    private final StageResultRepository stageResultRepository;

    public StageLineupResolver(
        RiderRepository riderRepository,
        StageResultRepository stageResultRepository
    ) {
        this.riderRepository = riderRepository;
        this.stageResultRepository = stageResultRepository;
    }

    public ResolvedStageLineup resolveLineup(long editionId, PlayerEntryVersionEntity entry, int stageNumber) {
        validateStageNumber(stageNumber);
        List<Long> riderIds = requireCompleteTeam(entry);
        Map<Long, RiderEntity> ridersById = loadRidersById(editionId, riderIds);

        Map<Integer, RiderEntity> activeRidersByPosition = createInitialActiveLineup(riderIds, ridersById);
        ReserveState reserveOne = new ReserveState(ridersById.get(riderIds.get(EntryRules.RESERVE_ONE_POSITION - 1)));
        ReserveState reserveTwo = new ReserveState(ridersById.get(riderIds.get(EntryRules.RESERVE_TWO_POSITION - 1)));

        for (int previousStage = 1; previousStage < stageNumber; previousStage++) {
            StageResultEntity stageResult = stageResultRepository.findByEditionIdAndStageNumber(editionId, previousStage)
                .orElseThrow(() -> ApiException.conflict(
                    "STAGE_SEQUENCE_CONFLICT",
                    "Stage lineup resolution requires results for all earlier stages."
                ));
            applyWithdrawalsForCompletedStage(activeRidersByPosition, reserveOne, reserveTwo, stageResult);
        }

        return new ResolvedStageLineup(editionId, stageNumber, activeRidersByPosition);
    }

    private void validateStageNumber(int stageNumber) {
        if (stageNumber < 1) {
            throw ApiException.conflict("INVALID_STAGE_NUMBER", "Stage number must be at least 1.");
        }
    }

    private List<Long> requireCompleteTeam(PlayerEntryVersionEntity entry) {
        List<Long> riderIds = entry.getRiderIds() == null ? List.of() : List.copyOf(entry.getRiderIds());
        if (riderIds.size() != EntryRules.TEAM_SIZE) {
            throw ApiException.conflict(
                "ENTRY_INCOMPLETE",
                "Stage lineup resolution requires a complete submitted team."
            );
        }
        return riderIds;
    }

    private Map<Long, RiderEntity> loadRidersById(long editionId, List<Long> riderIds) {
        List<RiderEntity> riders = riderRepository.findByEditionIdAndIdIn(editionId, riderIds);
        if (riders.size() != riderIds.size()) {
            throw ApiException.notFound("RIDER_NOT_FOUND", "One or more riders in the submitted team were not found.");
        }

        Map<Long, RiderEntity> ridersById = new HashMap<>();
        for (RiderEntity rider : riders) {
            ridersById.put(rider.getId(), rider);
        }

        return ridersById;
    }

    private Map<Integer, RiderEntity> createInitialActiveLineup(List<Long> riderIds, Map<Long, RiderEntity> ridersById) {
        Map<Integer, RiderEntity> activeRidersByPosition = new LinkedHashMap<>();
        for (int position = 1; position <= EntryRules.NATIONALITY_POSITION; position++) {
            activeRidersByPosition.put(position, ridersById.get(riderIds.get(position - 1)));
        }
        activeRidersByPosition.put(EntryRules.KLUNS_POSITION, ridersById.get(riderIds.get(EntryRules.KLUNS_POSITION - 1)));
        return activeRidersByPosition;
    }

    private void applyWithdrawalsForCompletedStage(
        Map<Integer, RiderEntity> activeRidersByPosition,
        ReserveState reserveOne,
        ReserveState reserveTwo,
        StageResultEntity stageResult
    ) {
        Set<Integer> withdrawnRaceBibNumbers = new HashSet<>(
            stageResult.getWithdrawals() == null ? List.of() : stageResult.getWithdrawals()
        );
        if (withdrawnRaceBibNumbers.isEmpty()) {
            return;
        }

        reserveOne.markWithdrawn(withdrawnRaceBibNumbers);
        reserveTwo.markWithdrawn(withdrawnRaceBibNumbers);

        List<Integer> vacatedPositions = new ArrayList<>();
        for (int position = 1; position <= EntryRules.NATIONALITY_POSITION; position++) {
            RiderEntity activeRider = activeRidersByPosition.get(position);
            if (activeRider != null && withdrawnRaceBibNumbers.contains(activeRider.getRaceBibNumber())) {
                activeRidersByPosition.remove(position);
                vacatedPositions.add(position);
            }
        }
        Collections.sort(vacatedPositions);

        RiderEntity klunsRider = activeRidersByPosition.get(EntryRules.KLUNS_POSITION);
        if (klunsRider != null && withdrawnRaceBibNumbers.contains(klunsRider.getRaceBibNumber())) {
            activeRidersByPosition.remove(EntryRules.KLUNS_POSITION);
        }

        for (Integer vacatedPosition : vacatedPositions) {
            ReserveState nextReserve = nextAvailableReserve(reserveOne, reserveTwo);
            if (nextReserve == null) {
                continue;
            }
            nextReserve.activate(vacatedPosition);
            activeRidersByPosition.put(vacatedPosition, nextReserve.rider());
        }
    }

    private ReserveState nextAvailableReserve(ReserveState reserveOne, ReserveState reserveTwo) {
        if (reserveOne.isAvailableForActivation()) {
            return reserveOne;
        }
        if (reserveTwo.isAvailableForActivation()) {
            return reserveTwo;
        }
        return null;
    }

    public record ResolvedStageLineup(
        long editionId,
        int stageNumber,
        Map<Integer, RiderEntity> activeRidersByPosition
    ) {
        public ResolvedStageLineup {
            activeRidersByPosition = Collections.unmodifiableMap(new TreeMap<>(activeRidersByPosition));
        }

        public RiderEntity getRiderAtPosition(int position) {
            return activeRidersByPosition.get(position);
        }

        public boolean isPositionOccupied(int position) {
            return activeRidersByPosition.containsKey(position);
        }
    }

    private static final class ReserveState {

        private final RiderEntity rider;
        private boolean withdrawn;
        private Integer activePosition;

        private ReserveState(RiderEntity rider) {
            this.rider = rider;
            this.withdrawn = Boolean.TRUE.equals(rider.getWithdrawn());
            this.activePosition = null;
        }

        private RiderEntity rider() {
            return rider;
        }

        private boolean isAvailableForActivation() {
            return !withdrawn && activePosition == null;
        }

        private void activate(int position) {
            this.activePosition = position;
        }

        private void markWithdrawn(Set<Integer> withdrawnRaceBibNumbers) {
            if (withdrawnRaceBibNumbers.contains(rider.getRaceBibNumber())) {
                this.withdrawn = true;
                this.activePosition = null;
            }
        }
    }
}

