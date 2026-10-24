import type { StandingRow } from "../api/types";

export const ALL_TEAMS_FILTER = "ALL";
export const STANDINGS_SORT_RANK = "RANK";
export const STANDINGS_SORT_POINTS_DESC = "POINTS_DESC";

export type StandingsSort = typeof STANDINGS_SORT_RANK | typeof STANDINGS_SORT_POINTS_DESC;

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

export function sortStandings(standings: StandingRow[], sort: StandingsSort): StandingRow[] {
  const sorted = [...standings];

  if (sort === STANDINGS_SORT_POINTS_DESC) {
    return sorted.sort((left, right) => {
      if (left.totalPoints !== right.totalPoints) {
        return right.totalPoints - left.totalPoints;
      }

      return left.rank - right.rank;
    });
  }

  return sorted.sort((left, right) => left.rank - right.rank);
}

