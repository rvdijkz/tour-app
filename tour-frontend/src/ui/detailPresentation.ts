import type { ScoreContribution } from "../api/types";

export const SCORE_CONTRIBUTION_PREVIEW_LIMIT = 3;

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

