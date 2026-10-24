import { describe, expect, it } from "vitest";
import { ALL_TEAMS_FILTER, filterStandings, getTeamOptions } from "./standingsPresentation";

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
});


