import type { RiderSummary } from "../api/types";

export interface RiderFilters {
  searchQuery: string;
  nationality: string;
  includeWithdrawn: boolean;
}

export const RIDER_SORT_NAME = "NAME";
export const RIDER_SORT_VALUE_DESC = "VALUE_DESC";

export type RiderSort = typeof RIDER_SORT_NAME | typeof RIDER_SORT_VALUE_DESC;

export const DEFAULT_RIDER_FILTERS: RiderFilters = {
  searchQuery: "",
  nationality: "ALL",
  includeWithdrawn: true
};

export function formatRiderValue(value: number): string {
  return `${value} pts`;
}

export function getNationalityOptions(riders: RiderSummary[]): string[] {
  const options = new Set<string>();

  riders.forEach((rider) => {
    if (rider.nationality.trim() !== "") {
      options.add(rider.nationality);
    }
  });

  return Array.from(options).sort((left, right) => left.localeCompare(right));
}

export function filterRiders(riders: RiderSummary[], filters: RiderFilters): RiderSummary[] {
  const normalizedQuery = filters.searchQuery.trim().toLowerCase();

  return riders.filter((rider) => {
    if (!filters.includeWithdrawn && rider.withdrawn) {
      return false;
    }

    if (filters.nationality !== "ALL" && rider.nationality !== filters.nationality) {
      return false;
    }

    if (normalizedQuery === "") {
      return true;
    }

    return rider.name.toLowerCase().includes(normalizedQuery);
  });
}

export function sortRiders(riders: RiderSummary[], sort: RiderSort): RiderSummary[] {
  const sorted = [...riders];

  if (sort === RIDER_SORT_VALUE_DESC) {
    return sorted.sort((left, right) => {
      if (left.value !== right.value) {
        return right.value - left.value;
      }

      return left.name.localeCompare(right.name);
    });
  }

  return sorted.sort((left, right) => left.name.localeCompare(right.name));
}

