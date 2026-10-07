import type { EditionSummary, HealthResponse } from "./types";
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
}

