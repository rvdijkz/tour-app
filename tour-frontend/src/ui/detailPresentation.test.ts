import { describe, expect, it } from "vitest";
import {
  formatScoreContribution,
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
});

