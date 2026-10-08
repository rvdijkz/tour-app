/**
 * Public health payload returned by the backend API.
 */
export interface HealthResponse {
  status: string;
  timestamp: string;
}

/**
 * Public edition summary payload returned by the backend API.
 */
export interface EditionSummary {
  editionId: number;
  editionYear: number;
  name: string;
  stage: number;
}

/**
 * Public standing row payload returned by the backend API.
 */
export interface StandingRow {
  rank: number;
  playerId: number;
  playerName: string;
  teamName: string;
  totalPoints: number;
}

/**
 * Public standings payload returned by the backend API.
 */
export interface StandingsResponse {
  editionId: number;
  stage: number;
  standings: StandingRow[];
}

export interface StageRiderReference {
  riderId: number;
  riderName: string;
  raceBibNumber: number;
}

export interface ScoreContribution {
  ruleCode: string;
  description: string;
  points: number;
}

export interface RiderStageScore {
  position: number;
  rider: StageRiderReference;
  substitute: boolean;
  points: number;
  scoreItems: ScoreContribution[];
}

export interface StageSubstitution {
  effectiveStage: number;
  position: number;
  replacedRider: StageRiderReference;
  substituteRider: StageRiderReference;
}

export interface StageHistoryItem {
  stage: number;
  stagePoints: number;
  totalPoints: number;
  substitutions: StageSubstitution[];
  riderScores: RiderStageScore[];
}

export interface PlayerStandingDetail {
  editionId: number;
  stage: number;
  player: StandingRow;
  stageHistory: StageHistoryItem[];
}

