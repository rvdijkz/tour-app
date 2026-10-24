import { describe, expect, it } from "vitest";
import { buildDashboardUrlSearch, loadDashboardUrlState } from "./dashboardUrlState";

describe("dashboardUrlState", () => {
  it("loads dashboard state values from query params", () => {
    const state = loadDashboardUrlState(
      "?view=riders&spq=alice&steam=Alpha&rrq=rider&rnat=NLD&rwith=0&dstage=3&rpstage=2"
    );

    expect(state).toEqual({
      activeView: "riders",
      standingsPlayerQuery: "alice",
      standingsTeamFilter: "Alpha",
      riderSearchQuery: "rider",
      riderNationalityFilter: "NLD",
      includeWithdrawnRiders: false,
      detailStageFilter: 3,
      riderPreviewStageFilter: 2
    });
  });

  it("returns null when no known query params are present", () => {
    expect(loadDashboardUrlState("?foo=bar")).toBeNull();
  });

  it("builds dashboard query params and preserves unrelated params", () => {
    const search = buildDashboardUrlSearch("?foo=bar", {
      activeView: "standings",
      standingsPlayerQuery: "",
      standingsTeamFilter: "ALL",
      riderSearchQuery: "demo",
      riderNationalityFilter: "ALL",
      includeWithdrawnRiders: true,
      detailStageFilter: "ALL",
      riderPreviewStageFilter: 5
    });

    expect(search).toContain("foo=bar");
    expect(search).toContain("view=standings");
    expect(search).toContain("rwith=1");
    expect(search).toContain("rrq=demo");
    expect(search).toContain("rpstage=5");
  });
});

