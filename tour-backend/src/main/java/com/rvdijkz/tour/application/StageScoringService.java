package com.rvdijkz.tour.application;

import com.rvdijkz.tour.persistence.entity.PlayerEntryVersionEntity;
import com.rvdijkz.tour.persistence.entity.RiderEntity;
import com.rvdijkz.tour.persistence.entity.StageResultEntity;
import com.rvdijkz.tour.persistence.value.PlacedBibData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class StageScoringService {

    private static final int EXACT_STAGE_POSITION_BONUS = 2;
    private static final int WITHDRAWAL_STAGE_DEDUCTION = 2;
    private static final int YELLOW_JERSEY_POINTS = 3;
    private static final int MINOR_JERSEY_POINTS = 1;
    private static final int MOST_COMBATIVE_POINTS = 5;
    private static final int KLUNS_WITHDRAWAL_POINTS = 5;
    private static final int KLUNS_LAST_PLACE_POINTS = 5;
    private static final int KLUNS_LAST_FIVE_POINTS = 2;
    private static final int KLUNS_TOP_TWENTY_POINTS = -5;
    private static final int KLUNS_TOP_TEN_POINTS = -10;
    private static final int KLUNS_STAGE_WIN_POINTS = -15;

    public StageScoreBreakdown calculateStageScore(
        PlayerEntryVersionEntity entry,
        StageLineupResolver.ResolvedStageLineup lineup,
        StageResultEntity stageResult,
        StageResultEntity previousStageResult,
        boolean doublePointsStage
    ) {
        Map<Integer, Integer> stagePlacingsByBib = toPlacingsByBib(stageResult.getFinishers());
        Map<Integer, Integer> lastFivePlacingsByBib = toPlacingsByBib(stageResult.getLastFive());
        Set<Integer> withdrawals = Set.copyOf(stageResult.getWithdrawals() == null ? List.of() : stageResult.getWithdrawals());
        Set<Long> reserveRiderIds = reserveRiderIds(entry);

        List<RiderStageBreakdown> riderScores = new ArrayList<>();
        int total = 0;

        for (int position = 1; position <= EntryRules.NATIONALITY_POSITION; position++) {
            RiderEntity rider = lineup.getRiderAtPosition(position);
            if (rider == null) {
                continue;
            }
            RiderStageBreakdown riderScore = scoreActiveRider(
                position,
                rider,
                reserveRiderIds.contains(rider.getId()),
                stageResult,
                previousStageResult,
                stagePlacingsByBib,
                withdrawals,
                doublePointsStage
            );
            riderScores.add(riderScore);
            total += riderScore.points();
        }

        RiderEntity klunsRider = lineup.getRiderAtPosition(EntryRules.KLUNS_POSITION);
        if (klunsRider != null) {
            RiderStageBreakdown klunsScore = scoreKlunsRider(
                klunsRider,
                reserveRiderIds.contains(klunsRider.getId()),
                stagePlacingsByBib,
                lastFivePlacingsByBib,
                withdrawals
            );
            riderScores.add(klunsScore);
            total += klunsScore.points();
        }

        return new StageScoreBreakdown(total, List.copyOf(riderScores));
    }

    private RiderStageBreakdown scoreActiveRider(
        int position,
        RiderEntity rider,
        boolean substitute,
        StageResultEntity stageResult,
        StageResultEntity previousStageResult,
        Map<Integer, Integer> stagePlacingsByBib,
        Set<Integer> withdrawals,
        boolean doublePointsStage
    ) {
        List<ScoreContributionDetail> contributions = new ArrayList<>();
        int raceBibNumber = rider.getRaceBibNumber();

        if (withdrawals.contains(raceBibNumber)) {
            contributions.add(new ScoreContributionDetail(
                ScoringRule.WITHDRAWAL_DEDUCTION,
                -WITHDRAWAL_STAGE_DEDUCTION
            ));
            return new RiderStageBreakdown(position, rider, substitute, -WITHDRAWAL_STAGE_DEDUCTION, List.copyOf(contributions));
        }

        Integer placing = stagePlacingsByBib.get(raceBibNumber);
        if (placing != null) {
            int placingPoints = placingPointsFor(position, placing);
            if (doublePointsStage) {
                placingPoints *= 2;
            }
            if (placingPoints != 0) {
                contributions.add(new ScoreContributionDetail(ScoringRule.STAGE_PLACING, placingPoints));
            }

            if (position <= EntryRules.STANDARD_TEAM_LAST_POSITION && placing == position) {
                contributions.add(new ScoreContributionDetail(ScoringRule.EXACT_POSITION_BONUS, EXACT_STAGE_POSITION_BONUS));
            }
        }

        appendJerseyContributions(contributions, previousStageResult, raceBibNumber);

        if (position <= EntryRules.STANDARD_TEAM_LAST_POSITION && matchesRaceBib(stageResult.getMostCombativeRaceBib(), raceBibNumber)) {
            contributions.add(new ScoreContributionDetail(ScoringRule.MOST_COMBATIVE, MOST_COMBATIVE_POINTS));
        }

        int total = contributions.stream().mapToInt(ScoreContributionDetail::points).sum();
        return new RiderStageBreakdown(position, rider, substitute, total, List.copyOf(contributions));
    }

    private RiderStageBreakdown scoreKlunsRider(
        RiderEntity rider,
        boolean substitute,
        Map<Integer, Integer> stagePlacingsByBib,
        Map<Integer, Integer> lastFivePlacingsByBib,
        Set<Integer> withdrawals
    ) {
        List<ScoreContributionDetail> contributions = new ArrayList<>();
        int raceBibNumber = rider.getRaceBibNumber();

        if (withdrawals.contains(raceBibNumber)) {
            contributions.add(new ScoreContributionDetail(ScoringRule.KLUNS_WITHDRAWAL, KLUNS_WITHDRAWAL_POINTS));
            return new RiderStageBreakdown(EntryRules.KLUNS_POSITION, rider, substitute, KLUNS_WITHDRAWAL_POINTS, List.copyOf(contributions));
        }

        Integer lastFivePlacing = lastFivePlacingsByBib.get(raceBibNumber);
        if (lastFivePlacing != null) {
            int finalStagePlace = lastFivePlacingsByBib.values().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(lastFivePlacing);
            int points = lastFivePlacing == finalStagePlace ? KLUNS_LAST_PLACE_POINTS : KLUNS_LAST_FIVE_POINTS;
            contributions.add(new ScoreContributionDetail(
                lastFivePlacing == finalStagePlace ? ScoringRule.KLUNS_LAST_PLACE : ScoringRule.KLUNS_LAST_FIVE,
                points
            ));
            return new RiderStageBreakdown(EntryRules.KLUNS_POSITION, rider, substitute, points, List.copyOf(contributions));
        }

        Integer placing = stagePlacingsByBib.get(raceBibNumber);
        if (placing != null) {
            if (placing == 1) {
                contributions.add(new ScoreContributionDetail(ScoringRule.KLUNS_STAGE_WIN, KLUNS_STAGE_WIN_POINTS));
            } else if (placing >= 2 && placing <= 10) {
                contributions.add(new ScoreContributionDetail(ScoringRule.KLUNS_TOP_TEN, KLUNS_TOP_TEN_POINTS));
            } else if (placing >= 11 && placing <= 20) {
                contributions.add(new ScoreContributionDetail(ScoringRule.KLUNS_TOP_TWENTY, KLUNS_TOP_TWENTY_POINTS));
            }
        }

        int total = contributions.stream().mapToInt(ScoreContributionDetail::points).sum();
        return new RiderStageBreakdown(EntryRules.KLUNS_POSITION, rider, substitute, total, List.copyOf(contributions));
    }

    private void appendJerseyContributions(List<ScoreContributionDetail> contributions, StageResultEntity previousStageResult, int raceBibNumber) {
        if (previousStageResult == null) {
            return;
        }
        if (matchesRaceBib(previousStageResult.getYellowRaceBib(), raceBibNumber)) {
            contributions.add(new ScoreContributionDetail(ScoringRule.YELLOW_JERSEY, YELLOW_JERSEY_POINTS));
        }
        if (matchesRaceBib(previousStageResult.getPointsRaceBib(), raceBibNumber)) {
            contributions.add(new ScoreContributionDetail(ScoringRule.GREEN_JERSEY, MINOR_JERSEY_POINTS));
        }
        if (matchesRaceBib(previousStageResult.getMountainsRaceBib(), raceBibNumber)) {
            contributions.add(new ScoreContributionDetail(ScoringRule.POLKA_DOT_JERSEY, MINOR_JERSEY_POINTS));
        }
        if (matchesRaceBib(previousStageResult.getWhiteRaceBib(), raceBibNumber)) {
            contributions.add(new ScoreContributionDetail(ScoringRule.WHITE_JERSEY, MINOR_JERSEY_POINTS));
        }
    }

    private Map<Integer, Integer> toPlacingsByBib(List<PlacedBibData> placings) {
        Map<Integer, Integer> placingsByBib = new HashMap<>();
        for (PlacedBibData placing : placings) {
            placingsByBib.put(placing.raceBibNumber(), placing.place());
        }
        return placingsByBib;
    }

    private Set<Long> reserveRiderIds(PlayerEntryVersionEntity entry) {
        List<Long> riderIds = entry.getRiderIds() == null ? List.of() : entry.getRiderIds();
        return Set.of(
            riderIds.get(EntryRules.RESERVE_ONE_POSITION - 1),
            riderIds.get(EntryRules.RESERVE_TWO_POSITION - 1)
        );
    }

    private int placingPointsFor(int position, int placing) {
        if (placing < 1 || placing > 20) {
            return 0;
        }

        if (position == EntryRules.NATIONALITY_POSITION) {
            if (placing <= 8) {
                return standardPlacingPoints(placing);
            }
            return 3;
        }

        if (position >= 1 && position <= EntryRules.STANDARD_TEAM_LAST_POSITION) {
            return standardPlacingPoints(placing);
        }

        return 0;
    }

    private int standardPlacingPoints(int placing) {
        return switch (placing) {
            case 1 -> 12;
            case 2 -> 10;
            case 3 -> 8;
            case 4 -> 7;
            case 5 -> 6;
            case 6 -> 5;
            case 7 -> 4;
            case 8 -> 3;
            case 9 -> 2;
            case 10 -> 1;
            default -> 0;
        };
    }

    private boolean matchesRaceBib(Integer candidateRaceBib, int raceBibNumber) {
        return candidateRaceBib != null && candidateRaceBib == raceBibNumber;
    }

    public enum ScoringRule {
        STAGE_PLACING("STAGE_PLACING", "Stage placing points"),
        EXACT_POSITION_BONUS("EXACT_POSITION_BONUS", "Exact team-position bonus"),
        WITHDRAWAL_DEDUCTION("WITHDRAWAL_DEDUCTION", "Withdrawal-stage deduction"),
        YELLOW_JERSEY("YELLOW_JERSEY", "Yellow jersey leader points"),
        GREEN_JERSEY("GREEN_JERSEY", "Green jersey leader points"),
        POLKA_DOT_JERSEY("POLKA_DOT_JERSEY", "Polka-dot jersey leader points"),
        WHITE_JERSEY("WHITE_JERSEY", "White jersey leader points"),
        MOST_COMBATIVE("MOST_COMBATIVE", "Most-combative rider points"),
        KLUNS_LAST_PLACE("KLUNS_LAST_PLACE", "Kluns last-place bonus"),
        KLUNS_LAST_FIVE("KLUNS_LAST_FIVE", "Kluns last-five bonus"),
        KLUNS_TOP_TWENTY("KLUNS_TOP_TWENTY", "Kluns top-20 penalty"),
        KLUNS_TOP_TEN("KLUNS_TOP_TEN", "Kluns top-10 penalty"),
        KLUNS_STAGE_WIN("KLUNS_STAGE_WIN", "Kluns stage-win penalty"),
        KLUNS_WITHDRAWAL("KLUNS_WITHDRAWAL", "Kluns withdrawal bonus");

        private final String code;
        private final String description;

        ScoringRule(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String code() {
            return code;
        }

        public String description() {
            return description;
        }
    }

    public record StageScoreBreakdown(int totalPoints, List<RiderStageBreakdown> riderScores) {
    }

    public record RiderStageBreakdown(
        int position,
        RiderEntity rider,
        boolean substitute,
        int points,
        List<ScoreContributionDetail> contributions
    ) {
    }

    public record ScoreContributionDetail(ScoringRule rule, int points) {
    }
}

