import type { RiderPreviewStageFilter } from "./riderDetailPresentation";
import type { StageHistoryFilter } from "./detailPresentation";

export const DASHBOARD_STATE_STORAGE_KEY = "tour.dashboard.state.v1";

export type PersistedAppView = "standings" | "riders";

export interface PersistedDashboardState {
  activeView: PersistedAppView;
  standingsPlayerQuery: string;
  standingsTeamFilter: string;
  riderSearchQuery: string;
  riderNationalityFilter: string;
  includeWithdrawnRiders: boolean;
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
  return value === "standings" || value === "riders";
}

function isStageFilter(value: unknown): value is StageHistoryFilter {
  return value === "ALL" || typeof value === "number";
}

function isRiderPreviewStageFilter(value: unknown): value is RiderPreviewStageFilter {
  return value === "ALL" || typeof value === "number";
}

function sanitizePersistedState(value: unknown): PersistedDashboardState | null {
  if (!isObject(value)) {
    return null;
  }

  const {
    activeView,
    standingsPlayerQuery,
    standingsTeamFilter,
    riderSearchQuery,
    riderNationalityFilter,
    includeWithdrawnRiders,
    detailStageFilter,
    riderPreviewStageFilter
  } = value;

  if (!isPersistedAppView(activeView)) {
    return null;
  }

  if (typeof standingsPlayerQuery !== "string" || typeof standingsTeamFilter !== "string") {
    return null;
  }

  if (typeof riderSearchQuery !== "string" || typeof riderNationalityFilter !== "string") {
    return null;
  }

  if (typeof includeWithdrawnRiders !== "boolean") {
    return null;
  }

  if (!isStageFilter(detailStageFilter) || !isRiderPreviewStageFilter(riderPreviewStageFilter)) {
    return null;
  }

  return {
    activeView,
    standingsPlayerQuery,
    standingsTeamFilter,
    riderSearchQuery,
    riderNationalityFilter,
    includeWithdrawnRiders,
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

