import { describe, expect, it, vi } from "vitest";
import { TourApiClient } from "./tourApiClient";
import type { HttpClient } from "./httpClient";

describe("TourApiClient", () => {
  it("calls current-edition endpoint", async () => {
    const request = vi.fn().mockResolvedValue({
      editionId: 1,
      editionYear: 2026,
      name: "Tour",
      stage: 5
    });

    const client = new TourApiClient({ request } as unknown as HttpClient);

    await client.getCurrentEdition();

    expect(request).toHaveBeenCalledWith("/editions/current");
  });

  it("calls standings endpoint for the given edition", async () => {
    const request = vi.fn().mockResolvedValue({
      editionId: 1,
      stage: 5,
      standings: []
    });

    const client = new TourApiClient({ request } as unknown as HttpClient);

    await client.getStandings(1);

    expect(request).toHaveBeenCalledWith("/editions/1/standings");
  });

  it("calls rider list endpoint for the given edition", async () => {
    const request = vi.fn().mockResolvedValue({
      editionId: 1,
      riders: []
    });

    const client = new TourApiClient({ request } as unknown as HttpClient);

    await client.listRiders(1);

    expect(request).toHaveBeenCalledWith("/editions/1/riders");
  });

  it("calls player standing detail endpoint for edition and player", async () => {
    const request = vi.fn().mockResolvedValue({
      editionId: 1,
      stage: 5,
      player: {
        rank: 1,
        playerId: 2,
        playerName: "Alice",
        teamName: "Alpha",
        totalPoints: 120
      },
      stageHistory: []
    });

    const client = new TourApiClient({ request } as unknown as HttpClient);

    await client.getPlayerStandingDetail(1, 2);

    expect(request).toHaveBeenCalledWith("/editions/1/standings/2");
  });

  it("calls create edition admin endpoint", async () => {
    const request = vi.fn().mockResolvedValue({
      editionId: 2,
      year: 2027,
      createdAt: "2026-10-24T12:00:00Z",
      cleared: {
        teams: true,
        predictions: true,
        stageResults: true,
        standings: true
      }
    });

    const client = new TourApiClient({ request } as unknown as HttpClient);

    await client.createEdition({ year: 2027 });

    expect(request).toHaveBeenCalledWith("/admin/editions", {
      method: "POST",
      body: { year: 2027 }
    });
  });

  it("calls update deadline admin endpoint", async () => {
    const request = vi.fn().mockResolvedValue({ editionId: 1, entryDeadline: "2026-12-31T12:00:00Z", reopenAllowed: true });
    const client = new TourApiClient({ request } as unknown as HttpClient);

    await client.updateEditionDeadline(1, { entryDeadline: "2026-12-31T12:00:00Z" });

    expect(request).toHaveBeenCalledWith("/admin/editions/1/deadline", {
      method: "PATCH",
      body: { entryDeadline: "2026-12-31T12:00:00Z" }
    });
  });

  it("calls update reserve budget, nationality, and double-points endpoints", async () => {
    const request = vi.fn().mockResolvedValue({});
    const client = new TourApiClient({ request } as unknown as HttpClient);

    await client.updateReserveBudget(1, { reserveBudget: 25 });
    await client.updateRequiredNationality(1, { requiredNationality: "NLD" });
    await client.updateDoublePointsStages(1, { doublePointsStages: [5, 12] });

    expect(request).toHaveBeenNthCalledWith(1, "/admin/editions/1/reserve-budget", {
      method: "PATCH",
      body: { reserveBudget: 25 }
    });
    expect(request).toHaveBeenNthCalledWith(2, "/admin/editions/1/required-nationality", {
      method: "PATCH",
      body: { requiredNationality: "NLD" }
    });
    expect(request).toHaveBeenNthCalledWith(3, "/admin/editions/1/double-points-stages", {
      method: "PATCH",
      body: { doublePointsStages: [5, 12] }
    });
  });
});


