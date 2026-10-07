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

