import { describe, expect, it } from "vitest";
import { buildDashboardUrlSearch, loadDashboardUrlState } from "./dashboardUrlState";

describe("dashboardUrlState", () => {
  it("loads dashboard state values from query params", () => {
    const state = loadDashboardUrlState(
      "?view=riders&spq=alice&steam=Alpha&ssort=POINTS_DESC&rrq=rider&rnat=NLD&rwith=0&rsort=VALUE_DESC&dstage=3&rpstage=2"
    );

    expect(state).toEqual({
      activeView: "riders",
      standingsPlayerQuery: "alice",
      standingsTeamFilter: "Alpha",
      standingsSort: "POINTS_DESC",
      riderSearchQuery: "rider",
      riderNationalityFilter: "NLD",
      includeWithdrawnRiders: false,
      riderSort: "VALUE_DESC",
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
      standingsSort: "RANK",
      riderSearchQuery: "demo",
      riderNationalityFilter: "ALL",
      includeWithdrawnRiders: true,
      riderSort: "NAME",
      detailStageFilter: "ALL",
      riderPreviewStageFilter: 5
    });

    expect(search).toContain("foo=bar");
    expect(search).toContain("view=standings");
    expect(search).toContain("ssort=RANK");
    expect(search).toContain("rwith=1");
    expect(search).toContain("rsort=NAME");
    expect(search).toContain("rrq=demo");
    expect(search).toContain("rpstage=5");
  });
});

