import { describe, expect, it } from "vitest";
import { loadDashboardState, saveDashboardState, type PersistedDashboardState } from "./dashboardStatePersistence";

class FakeStorage {
  private readonly values = new Map<string, string>();

  getItem(key: string): string | null {
    return this.values.get(key) ?? null;
  }

  setItem(key: string, value: string): void {
    this.values.set(key, value);
  }
}

describe("dashboardStatePersistence", () => {
  it("saves and loads dashboard state", () => {
    const storage = new FakeStorage();
    const expected: PersistedDashboardState = {
      activeView: "riders",
      standingsPlayerQuery: "player",
      standingsTeamFilter: "Alpha",
      standingsSort: "POINTS_DESC",
      riderSearchQuery: "rider",
      riderNationalityFilter: "NLD",
      includeWithdrawnRiders: false,
      riderSort: "VALUE_DESC",
      detailStageFilter: 2,
      riderPreviewStageFilter: "ALL"
    };

    saveDashboardState(storage, expected);

    expect(loadDashboardState(storage)).toEqual(expected);
  });

  it("returns null for invalid stored payload", () => {
    const storage = new FakeStorage();
    storage.setItem("tour.dashboard.state.v1", JSON.stringify({ activeView: "invalid" }));

    expect(loadDashboardState(storage)).toBeNull();
  });

  it("returns null for malformed json", () => {
    const storage = new FakeStorage();
    storage.setItem("tour.dashboard.state.v1", "{not valid json}");

    expect(loadDashboardState(storage)).toBeNull();
  });

  it("defaults missing sort fields for backward compatibility", () => {
    const storage = new FakeStorage();
    storage.setItem(
      "tour.dashboard.state.v1",
      JSON.stringify({
        activeView: "standings",
        standingsPlayerQuery: "",
        standingsTeamFilter: "ALL",
        riderSearchQuery: "",
        riderNationalityFilter: "ALL",
        includeWithdrawnRiders: true,
        detailStageFilter: "ALL",
        riderPreviewStageFilter: "ALL"
      })
    );

    expect(loadDashboardState(storage)).toEqual({
      activeView: "standings",
      standingsPlayerQuery: "",
      standingsTeamFilter: "ALL",
      standingsSort: "RANK",
      riderSearchQuery: "",
      riderNationalityFilter: "ALL",
      includeWithdrawnRiders: true,
      riderSort: "NAME",
      detailStageFilter: "ALL",
      riderPreviewStageFilter: "ALL"
    });
  });
});

