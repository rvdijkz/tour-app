import type { PersistedDashboardState } from "./dashboardStatePersistence";

const PARAM_VIEW = "view";
const PARAM_STANDINGS_PLAYER_QUERY = "spq";
const PARAM_STANDINGS_TEAM_FILTER = "steam";
const PARAM_RIDER_SEARCH_QUERY = "rrq";
const PARAM_RIDER_NATIONALITY_FILTER = "rnat";
const PARAM_INCLUDE_WITHDRAWN = "rwith";
const PARAM_DETAIL_STAGE_FILTER = "dstage";
const PARAM_RIDER_PREVIEW_STAGE_FILTER = "rpstage";

const DASHBOARD_QUERY_KEYS = [
  PARAM_VIEW,
  PARAM_STANDINGS_PLAYER_QUERY,
  PARAM_STANDINGS_TEAM_FILTER,
  PARAM_RIDER_SEARCH_QUERY,
  PARAM_RIDER_NATIONALITY_FILTER,
  PARAM_INCLUDE_WITHDRAWN,
  PARAM_DETAIL_STAGE_FILTER,
  PARAM_RIDER_PREVIEW_STAGE_FILTER
] as const;

function parseNumber(value: string | null): number | null {
  if (value === null || value.trim() === "") {
    return null;
  }

  const parsed = Number(value);
  if (!Number.isFinite(parsed) || !Number.isInteger(parsed)) {
    return null;
  }

  return parsed;
}

function parseView(value: string | null): PersistedDashboardState["activeView"] | null {
  if (value === "standings" || value === "riders") {
    return value;
  }

  return null;
}

function parseStageFilter(value: string | null): PersistedDashboardState["detailStageFilter"] | null {
  if (value === null || value === "") {
    return null;
  }

  if (value === "ALL") {
    return "ALL";
  }

  return parseNumber(value);
}

function parseRiderPreviewStageFilter(value: string | null): PersistedDashboardState["riderPreviewStageFilter"] | null {
  if (value === null || value === "") {
    return null;
  }

  if (value === "ALL") {
    return "ALL";
  }

  return parseNumber(value);
}

function parseBooleanFlag(value: string | null): boolean | null {
  if (value === "1") {
    return true;
  }
  if (value === "0") {
    return false;
  }

  return null;
}

export function loadDashboardUrlState(
  search: string
): Partial<PersistedDashboardState> | null {
  const params = new URLSearchParams(search);
  const result: Partial<PersistedDashboardState> = {};

  const parsedView = parseView(params.get(PARAM_VIEW));
  if (parsedView !== null) {
    result.activeView = parsedView;
  }

  const standingsPlayerQuery = params.get(PARAM_STANDINGS_PLAYER_QUERY);
  if (standingsPlayerQuery !== null) {
    result.standingsPlayerQuery = standingsPlayerQuery;
  }

  const standingsTeamFilter = params.get(PARAM_STANDINGS_TEAM_FILTER);
  if (standingsTeamFilter !== null) {
    result.standingsTeamFilter = standingsTeamFilter;
  }

  const riderSearchQuery = params.get(PARAM_RIDER_SEARCH_QUERY);
  if (riderSearchQuery !== null) {
    result.riderSearchQuery = riderSearchQuery;
  }

  const riderNationalityFilter = params.get(PARAM_RIDER_NATIONALITY_FILTER);
  if (riderNationalityFilter !== null) {
    result.riderNationalityFilter = riderNationalityFilter;
  }

  const includeWithdrawnRiders = parseBooleanFlag(params.get(PARAM_INCLUDE_WITHDRAWN));
  if (includeWithdrawnRiders !== null) {
    result.includeWithdrawnRiders = includeWithdrawnRiders;
  }

  const detailStageFilter = parseStageFilter(params.get(PARAM_DETAIL_STAGE_FILTER));
  if (detailStageFilter !== null) {
    result.detailStageFilter = detailStageFilter;
  }

  const riderPreviewStageFilter = parseRiderPreviewStageFilter(params.get(PARAM_RIDER_PREVIEW_STAGE_FILTER));
  if (riderPreviewStageFilter !== null) {
    result.riderPreviewStageFilter = riderPreviewStageFilter;
  }

  return Object.keys(result).length > 0 ? result : null;
}

export function buildDashboardUrlSearch(
  currentSearch: string,
  state: Pick<
    PersistedDashboardState,
    | "activeView"
    | "standingsPlayerQuery"
    | "standingsTeamFilter"
    | "riderSearchQuery"
    | "riderNationalityFilter"
    | "includeWithdrawnRiders"
    | "detailStageFilter"
    | "riderPreviewStageFilter"
  >
): string {
  const params = new URLSearchParams(currentSearch);

  DASHBOARD_QUERY_KEYS.forEach((key) => {
    params.delete(key);
  });

  params.set(PARAM_VIEW, state.activeView);

  if (state.standingsPlayerQuery.trim() !== "") {
    params.set(PARAM_STANDINGS_PLAYER_QUERY, state.standingsPlayerQuery);
  }

  if (state.standingsTeamFilter.trim() !== "") {
    params.set(PARAM_STANDINGS_TEAM_FILTER, state.standingsTeamFilter);
  }

  if (state.riderSearchQuery.trim() !== "") {
    params.set(PARAM_RIDER_SEARCH_QUERY, state.riderSearchQuery);
  }

  if (state.riderNationalityFilter.trim() !== "") {
    params.set(PARAM_RIDER_NATIONALITY_FILTER, state.riderNationalityFilter);
  }

  params.set(PARAM_INCLUDE_WITHDRAWN, state.includeWithdrawnRiders ? "1" : "0");

  if (state.detailStageFilter !== "ALL") {
    params.set(PARAM_DETAIL_STAGE_FILTER, String(state.detailStageFilter));
  }

  if (state.riderPreviewStageFilter !== "ALL") {
    params.set(PARAM_RIDER_PREVIEW_STAGE_FILTER, String(state.riderPreviewStageFilter));
  }

  const serialized = params.toString();
  return serialized === "" ? "" : `?${serialized}`;
}

