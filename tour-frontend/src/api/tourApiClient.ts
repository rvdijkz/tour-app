import type {
  CreateEditionRequest,
  CreateEditionResponse,
  DeadlineConfigurationResponse,
  DoublePointsStagesConfigurationResponse,
  EditionSummary,
  HealthResponse,
  PlayerStandingDetail,
  RequiredNationalityConfigurationResponse,
  ReserveBudgetConfigurationResponse,
  RiderListResponse,
  StandingsResponse,
  UpdateDeadlineRequest,
  UpdateDoublePointsStagesRequest,
  UpdateRequiredNationalityRequest,
  UpdateReserveBudgetRequest
} from "./types";
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

  listRiders(editionId: number): Promise<RiderListResponse> {
    return this.httpClient.request<RiderListResponse>(`/editions/${editionId}/riders`);
  }

  getStandings(editionId: number): Promise<StandingsResponse> {
    return this.httpClient.request<StandingsResponse>(`/editions/${editionId}/standings`);
  }

  getPlayerStandingDetail(editionId: number, playerId: number): Promise<PlayerStandingDetail> {
    return this.httpClient.request<PlayerStandingDetail>(`/editions/${editionId}/standings/${playerId}`);
  }

  createEdition(request: CreateEditionRequest): Promise<CreateEditionResponse> {
    return this.httpClient.request<CreateEditionResponse>("/admin/editions", {
      method: "POST",
      body: request
    });
  }

  updateEditionDeadline(editionId: number, request: UpdateDeadlineRequest): Promise<DeadlineConfigurationResponse> {
    return this.httpClient.request<DeadlineConfigurationResponse>(`/admin/editions/${editionId}/deadline`, {
      method: "PATCH",
      body: request
    });
  }

  updateReserveBudget(
    editionId: number,
    request: UpdateReserveBudgetRequest
  ): Promise<ReserveBudgetConfigurationResponse> {
    return this.httpClient.request<ReserveBudgetConfigurationResponse>(`/admin/editions/${editionId}/reserve-budget`, {
      method: "PATCH",
      body: request
    });
  }

  updateRequiredNationality(
    editionId: number,
    request: UpdateRequiredNationalityRequest
  ): Promise<RequiredNationalityConfigurationResponse> {
    return this.httpClient.request<RequiredNationalityConfigurationResponse>(`/admin/editions/${editionId}/required-nationality`, {
      method: "PATCH",
      body: request
    });
  }

  updateDoublePointsStages(
    editionId: number,
    request: UpdateDoublePointsStagesRequest
  ): Promise<DoublePointsStagesConfigurationResponse> {
    return this.httpClient.request<DoublePointsStagesConfigurationResponse>(`/admin/editions/${editionId}/double-points-stages`, {
      method: "PATCH",
      body: request
    });
  }
}

