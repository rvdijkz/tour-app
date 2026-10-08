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
});


