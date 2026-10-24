import type { RiderPreviewStageFilter } from "./riderDetailPresentation";
import type { StageHistoryFilter } from "./detailPresentation";
import {
  STANDINGS_SORT_POINTS_DESC,
  STANDINGS_SORT_RANK,
  type StandingsSort
} from "./standingsPresentation";
import {
  RIDER_SORT_NAME,
  RIDER_SORT_VALUE_DESC,
  type RiderSort
} from "./riderPresentation";

export const DASHBOARD_STATE_STORAGE_KEY = "tour.dashboard.state.v1";

export type PersistedAppView = "standings" | "riders" | "admin";

export interface PersistedDashboardState {
  activeView: PersistedAppView;
  standingsPlayerQuery: string;
  standingsTeamFilter: string;
  standingsSort: StandingsSort;
  riderSearchQuery: string;
  riderNationalityFilter: string;
  includeWithdrawnRiders: boolean;
  riderSort: RiderSort;
  detailStageFilter: StageHistoryFilter;
  riderPreviewStageFilter: RiderPreviewStageFilter;
}

interface StorageReader {
  getItem(key: string): string | null;
}

interface StorageWriter {
  setItem(key: string, value: string): void;
}

function isObject(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null;
}

function isPersistedAppView(value: unknown): value is PersistedAppView {
  return value === "standings" || value === "riders" || value === "admin";
}

function isStageFilter(value: unknown): value is StageHistoryFilter {
  return value === "ALL" || typeof value === "number";
}

function isRiderPreviewStageFilter(value: unknown): value is RiderPreviewStageFilter {
  return value === "ALL" || typeof value === "number";
}

function isStandingsSort(value: unknown): value is StandingsSort {
  return value === STANDINGS_SORT_RANK || value === STANDINGS_SORT_POINTS_DESC;
}

function isRiderSort(value: unknown): value is RiderSort {
  return value === RIDER_SORT_NAME || value === RIDER_SORT_VALUE_DESC;
}

function sanitizePersistedState(value: unknown): PersistedDashboardState | null {
  if (!isObject(value)) {
    return null;
  }

  const {
    activeView,
    standingsPlayerQuery,
    standingsTeamFilter,
    standingsSort,
    riderSearchQuery,
    riderNationalityFilter,
    includeWithdrawnRiders,
    riderSort,
    detailStageFilter,
    riderPreviewStageFilter
  } = value;

  if (!isPersistedAppView(activeView)) {
    return null;
  }

  if (typeof standingsPlayerQuery !== "string" || typeof standingsTeamFilter !== "string") {
    return null;
  }

  if (standingsSort !== undefined && !isStandingsSort(standingsSort)) {
    return null;
  }

  if (typeof riderSearchQuery !== "string" || typeof riderNationalityFilter !== "string") {
    return null;
  }

  if (typeof includeWithdrawnRiders !== "boolean") {
    return null;
  }

  if (riderSort !== undefined && !isRiderSort(riderSort)) {
    return null;
  }

  if (!isStageFilter(detailStageFilter) || !isRiderPreviewStageFilter(riderPreviewStageFilter)) {
    return null;
  }

  return {
    activeView,
    standingsPlayerQuery,
    standingsTeamFilter,
    standingsSort: standingsSort ?? STANDINGS_SORT_RANK,
    riderSearchQuery,
    riderNationalityFilter,
    includeWithdrawnRiders,
    riderSort: riderSort ?? RIDER_SORT_NAME,
    detailStageFilter,
    riderPreviewStageFilter
  };
}

export function loadDashboardState(storage: StorageReader): PersistedDashboardState | null {
  try {
    const raw = storage.getItem(DASHBOARD_STATE_STORAGE_KEY);
    if (raw === null) {
      return null;
    }

    return sanitizePersistedState(JSON.parse(raw));
  } catch {
    return null;
  }
}

export function saveDashboardState(storage: StorageWriter, state: PersistedDashboardState): void {
  storage.setItem(DASHBOARD_STATE_STORAGE_KEY, JSON.stringify(state));
}

