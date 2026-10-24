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
 * Public rider summary payload returned by the backend API.
 */
export interface RiderSummary {
  riderId: number;
  gameRiderNumber: number;
  raceBibNumber: number;
  name: string;
  value: number;
  nationality: string;
  withdrawn: boolean;
}

/**
 * Public rider list payload returned by the backend API.
 */
export interface RiderListResponse {
  editionId: number;
  riders: RiderSummary[];
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

export interface ClearedDataSummary {
  teams: boolean;
  predictions: boolean;
  stageResults: boolean;
  standings: boolean;
}

export interface CreateEditionRequest {
  year: number;
}

export interface CreateEditionResponse {
  editionId: number;
  year: number;
  createdAt: string;
  cleared: ClearedDataSummary;
}

export interface UpdateDeadlineRequest {
  entryDeadline: string;
}

export interface DeadlineConfigurationResponse {
  editionId: number;
  entryDeadline: string;
  reopenAllowed: boolean;
}

export interface UpdateReserveBudgetRequest {
  reserveBudget: number;
}

export interface ReserveBudgetConfigurationResponse {
  editionId: number;
  reserveBudget: number;
  locked: boolean;
}

export interface UpdateRequiredNationalityRequest {
  requiredNationality: string;
}

export interface RequiredNationalityConfigurationResponse {
  editionId: number;
  requiredNationality: string;
  locked: boolean;
}

export interface UpdateDoublePointsStagesRequest {
  doublePointsStages: number[];
}

export interface DoublePointsStagesConfigurationResponse {
  editionId: number;
  doublePointsStages: number[];
  locked: boolean;
}

