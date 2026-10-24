import type { StandingRow } from "../api/types";

export const ALL_TEAMS_FILTER = "ALL";

export interface StandingsFilters {
  playerQuery: string;
  teamName: string;
}

export function getTeamOptions(standings: StandingRow[]): string[] {
  const options = new Set<string>();

  standings.forEach((row) => {
    if (row.teamName.trim() !== "") {
      options.add(row.teamName);
    }
  });

  return Array.from(options).sort((left, right) => left.localeCompare(right));
}

export function filterStandings(standings: StandingRow[], filters: StandingsFilters): StandingRow[] {
  const normalizedPlayerQuery = filters.playerQuery.trim().toLowerCase();

  return standings.filter((row) => {
    if (filters.teamName !== ALL_TEAMS_FILTER && row.teamName !== filters.teamName) {
      return false;
    }

    if (normalizedPlayerQuery === "") {
      return true;
    }

    return row.playerName.toLowerCase().includes(normalizedPlayerQuery);
  });
}

