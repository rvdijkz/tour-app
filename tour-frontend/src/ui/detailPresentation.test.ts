import { describe, expect, it } from "vitest";
import {
  ALL_STAGES_FILTER,
  filterStageHistory,
  formatScoreContribution,
  getStageHistoryFilterOptions,
  getHiddenScoreContributionCount,
  getVisibleScoreContributions
} from "./detailPresentation";

describe("detailPresentation", () => {
  it("limits score contribution preview to the configured number", () => {
    const scoreItems = [
      { ruleCode: "R1", description: "First", points: 10 },
      { ruleCode: "R2", description: "Second", points: 5 },
      { ruleCode: "R3", description: "Third", points: -3 },
      { ruleCode: "R4", description: "Fourth", points: 2 }
    ];

    expect(getVisibleScoreContributions(scoreItems)).toHaveLength(3);
    expect(getHiddenScoreContributionCount(scoreItems)).toBe(1);
  });

  it("formats positive and negative score contributions with an explicit sign", () => {
    expect(formatScoreContribution({ ruleCode: "BONUS", description: "Sprint win", points: 8 })).toBe(
      "BONUS — Sprint win (+8)"
    );
    expect(formatScoreContribution({ ruleCode: "PENALTY", description: "Late change", points: -4 })).toBe(
      "PENALTY — Late change (-4)"
    );
  });

  it("returns stage filter options sorted descending", () => {
    const options = getStageHistoryFilterOptions([
      { stage: 2, stagePoints: 0, totalPoints: 0, substitutions: [], riderScores: [] },
      { stage: 5, stagePoints: 0, totalPoints: 0, substitutions: [], riderScores: [] },
      { stage: 2, stagePoints: 0, totalPoints: 0, substitutions: [], riderScores: [] }
    ]);

    expect(options).toEqual([5, 2]);
  });

  it("filters stage history by selected stage and returns all for ALL filter", () => {
    const stageHistory = [
      { stage: 3, stagePoints: 0, totalPoints: 0, substitutions: [], riderScores: [] },
      { stage: 4, stagePoints: 0, totalPoints: 0, substitutions: [], riderScores: [] }
    ];

    expect(filterStageHistory(stageHistory, 3)).toEqual([stageHistory[0]]);
    expect(filterStageHistory(stageHistory, ALL_STAGES_FILTER)).toEqual(stageHistory);
  });
});

