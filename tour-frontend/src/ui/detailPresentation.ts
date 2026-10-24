import type { ScoreContribution, StageHistoryItem } from "../api/types";

export const SCORE_CONTRIBUTION_PREVIEW_LIMIT = 3;
export const ALL_STAGES_FILTER = "ALL";

export type StageHistoryFilter = typeof ALL_STAGES_FILTER | number;

export function getVisibleScoreContributions(
  scoreItems: ScoreContribution[],
  limit: number = SCORE_CONTRIBUTION_PREVIEW_LIMIT
): ScoreContribution[] {
  return scoreItems.slice(0, limit);
}

export function getHiddenScoreContributionCount(
  scoreItems: ScoreContribution[],
  limit: number = SCORE_CONTRIBUTION_PREVIEW_LIMIT
): number {
  return Math.max(scoreItems.length - limit, 0);
}

export function formatScoreContribution(contribution: ScoreContribution): string {
  const sign = contribution.points > 0 ? "+" : "";
  return `${contribution.ruleCode} — ${contribution.description} (${sign}${contribution.points})`;
}

export function getStageHistoryFilterOptions(stageHistory: StageHistoryItem[]): number[] {
  const stageNumbers = new Set<number>();

  stageHistory.forEach((item) => {
    stageNumbers.add(item.stage);
  });

  return Array.from(stageNumbers).sort((left, right) => right - left);
}

export function filterStageHistory(
  stageHistory: StageHistoryItem[],
  stageFilter: StageHistoryFilter
): StageHistoryItem[] {
  if (stageFilter === ALL_STAGES_FILTER) {
    return stageHistory;
  }

  return stageHistory.filter((item) => item.stage === stageFilter);
}

