import { describe, expect, it } from "vitest";
import {
  ALL_TEAMS_FILTER,
  filterStandings,
  getTeamOptions,
  sortStandings,
  STANDINGS_SORT_POINTS_DESC,
  STANDINGS_SORT_RANK
} from "./standingsPresentation";

const standings = [
  {
    rank: 1,
    playerId: 11,
    playerName: "Alice Attack",
    teamName: "Alpha",
    totalPoints: 120
  },
  {
    rank: 2,
    playerId: 12,
    playerName: "Bob Breakaway",
    teamName: "Beta",
    totalPoints: 117
  },
  {
    rank: 3,
    playerId: 13,
    playerName: "Carla Climber",
    teamName: "Alpha",
    totalPoints: 113
  }
];

describe("standingsPresentation", () => {
  it("returns unique sorted team options", () => {
    expect(getTeamOptions(standings)).toEqual(["Alpha", "Beta"]);
  });

  it("filters by player query", () => {
    const filtered = filterStandings(standings, {
      playerQuery: "bob",
      teamName: ALL_TEAMS_FILTER
    });

    expect(filtered).toHaveLength(1);
    expect(filtered[0].playerName).toBe("Bob Breakaway");
  });

  it("filters by team and player together", () => {
    const filtered = filterStandings(standings, {
      playerQuery: "carla",
      teamName: "Alpha"
    });

    expect(filtered).toHaveLength(1);
    expect(filtered[0].playerName).toBe("Carla Climber");
  });

  it("sorts standings by rank ascending", () => {
    const sorted = sortStandings([standings[2], standings[0], standings[1]], STANDINGS_SORT_RANK);

    expect(sorted.map((row) => row.rank)).toEqual([1, 2, 3]);
  });

  it("sorts standings by points descending with rank tie-break", () => {
    const sorted = sortStandings(
      [
        { ...standings[0], totalPoints: 120, rank: 2 },
        { ...standings[1], totalPoints: 120, rank: 1 },
        standings[2]
      ],
      STANDINGS_SORT_POINTS_DESC
    );

    expect(sorted.map((row) => row.playerId)).toEqual([12, 11, 13]);
  });
});


