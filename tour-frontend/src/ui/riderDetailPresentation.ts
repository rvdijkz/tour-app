import type { PlayerStandingDetail } from "../api/types";
import { formatScoreContribution } from "./detailPresentation";

const DEFAULT_CONTRIBUTION_LIMIT = 2;

export interface RiderStageScorePreview {
  key: string;
  playerName: string;
  stage: number;
  position: number;
  points: number;
  substitute: boolean;
  contributions: string[];
}

export function buildRiderStageScorePreview(
  playerDetails: PlayerStandingDetail[],
  riderId: number,
  contributionLimit = DEFAULT_CONTRIBUTION_LIMIT
): RiderStageScorePreview[] {
  const previewEntries: RiderStageScorePreview[] = [];

  playerDetails.forEach((playerDetail) => {
    playerDetail.stageHistory.forEach((stageHistoryItem) => {
      stageHistoryItem.riderScores.forEach((riderScore) => {
        if (riderScore.rider.riderId !== riderId) {
          return;
        }

        previewEntries.push({
          key: `${playerDetail.player.playerId}-${stageHistoryItem.stage}-${riderScore.position}-${riderScore.rider.riderId}`,
          playerName: playerDetail.player.playerName,
          stage: stageHistoryItem.stage,
          position: riderScore.position,
          points: riderScore.points,
          substitute: riderScore.substitute,
          contributions: riderScore.scoreItems.slice(0, contributionLimit).map(formatScoreContribution)
        });
      });
    });
  });

  return previewEntries.sort((left, right) => {
    if (left.stage !== right.stage) {
      return right.stage - left.stage;
    }

    return left.playerName.localeCompare(right.playerName);
  });
}

