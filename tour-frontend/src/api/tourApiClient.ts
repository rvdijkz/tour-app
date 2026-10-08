import type { EditionSummary, HealthResponse, PlayerStandingDetail, StandingsResponse } from "./types";
import type { HttpClient } from "./httpClient";

/**
 * Dedicated frontend service aligned with the backend OpenAPI contract.
 */
export class TourApiClient {
  private readonly httpClient: HttpClient;

  constructor(httpClient: HttpClient) {
    this.httpClient = httpClient;
  }

  getHealth(): Promise<HealthResponse> {
    return this.httpClient.request<HealthResponse>("/health");
  }

  getCurrentEdition(): Promise<EditionSummary> {
    return this.httpClient.request<EditionSummary>("/editions/current");
  }

  getStandings(editionId: number): Promise<StandingsResponse> {
    return this.httpClient.request<StandingsResponse>(`/editions/${editionId}/standings`);
  }

  getPlayerStandingDetail(editionId: number, playerId: number): Promise<PlayerStandingDetail> {
    return this.httpClient.request<PlayerStandingDetail>(`/editions/${editionId}/standings/${playerId}`);
  }
}

