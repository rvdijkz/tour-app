import { useEffect, useMemo, useState } from "react";
import { environment } from "./config/env";
import { FetchHttpClient } from "./api/httpClient";
import { TokenProvider } from "./api/tokenProvider";
import { TourApiClient } from "./api/tourApiClient";
import type { EditionSummary, PlayerStandingDetail, RiderSummary, StandingRow } from "./api/types";
import {
  ALL_STAGES_FILTER,
  filterStageHistory,
  formatScoreContribution,
  getStageHistoryFilterOptions,
  getHiddenScoreContributionCount,
  getVisibleScoreContributions,
  type StageHistoryFilter
} from "./ui/detailPresentation";
import {
  ALL_TEAMS_FILTER,
  filterStandings,
  getTeamOptions,
  sortStandings,
  STANDINGS_SORT_POINTS_DESC,
  STANDINGS_SORT_RANK,
  type StandingsSort
} from "./ui/standingsPresentation";
import {
  ALL_RIDER_PREVIEW_STAGES,
  buildRiderStageScorePreview,
  filterRiderPreviewByStage,
  getRiderPreviewStageOptions,
  type RiderPreviewStageFilter
} from "./ui/riderDetailPresentation";
import {
  loadDashboardState,
  saveDashboardState
} from "./ui/dashboardStatePersistence";
import { buildDashboardUrlSearch, loadDashboardUrlState } from "./ui/dashboardUrlState";
import {
  filterRiders,
  formatRiderValue,
  getNationalityOptions,
  RIDER_SORT_NAME,
  RIDER_SORT_VALUE_DESC,
  sortRiders,
  type RiderSort
} from "./ui/riderPresentation";

type AppView = "standings" | "riders" | "admin";
type AdminAction = "createEdition" | "entryDeadline" | "reserveBudget" | "requiredNationality" | "doublePointsStages";
type AdminFeedback = {
  action: AdminAction;
  message: string;
  kind: "info" | "error";
};

const VIEW_STANDINGS: AppView = "standings";
const VIEW_RIDERS: AppView = "riders";
const VIEW_ADMIN: AppView = "admin";
const ALL_NATIONALITIES = "ALL";
const RIDER_PREVIEW_PLAYER_LIMIT = 8;

function formatEditionLabel(year: number, stage: number): string {
  return `Edition ${year} - Stage ${stage}`;
}

function toDateTimeLocalValue(isoDateTime: string): string {
  const parsed = new Date(isoDateTime);
  if (Number.isNaN(parsed.getTime())) {
    return "";
  }

  const timezoneOffsetMs = parsed.getTimezoneOffset() * 60 * 1000;
  const localDate = new Date(parsed.getTime() - timezoneOffsetMs);
  return localDate.toISOString().slice(0, 16);
}

function formatDateTimeForDisplay(isoDateTime: string): string {
  const parsed = new Date(isoDateTime);
  if (Number.isNaN(parsed.getTime())) {
    return isoDateTime;
  }

  return parsed.toLocaleString();
}

function normalizeStageList(value: string): string {
  const parsedStages = Array.from(
    new Set(
      value
        .split(",")
        .map((part) => Number(part.trim()))
        .filter((stage) => Number.isInteger(stage))
    )
  ).sort((left, right) => left - right);

  return parsedStages.join(",");
}

/**
 * Minimal app shell proving API layer usage stays outside UI components.
 */
export default function App() {
  const [activeView, setActiveView] = useState<AppView>(VIEW_STANDINGS);
  const [editionLabel, setEditionLabel] = useState<string>("No edition loaded");
  const [activeEditionId, setActiveEditionId] = useState<number | null>(null);
  const [standings, setStandings] = useState<StandingRow[]>([]);
  const [riders, setRiders] = useState<RiderSummary[]>([]);
  const [selectedPlayerDetail, setSelectedPlayerDetail] = useState<PlayerStandingDetail | null>(null);
  const [riderSearchQuery, setRiderSearchQuery] = useState<string>("");
  const [riderNationalityFilter, setRiderNationalityFilter] = useState<string>(ALL_NATIONALITIES);
  const [includeWithdrawnRiders, setIncludeWithdrawnRiders] = useState<boolean>(true);
  const [standingsPlayerQuery, setStandingsPlayerQuery] = useState<string>("");
  const [standingsTeamFilter, setStandingsTeamFilter] = useState<string>(ALL_TEAMS_FILTER);
  const [standingsSort, setStandingsSort] = useState<StandingsSort>(STANDINGS_SORT_RANK);
  const [errorMessage, setErrorMessage] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isLoadingDetail, setIsLoadingDetail] = useState<boolean>(false);
  const [detailStageFilter, setDetailStageFilter] = useState<StageHistoryFilter>(ALL_STAGES_FILTER);
  const [selectedRider, setSelectedRider] = useState<RiderSummary | null>(null);
  const [selectedRiderScorePreview, setSelectedRiderScorePreview] = useState<ReturnType<typeof buildRiderStageScorePreview>>(
    []
  );
  const [isLoadingRiderPreview, setIsLoadingRiderPreview] = useState<boolean>(false);
  const [riderPreviewErrorMessage, setRiderPreviewErrorMessage] = useState<string>("");
  const [riderPreviewStageFilter, setRiderPreviewStageFilter] = useState<RiderPreviewStageFilter>(ALL_RIDER_PREVIEW_STAGES);
  const [riderSort, setRiderSort] = useState<RiderSort>(RIDER_SORT_NAME);
  const [isDashboardStateHydrated, setIsDashboardStateHydrated] = useState<boolean>(false);
  const [shareStateMessage, setShareStateMessage] = useState<string>("");
  const [adminEditionYear, setAdminEditionYear] = useState<string>("2027");
  const [adminEditionIdInput, setAdminEditionIdInput] = useState<string>("");
  const [adminEntryDeadline, setAdminEntryDeadline] = useState<string>("");
  const [adminReserveBudget, setAdminReserveBudget] = useState<string>("20");
  const [adminRequiredNationality, setAdminRequiredNationality] = useState<string>("NLD");
  const [adminDoublePointsStages, setAdminDoublePointsStages] = useState<string>("5,12");
  const [adminFeedback, setAdminFeedback] = useState<AdminFeedback | null>(null);
  const [isLoadingAdmin, setIsLoadingAdmin] = useState<boolean>(false);
  const [activeAdminAction, setActiveAdminAction] = useState<AdminAction | null>(null);
  const [adminContext, setAdminContext] = useState<EditionSummary | null>(null);

  const apiClient = useMemo(() => {
    const tokenProvider = new TokenProvider(() => null);
    const httpClient = new FetchHttpClient({
      baseUrl: environment.apiBaseUrl,
      tokenProvider
    });

    return new TourApiClient(httpClient);
  }, []);

  const nationalityOptions = useMemo(() => getNationalityOptions(riders), [riders]);
  const standingsTeamOptions = useMemo(() => getTeamOptions(standings), [standings]);

  const filteredRiders = useMemo(
    () =>
      filterRiders(riders, {
        searchQuery: riderSearchQuery,
        nationality: riderNationalityFilter,
        includeWithdrawn: includeWithdrawnRiders
      }),
    [includeWithdrawnRiders, riderNationalityFilter, riderSearchQuery, riders]
  );

  const sortedRiders = useMemo(() => sortRiders(filteredRiders, riderSort), [filteredRiders, riderSort]);

  const filteredStandings = useMemo(
    () =>
      filterStandings(standings, {
        playerQuery: standingsPlayerQuery,
        teamName: standingsTeamFilter
      }),
    [standings, standingsPlayerQuery, standingsTeamFilter]
  );

  const sortedStandings = useMemo(
    () => sortStandings(filteredStandings, standingsSort),
    [filteredStandings, standingsSort]
  );

  const detailStageFilterOptions = useMemo(
    () => getStageHistoryFilterOptions(selectedPlayerDetail?.stageHistory ?? []),
    [selectedPlayerDetail]
  );

  const filteredStageHistory = useMemo(
    () => filterStageHistory(selectedPlayerDetail?.stageHistory ?? [], detailStageFilter),
    [detailStageFilter, selectedPlayerDetail]
  );

  const riderPreviewStageOptions = useMemo(
    () => getRiderPreviewStageOptions(selectedRiderScorePreview),
    [selectedRiderScorePreview]
  );

  const filteredRiderPreviewRows = useMemo(
    () => filterRiderPreviewByStage(selectedRiderScorePreview, riderPreviewStageFilter),
    [riderPreviewStageFilter, selectedRiderScorePreview]
  );

  const hasActiveStandingsFilters =
    standingsPlayerQuery.trim() !== "" ||
    standingsTeamFilter !== ALL_TEAMS_FILTER ||
    standingsSort !== STANDINGS_SORT_RANK;
  const hasActiveRiderFilters =
    riderSearchQuery.trim() !== "" ||
    riderNationalityFilter !== ALL_NATIONALITIES ||
    !includeWithdrawnRiders ||
    riderSort !== RIDER_SORT_NAME;
  const hasActiveRiderPreviewStageFilter = riderPreviewStageFilter !== ALL_RIDER_PREVIEW_STAGES;
  const hasActiveDetailStageFilter = detailStageFilter !== ALL_STAGES_FILTER;
  const hasAnyActiveDashboardFilters =
    hasActiveStandingsFilters ||
    hasActiveRiderFilters ||
    hasActiveRiderPreviewStageFilter ||
    hasActiveDetailStageFilter;

  const adminContextEditionId = adminContext !== null ? String(adminContext.editionId) : "";
  const adminContextEntryDeadline = adminContext !== null ? toDateTimeLocalValue(adminContext.entryDeadline) : "";
  const adminContextReserveBudget = adminContext !== null ? String(adminContext.reserveBudget) : "";
  const adminContextRequiredNationality = adminContext !== null ? adminContext.requiredNationality : "";
  const adminContextDoublePointsStages = adminContext !== null ? adminContext.doublePointsStages.join(",") : "";

  const isTargetEditionDirty = adminContext !== null && adminEditionIdInput !== adminContextEditionId;
  const isEntryDeadlineDirty = adminContext !== null && adminEntryDeadline !== adminContextEntryDeadline;
  const isReserveBudgetDirty = adminContext !== null && adminReserveBudget !== adminContextReserveBudget;
  const isRequiredNationalityDirty =
    adminContext !== null && adminRequiredNationality.trim().toUpperCase() !== adminContextRequiredNationality;
  const isDoublePointsStagesDirty =
    adminContext !== null && normalizeStageList(adminDoublePointsStages) !== normalizeStageList(adminContextDoublePointsStages);
  const canSaveEntryDeadline = adminContext === null || isEntryDeadlineDirty;
  const canSaveReserveBudget = adminContext === null || isReserveBudgetDirty;
  const canSaveRequiredNationality = adminContext === null || isRequiredNationalityDirty;
  const canSaveDoublePointsStages = adminContext === null || isDoublePointsStagesDirty;

  const getSaveHint = (canSave: boolean): string => {
    if (adminContext === null || isLoadingAdmin || canSave) {
      return "";
    }

    return "No changes to save.";
  };

  const resetStandingsFilters = () => {
    setStandingsPlayerQuery("");
    setStandingsTeamFilter(ALL_TEAMS_FILTER);
    setStandingsSort(STANDINGS_SORT_RANK);
  };

  const resetRiderFilters = () => {
    setRiderSearchQuery("");
    setRiderNationalityFilter(ALL_NATIONALITIES);
    setIncludeWithdrawnRiders(true);
    setRiderSort(RIDER_SORT_NAME);
  };

  const resetDashboardFilters = () => {
    resetStandingsFilters();
    resetRiderFilters();
    setDetailStageFilter(ALL_STAGES_FILTER);
    setRiderPreviewStageFilter(ALL_RIDER_PREVIEW_STAGES);
  };

  const copyShareLink = async () => {
    if (typeof window === "undefined") {
      return;
    }

    const urlToCopy = window.location.href;

    try {
      await navigator.clipboard.writeText(urlToCopy);
      setShareStateMessage("Share link copied.");
    } catch {
      try {
        const fallbackInput = document.createElement("textarea");
        fallbackInput.value = urlToCopy;
        fallbackInput.setAttribute("readonly", "");
        fallbackInput.style.position = "fixed";
        fallbackInput.style.opacity = "0";
        fallbackInput.style.pointerEvents = "none";
        document.body.appendChild(fallbackInput);
        fallbackInput.focus();
        fallbackInput.select();

        const copied = document.execCommand("copy");
        setShareStateMessage(copied ? "Share link copied." : "Failed to copy share link.");
        document.body.removeChild(fallbackInput);
      } catch {
        setShareStateMessage("Failed to copy share link.");
      }
    }

    window.setTimeout(() => {
      setShareStateMessage("");
    }, 2500);
  };

  useEffect(() => {
    if (adminFeedback === null || adminFeedback.kind !== "info") {
      return;
    }

    const { action, message } = adminFeedback;
    const timeoutId = window.setTimeout(() => {
      setAdminFeedback((previous) =>
        previous !== null && previous.kind === "info" && previous.action === action && previous.message === message
          ? null
          : previous
      );
    }, 3000);

    return () => {
      window.clearTimeout(timeoutId);
    };
  }, [adminFeedback]);

  useEffect(() => {
    if (typeof window === "undefined") {
      return;
    }

    const savedState = loadDashboardState(window.localStorage) ?? undefined;
    const urlState = loadDashboardUrlState(window.location.search) ?? undefined;
    const mergedState = {
      ...savedState,
      ...urlState
    };

    if (mergedState.activeView !== undefined) {
      setActiveView(mergedState.activeView);
    }
    if (mergedState.standingsPlayerQuery !== undefined) {
      setStandingsPlayerQuery(mergedState.standingsPlayerQuery);
    }
    if (mergedState.standingsTeamFilter !== undefined) {
      setStandingsTeamFilter(mergedState.standingsTeamFilter);
    }
    if (mergedState.standingsSort !== undefined) {
      setStandingsSort(mergedState.standingsSort);
    }
    if (mergedState.riderSearchQuery !== undefined) {
      setRiderSearchQuery(mergedState.riderSearchQuery);
    }
    if (mergedState.riderNationalityFilter !== undefined) {
      setRiderNationalityFilter(mergedState.riderNationalityFilter);
    }
    if (mergedState.includeWithdrawnRiders !== undefined) {
      setIncludeWithdrawnRiders(mergedState.includeWithdrawnRiders);
    }
    if (mergedState.riderSort !== undefined) {
      setRiderSort(mergedState.riderSort);
    }
    if (mergedState.detailStageFilter !== undefined) {
      setDetailStageFilter(mergedState.detailStageFilter);
    }
    if (mergedState.riderPreviewStageFilter !== undefined) {
      setRiderPreviewStageFilter(mergedState.riderPreviewStageFilter);
    }

    setIsDashboardStateHydrated(true);
  }, []);

  useEffect(() => {
    if (!isDashboardStateHydrated || typeof window === "undefined") {
      return;
    }

    const dashboardState = {
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
    };

    saveDashboardState(window.localStorage, dashboardState);

    const nextSearch = buildDashboardUrlSearch(window.location.search, dashboardState);
    const nextUrl = `${window.location.pathname}${nextSearch}${window.location.hash}`;
    const currentUrl = `${window.location.pathname}${window.location.search}${window.location.hash}`;
    if (nextUrl !== currentUrl) {
      window.history.replaceState(null, "", nextUrl);
    }
  }, [
    activeView,
    detailStageFilter,
    includeWithdrawnRiders,
    isDashboardStateHydrated,
    riderNationalityFilter,
    riderSort,
    riderPreviewStageFilter,
    riderSearchQuery,
    standingsPlayerQuery,
    standingsSort,
    standingsTeamFilter
  ]);

  const loadCurrentEditionData = async (view: AppView) => {
    setIsLoading(true);
    setErrorMessage("");
    setActiveView(view);

    try {
      const edition = await apiClient.getCurrentEdition();
      setActiveEditionId(edition.editionId);
      setAdminContext(edition);

      if (view === VIEW_STANDINGS) {
        const standingsResponse = await apiClient.getStandings(edition.editionId);
        setEditionLabel(formatEditionLabel(edition.year, standingsResponse.stage));
        setStandings(standingsResponse.standings);
        setStandingsPlayerQuery("");
        setStandingsTeamFilter(ALL_TEAMS_FILTER);
        setStandingsSort(STANDINGS_SORT_RANK);
      } else if (view === VIEW_RIDERS) {
        const riderResponse = await apiClient.listRiders(edition.editionId);
        setEditionLabel(formatEditionLabel(edition.year, edition.currentStage));
        setRiders(riderResponse.riders);
        setRiderSort(RIDER_SORT_NAME);
      } else {
        setEditionLabel(formatEditionLabel(edition.year, edition.currentStage));
        setAdminEditionIdInput(String(edition.editionId));
        setAdminEntryDeadline(toDateTimeLocalValue(edition.entryDeadline));
        setAdminReserveBudget(String(edition.reserveBudget));
        setAdminRequiredNationality(edition.requiredNationality);
        setAdminDoublePointsStages(edition.doublePointsStages.join(","));
      }

      if (view === VIEW_RIDERS) {
        setSelectedPlayerDetail(null);
        setDetailStageFilter(ALL_STAGES_FILTER);
      } else if (view === VIEW_STANDINGS) {
        setSelectedRider(null);
        setSelectedRiderScorePreview([]);
        setRiderPreviewErrorMessage("");
        setRiderPreviewStageFilter(ALL_RIDER_PREVIEW_STAGES);
      } else {
        setSelectedPlayerDetail(null);
        setSelectedRider(null);
        setSelectedRiderScorePreview([]);
        setRiderPreviewErrorMessage("");
        setRiderPreviewStageFilter(ALL_RIDER_PREVIEW_STAGES);
      }
    } catch (error) {
      const nextErrorMessage = error instanceof Error ? error.message : "Unknown error";
      setEditionLabel("No edition loaded");
      setActiveEditionId(null);
      setStandings([]);
      setRiders([]);
      setSelectedPlayerDetail(null);
      setDetailStageFilter(ALL_STAGES_FILTER);
      setSelectedRider(null);
      setSelectedRiderScorePreview([]);
      setRiderPreviewErrorMessage("");
      setRiderPreviewStageFilter(ALL_RIDER_PREVIEW_STAGES);
      setAdminContext(null);
      setErrorMessage(`Failed to load ${view}: ${nextErrorMessage}`);
    } finally {
      setIsLoading(false);
    }
  };

  const loadPlayerStandingDetail = async (player: StandingRow) => {
    if (activeEditionId === null) {
      return;
    }

    setIsLoadingDetail(true);
    setErrorMessage("");

    try {
      const detail = await apiClient.getPlayerStandingDetail(activeEditionId, player.playerId);
      setSelectedPlayerDetail(detail);
      setDetailStageFilter(ALL_STAGES_FILTER);
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : "Unknown error";
      setSelectedPlayerDetail(null);
      setErrorMessage(`Failed to load player detail: ${errorMessage}`);
    } finally {
      setIsLoadingDetail(false);
    }
  };

  const loadRiderScorePreview = async (rider: RiderSummary, mode: "toggle" | "open" = "toggle") => {
    if (activeEditionId === null) {
      return;
    }

    if (mode === "toggle" && selectedRider?.riderId === rider.riderId) {
      setSelectedRider(null);
      setSelectedRiderScorePreview([]);
      setRiderPreviewErrorMessage("");
      setRiderPreviewStageFilter(ALL_RIDER_PREVIEW_STAGES);
      return;
    }

    setIsLoadingRiderPreview(true);
    setRiderPreviewErrorMessage("");
    setRiderPreviewStageFilter(ALL_RIDER_PREVIEW_STAGES);
    setSelectedRider(rider);

    try {
      const standingsResponse = await apiClient.getStandings(activeEditionId);
      const sampledPlayers = standingsResponse.standings.slice(0, RIDER_PREVIEW_PLAYER_LIMIT);
      const playerDetails = await Promise.all(
        sampledPlayers.map((standing) => apiClient.getPlayerStandingDetail(activeEditionId, standing.playerId))
      );

      setSelectedRiderScorePreview(buildRiderStageScorePreview(playerDetails, rider.riderId));
    } catch (error) {
      const nextErrorMessage = error instanceof Error ? error.message : "Unknown error";
      setSelectedRiderScorePreview([]);
      setRiderPreviewErrorMessage(`Failed to load rider preview: ${nextErrorMessage}`);
    } finally {
      setIsLoadingRiderPreview(false);
    }
  };

  const openPlayerDetailFromRiderPreview = async (playerId: number) => {
    if (activeEditionId === null) {
      return;
    }

    setErrorMessage("");
    setActiveView(VIEW_STANDINGS);

    let nextStandings = standings;
    if (nextStandings.length === 0) {
      setIsLoading(true);
      try {
        const standingsResponse = await apiClient.getStandings(activeEditionId);
        setStandings(standingsResponse.standings);
        nextStandings = standingsResponse.standings;
      } catch (error) {
        const nextErrorMessage = error instanceof Error ? error.message : "Unknown error";
        setErrorMessage(`Failed to load standings for player detail: ${nextErrorMessage}`);
        return;
      } finally {
        setIsLoading(false);
      }
    }

    const selectedStanding = nextStandings.find((standing) => standing.playerId === playerId);
    if (selectedStanding === undefined) {
      setErrorMessage("Player is no longer present in the current published standings.");
      return;
    }

    await loadPlayerStandingDetail(selectedStanding);
  };

  const openRiderFromPlayerDetail = async (riderId: number) => {
    if (activeEditionId === null) {
      return;
    }

    setErrorMessage("");
    setActiveView(VIEW_RIDERS);

    let nextRiders = riders;
    if (nextRiders.length === 0) {
      setIsLoading(true);
      try {
        const riderResponse = await apiClient.listRiders(activeEditionId);
        setRiders(riderResponse.riders);
        nextRiders = riderResponse.riders;
      } catch (error) {
        const nextErrorMessage = error instanceof Error ? error.message : "Unknown error";
        setErrorMessage(`Failed to load riders for detail jump: ${nextErrorMessage}`);
        return;
      } finally {
        setIsLoading(false);
      }
    }

    const selectedDetailRider = nextRiders.find((rider) => rider.riderId === riderId);
    if (selectedDetailRider === undefined) {
      setErrorMessage("Rider from player detail is not available in the current edition rider list.");
      return;
    }

    setRiderSearchQuery(selectedDetailRider.name);
    setRiderNationalityFilter(ALL_NATIONALITIES);
    setIncludeWithdrawnRiders(true);
    await loadRiderScorePreview(selectedDetailRider, "open");
  };

  const parseAdminEditionId = (action: AdminAction): number | null => {
    const parsed = Number(adminEditionIdInput);
    if (!Number.isInteger(parsed) || parsed <= 0) {
      setAdminFeedback({ action, message: "Enter a valid edition id.", kind: "error" });
      return null;
    }

    return parsed;
  };

  const createEdition = async () => {
    const parsedYear = Number(adminEditionYear);
    if (!Number.isInteger(parsedYear) || parsedYear < 2020) {
      setAdminFeedback({
        action: "createEdition",
        message: "Edition year must be an integer from 2020 onward.",
        kind: "error"
      });
      return;
    }

    setIsLoadingAdmin(true);
    setActiveAdminAction("createEdition");
    setAdminFeedback(null);
    try {
      const response = await apiClient.createEdition({ year: parsedYear });
      setAdminEditionIdInput(String(response.editionId));
      setAdminFeedback({
        action: "createEdition",
        message: `Edition ${response.editionId} created for year ${response.year}.`,
        kind: "info"
      });
    } catch (error) {
      const nextError = error instanceof Error ? error.message : "Unknown error";
      setAdminFeedback({ action: "createEdition", message: `Create edition failed: ${nextError}`, kind: "error" });
    } finally {
      setIsLoadingAdmin(false);
      setActiveAdminAction(null);
    }
  };

  const saveEditionDeadline = async () => {
    const editionId = parseAdminEditionId("entryDeadline");
    if (editionId === null) {
      return;
    }

    if (adminEntryDeadline.trim() === "") {
      setAdminFeedback({ action: "entryDeadline", message: "Entry deadline is required.", kind: "error" });
      return;
    }

    const asDate = new Date(adminEntryDeadline);
    if (Number.isNaN(asDate.getTime())) {
      setAdminFeedback({ action: "entryDeadline", message: "Entry deadline must be a valid date and time.", kind: "error" });
      return;
    }

    setIsLoadingAdmin(true);
    setActiveAdminAction("entryDeadline");
    setAdminFeedback(null);
    try {
      const response = await apiClient.updateEditionDeadline(editionId, { entryDeadline: asDate.toISOString() });
      setAdminContext((previous) =>
        previous !== null && previous.editionId === response.editionId
          ? { ...previous, entryDeadline: response.entryDeadline }
          : previous
      );
      setAdminFeedback({ action: "entryDeadline", message: `Deadline updated for edition ${response.editionId}.`, kind: "info" });
    } catch (error) {
      const nextError = error instanceof Error ? error.message : "Unknown error";
      setAdminFeedback({ action: "entryDeadline", message: `Update deadline failed: ${nextError}`, kind: "error" });
    } finally {
      setIsLoadingAdmin(false);
      setActiveAdminAction(null);
    }
  };

  const saveReserveBudget = async () => {
    const editionId = parseAdminEditionId("reserveBudget");
    if (editionId === null) {
      return;
    }

    const parsedBudget = Number(adminReserveBudget);
    if (!Number.isInteger(parsedBudget) || parsedBudget < 0) {
      setAdminFeedback({ action: "reserveBudget", message: "Reserve budget must be a non-negative integer.", kind: "error" });
      return;
    }

    setIsLoadingAdmin(true);
    setActiveAdminAction("reserveBudget");
    setAdminFeedback(null);
    try {
      const response = await apiClient.updateReserveBudget(editionId, { reserveBudget: parsedBudget });
      setAdminContext((previous) =>
        previous !== null && previous.editionId === response.editionId
          ? { ...previous, reserveBudget: response.reserveBudget }
          : previous
      );
      setAdminFeedback({
        action: "reserveBudget",
        message: `Reserve budget set to ${response.reserveBudget} for edition ${response.editionId}.`,
        kind: "info"
      });
    } catch (error) {
      const nextError = error instanceof Error ? error.message : "Unknown error";
      setAdminFeedback({ action: "reserveBudget", message: `Update reserve budget failed: ${nextError}`, kind: "error" });
    } finally {
      setIsLoadingAdmin(false);
      setActiveAdminAction(null);
    }
  };

  const saveRequiredNationality = async () => {
    const editionId = parseAdminEditionId("requiredNationality");
    if (editionId === null) {
      return;
    }

    const normalizedNationality = adminRequiredNationality.trim().toUpperCase();
    if (!/^[A-Z]{3}$/.test(normalizedNationality)) {
      setAdminFeedback({ action: "requiredNationality", message: "Required nationality must be a 3-letter code.", kind: "error" });
      return;
    }

    setIsLoadingAdmin(true);
    setActiveAdminAction("requiredNationality");
    setAdminFeedback(null);
    try {
      const response = await apiClient.updateRequiredNationality(editionId, { requiredNationality: normalizedNationality });
      setAdminContext((previous) =>
        previous !== null && previous.editionId === response.editionId
          ? { ...previous, requiredNationality: response.requiredNationality }
          : previous
      );
      setAdminRequiredNationality(response.requiredNationality);
      setAdminFeedback({
        action: "requiredNationality",
        message: `Required nationality set to ${response.requiredNationality} for edition ${response.editionId}.`,
        kind: "info"
      });
    } catch (error) {
      const nextError = error instanceof Error ? error.message : "Unknown error";
      setAdminFeedback({
        action: "requiredNationality",
        message: `Update required nationality failed: ${nextError}`,
        kind: "error"
      });
    } finally {
      setIsLoadingAdmin(false);
      setActiveAdminAction(null);
    }
  };

  const saveDoublePointsStages = async () => {
    const editionId = parseAdminEditionId("doublePointsStages");
    if (editionId === null) {
      return;
    }

    const parsedStages = Array.from(
      new Set(
        adminDoublePointsStages
          .split(",")
          .map((value) => Number(value.trim()))
          .filter((value) => Number.isInteger(value))
      )
    );

    if (parsedStages.length === 0 || parsedStages.some((stage) => stage < 1 || stage > 21)) {
      setAdminFeedback({
        action: "doublePointsStages",
        message: "Double points stages must contain one or more integers between 1 and 21.",
        kind: "error"
      });
      return;
    }

    const normalizedStages = parsedStages.sort((left, right) => left - right);

    setIsLoadingAdmin(true);
    setActiveAdminAction("doublePointsStages");
    setAdminFeedback(null);
    try {
      const response = await apiClient.updateDoublePointsStages(editionId, { doublePointsStages: normalizedStages });
      setAdminContext((previous) =>
        previous !== null && previous.editionId === response.editionId
          ? { ...previous, doublePointsStages: response.doublePointsStages }
          : previous
      );
      setAdminDoublePointsStages(response.doublePointsStages.join(","));
      setAdminFeedback({
        action: "doublePointsStages",
        message: `Double points stages updated for edition ${response.editionId}.`,
        kind: "info"
      });
    } catch (error) {
      const nextError = error instanceof Error ? error.message : "Unknown error";
      setAdminFeedback({
        action: "doublePointsStages",
        message: `Update double points stages failed: ${nextError}`,
        kind: "error"
      });
    } finally {
      setIsLoadingAdmin(false);
      setActiveAdminAction(null);
    }
  };

  const renderAdminFeedback = (action: AdminAction) => {
    if (adminFeedback === null || adminFeedback.action !== action) {
      return null;
    }

    const className = adminFeedback.kind === "error" ? "message message--error admin-card__message" : "message message--info admin-card__message";
    return <p className={className}>{adminFeedback.message}</p>;
  };

  return (
    <main className="app-shell">
      <section className="hero">
        <div className="hero__topline">
          <div>
            <h1>Tour Frontend</h1>
            <p className="hero__copy">
              Load standings and riders for the current edition, inspect stage history, and review rider-level score
              contributions.
            </p>
          </div>

          <div className="view-toggle" role="tablist" aria-label="Dashboard view selector">
            <button
              type="button"
              className={`view-toggle__button ${activeView === VIEW_STANDINGS ? "view-toggle__button--active" : ""}`}
              onClick={() => {
                void loadCurrentEditionData(VIEW_STANDINGS);
              }}
              disabled={isLoading}
              role="tab"
              aria-selected={activeView === VIEW_STANDINGS}
            >
              Standings
            </button>
            <button
              type="button"
              className={`view-toggle__button ${activeView === VIEW_RIDERS ? "view-toggle__button--active" : ""}`}
              onClick={() => {
                void loadCurrentEditionData(VIEW_RIDERS);
              }}
              disabled={isLoading}
              role="tab"
              aria-selected={activeView === VIEW_RIDERS}
            >
              Riders
            </button>
            <button
              type="button"
              className={`view-toggle__button ${activeView === VIEW_ADMIN ? "view-toggle__button--active" : ""}`}
              onClick={() => {
                void loadCurrentEditionData(VIEW_ADMIN);
              }}
              disabled={isLoading}
              role="tab"
              aria-selected={activeView === VIEW_ADMIN}
            >
              Admin
            </button>
          </div>

          <div className="toolbar">
            <button
              type="button"
              className="toolbar__button"
              onClick={() => {
                void loadCurrentEditionData(activeView);
              }}
              disabled={isLoading}
            >
              {isLoading
                ? "Loading..."
                : activeView === VIEW_STANDINGS
                  ? standings.length > 0
                    ? "Refresh Standings"
                    : "Load Current Standings"
                  : activeView === VIEW_RIDERS
                    ? riders.length > 0
                      ? "Refresh Riders"
                      : "Load Riders"
                    : activeEditionId !== null
                      ? "Refresh Admin Context"
                      : "Load Admin Context"}
            </button>
            {selectedPlayerDetail !== null && (
              <button
                type="button"
                className="toolbar__button toolbar__button--secondary"
                onClick={() => {
                  setSelectedPlayerDetail(null);
                }}
              >
                Clear detail
              </button>
            )}
            <button
              type="button"
              className="toolbar__button toolbar__button--secondary"
              onClick={() => {
                void copyShareLink();
              }}
            >
              Copy share link
            </button>
            {hasAnyActiveDashboardFilters && (
              <button
                type="button"
                className="toolbar__button toolbar__button--secondary"
                onClick={resetDashboardFilters}
              >
                Reset dashboard filters
              </button>
            )}
            {shareStateMessage !== "" && <span className="toolbar__hint">{shareStateMessage}</span>}
          </div>
        </div>

        <div className="status-card" aria-live="polite">
          <div>
            <p className="status-card__label">Current edition</p>
            <p className="status-card__value">{editionLabel}</p>
          </div>
          <div>
            <p className="status-card__label">Detail state</p>
            <p className="status-card__value">
              {isLoadingDetail
                ? "Loading player detail..."
                : selectedPlayerDetail !== null
                  ? `Showing ${selectedPlayerDetail.player.playerName}`
                  : "No player selected"}
            </p>
          </div>
        </div>
      </section>

      {errorMessage !== "" && <p className="message message--error">{errorMessage}</p>}

      {activeView === VIEW_STANDINGS && standings.length === 0 && errorMessage === "" && !isLoading && (
        <p className="message message--info">
          Click <strong>Load Current Standings</strong> to fetch the latest public API data.
        </p>
      )}

      {activeView === VIEW_RIDERS && riders.length === 0 && errorMessage === "" && !isLoading && (
        <p className="message message--info">
          Click <strong>Load Riders</strong> to fetch the current edition rider catalog.
        </p>
      )}

      {activeView === VIEW_ADMIN && activeEditionId === null && errorMessage === "" && !isLoading && (
        <p className="message message--info">
          Click <strong>Load Admin Context</strong> to resolve the current edition before applying admin updates.
        </p>
      )}

      {activeView === VIEW_STANDINGS && standings.length > 0 && (
        <section className="panel">
          <div className="panel__header">
            <div>
              <h2 className="panel__title">Standings</h2>
              <p className="panel__subtitle">Rank, player, team, and total points from the live API response.</p>
            </div>
            <div className="panel__chips">
              {hasActiveStandingsFilters && <span className="active-chip">Filters active</span>}
              <span className="score-chip">
                {filteredStandings.length} / {standings.length} players
              </span>
            </div>
          </div>

          <div className="standings-filters" aria-label="Standings filters">
            <label className="rider-filter">
              <span>Player</span>
              <input
                type="search"
                value={standingsPlayerQuery}
                onChange={(event) => {
                  setStandingsPlayerQuery(event.target.value);
                }}
                placeholder="Search player"
              />
            </label>

            <label className="rider-filter">
              <span>Team</span>
              <select
                value={standingsTeamFilter}
                onChange={(event) => {
                  setStandingsTeamFilter(event.target.value);
                }}
              >
                <option value={ALL_TEAMS_FILTER}>All</option>
                {standingsTeamOptions.map((teamName) => (
                  <option key={teamName} value={teamName}>
                    {teamName}
                  </option>
                ))}
              </select>
            </label>

            <label className="rider-filter">
              <span>Sort</span>
              <select
                value={standingsSort}
                onChange={(event) => {
                  setStandingsSort(event.target.value as StandingsSort);
                }}
              >
                <option value={STANDINGS_SORT_RANK}>Rank</option>
                <option value={STANDINGS_SORT_POINTS_DESC}>Points (high-low)</option>
              </select>
            </label>

            {hasActiveStandingsFilters && (
              <div className="filter-actions">
                <button
                  type="button"
                  className="small-button"
                  onClick={resetStandingsFilters}
                >
                  Reset standings filters
                </button>
              </div>
            )}
          </div>

          {filteredStandings.length > 0 ? (
            <div className="table-scroll">
              <table>
                <thead>
                  <tr>
                    <th>Rank</th>
                    <th>Player</th>
                    <th>Team</th>
                    <th>Points</th>
                    <th>Detail</th>
                  </tr>
                </thead>
                <tbody>
                  {sortedStandings.map((row) => (
                    <tr className={selectedPlayerDetail?.player.playerId === row.playerId ? "table-row--active" : ""} key={row.playerId}>
                      <td>{row.rank}</td>
                      <td>{row.playerName}</td>
                      <td>{row.teamName}</td>
                      <td>{row.totalPoints}</td>
                      <td className="button-cell">
                        <button
                          type="button"
                          className={`small-button ${selectedPlayerDetail?.player.playerId === row.playerId ? "small-button--active" : ""}`}
                          onClick={() => {
                            void loadPlayerStandingDetail(row);
                          }}
                          disabled={isLoadingDetail}
                          aria-pressed={selectedPlayerDetail?.player.playerId === row.playerId}
                        >
                          {isLoadingDetail && selectedPlayerDetail?.player.playerId === row.playerId ? "Loading..." : "View"}
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <p className="empty-state">No standings rows match the active player/team filters.</p>
          )}
        </section>
      )}

      {activeView === VIEW_RIDERS && riders.length > 0 && (
        <section className="panel">
          <div className="panel__header">
            <div>
              <h2 className="panel__title">Riders</h2>
              <p className="panel__subtitle">Browse the edition rider catalog with search and nationality filters.</p>
            </div>
            <div className="panel__chips">
              {hasActiveRiderFilters && <span className="active-chip">Filters active</span>}
              <span className="score-chip">
                {filteredRiders.length} / {riders.length} riders
              </span>
            </div>
          </div>

          <div className="rider-filters" aria-label="Rider filters">
            <label className="rider-filter">
              <span>Name</span>
              <input
                type="search"
                value={riderSearchQuery}
                onChange={(event) => {
                  setRiderSearchQuery(event.target.value);
                }}
                placeholder="Search rider name"
              />
            </label>

            <label className="rider-filter">
              <span>Nationality</span>
              <select
                value={riderNationalityFilter}
                onChange={(event) => {
                  setRiderNationalityFilter(event.target.value);
                }}
              >
                <option value={ALL_NATIONALITIES}>All</option>
                {nationalityOptions.map((nationality) => (
                  <option key={nationality} value={nationality}>
                    {nationality}
                  </option>
                ))}
              </select>
            </label>

            <label className="rider-filter rider-filter--checkbox">
              <input
                type="checkbox"
                checked={includeWithdrawnRiders}
                onChange={(event) => {
                  setIncludeWithdrawnRiders(event.target.checked);
                }}
              />
              <span>Include withdrawn riders</span>
            </label>

            <label className="rider-filter">
              <span>Sort</span>
              <select
                value={riderSort}
                onChange={(event) => {
                  setRiderSort(event.target.value as RiderSort);
                }}
              >
                <option value={RIDER_SORT_NAME}>Name</option>
                <option value={RIDER_SORT_VALUE_DESC}>Value (high-low)</option>
              </select>
            </label>

            {hasActiveRiderFilters && (
              <div className="filter-actions">
                <button
                  type="button"
                  className="small-button"
                  onClick={resetRiderFilters}
                >
                  Reset rider filters
                </button>
              </div>
            )}
          </div>

          {filteredRiders.length > 0 ? (
            <div className="rider-grid">
              {sortedRiders.map((rider) => (
                <article
                  className={`rider-card ${rider.withdrawn ? "rider-card--withdrawn" : ""} ${selectedRider?.riderId === rider.riderId ? "rider-card--active" : ""}`}
                  key={rider.riderId}
                >
                  <header className="rider-card__header">
                    <span className="score-chip">#{rider.gameRiderNumber}</span>
                    {rider.withdrawn && <span className="rider-card__status">Withdrawn</span>}
                  </header>
                  <h3 className="panel__title">{rider.name}</h3>
                  <p className="panel__subtitle">Race bib {rider.raceBibNumber}</p>
                  <dl className="rider-meta">
                    <div>
                      <dt>Value</dt>
                      <dd>{formatRiderValue(rider.value)}</dd>
                    </div>
                    <div>
                      <dt>Nationality</dt>
                      <dd>{rider.nationality}</dd>
                    </div>
                  </dl>
                  <div className="rider-card__actions">
                    <button
                      type="button"
                      className={`small-button ${selectedRider?.riderId === rider.riderId ? "small-button--active" : ""}`}
                      onClick={() => {
                        void loadRiderScorePreview(rider);
                      }}
                      disabled={isLoadingRiderPreview && selectedRider?.riderId === rider.riderId}
                      aria-pressed={selectedRider?.riderId === rider.riderId}
                    >
                      {isLoadingRiderPreview && selectedRider?.riderId === rider.riderId ? "Loading..." : "Preview scores"}
                    </button>
                  </div>
                </article>
              ))}
            </div>
          ) : (
            <p className="empty-state">No riders match the active filters.</p>
          )}

          {selectedRider !== null && (
            <section className="rider-preview-drawer" aria-live="polite">
              <div className="panel__header">
                <div>
                  <h3 className="panel__title">Preview scores: {selectedRider.name}</h3>
                  <p className="panel__subtitle">Sampled from the top {RIDER_PREVIEW_PLAYER_LIMIT} standings players.</p>
                </div>
                {hasActiveRiderPreviewStageFilter && <span className="active-chip">Stage filter active</span>}
              </div>

              {riderPreviewStageOptions.length > 0 && (
                <div className="detail-toolbar">
                  <span className="detail-toolbar__label">Filter stages</span>
                  <div className="detail-toolbar__actions">
                    <button
                      type="button"
                      className={`small-button ${riderPreviewStageFilter === ALL_RIDER_PREVIEW_STAGES ? "small-button--active" : ""}`}
                      onClick={() => {
                        setRiderPreviewStageFilter(ALL_RIDER_PREVIEW_STAGES);
                      }}
                      aria-pressed={riderPreviewStageFilter === ALL_RIDER_PREVIEW_STAGES}
                    >
                      All
                    </button>
                    {riderPreviewStageOptions.map((stage) => (
                      <button
                        type="button"
                        className={`small-button ${riderPreviewStageFilter === stage ? "small-button--active" : ""}`}
                        onClick={() => {
                          setRiderPreviewStageFilter(stage);
                        }}
                        key={`rider-preview-stage-${stage}`}
                        aria-pressed={riderPreviewStageFilter === stage}
                      >
                        Stage {stage}
                      </button>
                    ))}
                    {hasActiveRiderPreviewStageFilter && (
                      <button
                        type="button"
                        className="small-button"
                        onClick={() => {
                          setRiderPreviewStageFilter(ALL_RIDER_PREVIEW_STAGES);
                        }}
                      >
                        Clear stage filter
                      </button>
                    )}
                  </div>
                </div>
              )}

              {riderPreviewErrorMessage !== "" && <p className="message message--error">{riderPreviewErrorMessage}</p>}

              {filteredRiderPreviewRows.length > 0 ? (
                <div className="table-scroll">
                  <table>
                    <thead>
                      <tr>
                        <th>Stage</th>
                        <th>Player</th>
                        <th>Position</th>
                        <th>Substitute</th>
                        <th>Points</th>
                        <th>Contributions</th>
                        <th>Detail</th>
                      </tr>
                    </thead>
                    <tbody>
                      {filteredRiderPreviewRows.map((previewRow) => (
                        <tr key={previewRow.key}>
                          <td>{previewRow.stage}</td>
                          <td>{previewRow.playerName}</td>
                          <td>{previewRow.position}</td>
                          <td>{previewRow.substitute ? "Yes" : "No"}</td>
                          <td>{previewRow.points}</td>
                          <td>
                            {previewRow.contributions.length > 0 ? (
                              <ul className="detail-inline-list">
                                {previewRow.contributions.map((contribution) => (
                                  <li key={`${previewRow.key}-${contribution}`}>{contribution}</li>
                                ))}
                              </ul>
                            ) : (
                              <span className="muted">No score contributions recorded.</span>
                            )}
                          </td>
                          <td className="button-cell">
                            <button
                              type="button"
                              className="small-button"
                              onClick={() => {
                                void openPlayerDetailFromRiderPreview(previewRow.playerId);
                              }}
                              disabled={isLoadingDetail || isLoading}
                            >
                              Open detail
                            </button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              ) : (
                !isLoadingRiderPreview &&
                riderPreviewErrorMessage === "" && (
                  <p className="empty-state">
                    {selectedRiderScorePreview.length > 0
                      ? "No preview rows match the active stage filter."
                      : "No preview rows found for this rider."}
                  </p>
                )
              )}
            </section>
          )}
        </section>
      )}

      {activeView === VIEW_ADMIN && (
        <section className="panel">
          <div className="panel__header">
            <div>
              <h2 className="panel__title">Admin Edition Configuration</h2>
              <p className="panel__subtitle">Create editions and update core configuration fields through admin endpoints.</p>
            </div>
            <span className="score-chip">MVP</span>
          </div>

          <section className="list-card admin-card">
            <div className="panel__header" style={{ marginBottom: 0 }}>
              <div>
                <h3>Current admin context</h3>
                <p className="panel__subtitle">Read-only snapshot of the active edition configuration.</p>
              </div>
              <button
                type="button"
                className="small-button"
                disabled={isLoading}
                onClick={() => {
                  void loadCurrentEditionData(VIEW_ADMIN);
                }}
              >
                {isLoading ? "Refreshing..." : "Refresh context"}
              </button>
            </div>

            {adminContext !== null ? (
              <dl className="admin-context-list">
                <div>
                  <dt>Edition</dt>
                  <dd>
                    #{adminContext.editionId} ({adminContext.year})
                  </dd>
                </div>
                <div>
                  <dt>Current stage</dt>
                  <dd>{adminContext.currentStage}</dd>
                </div>
                <div>
                  <dt>Entry deadline</dt>
                  <dd>{formatDateTimeForDisplay(adminContext.entryDeadline)}</dd>
                </div>
                <div>
                  <dt>Reserve budget</dt>
                  <dd>{adminContext.reserveBudget}</dd>
                </div>
                <div>
                  <dt>Required nationality</dt>
                  <dd>{adminContext.requiredNationality}</dd>
                </div>
                <div>
                  <dt>Double points stages</dt>
                  <dd>
                    {adminContext.doublePointsStages.length > 0 ? adminContext.doublePointsStages.join(", ") : "None"}
                  </dd>
                </div>
              </dl>
            ) : (
              <p className="empty-state">Load admin context to inspect the current edition configuration snapshot.</p>
            )}
          </section>

          <div className="admin-grid">
            <section className="list-card admin-card">
              <h3>Create edition</h3>
              <div className="admin-form-grid">
                <label className="rider-filter">
                  <span>Edition year</span>
                  <input
                    type="number"
                    min={2020}
                    step={1}
                    value={adminEditionYear}
                    onChange={(event) => {
                      setAdminEditionYear(event.target.value);
                    }}
                  />
                </label>
                <button
                  type="button"
                  className="small-button"
                  disabled={isLoadingAdmin}
                  onClick={() => {
                    void createEdition();
                  }}
                >
                  {activeAdminAction === "createEdition" ? "Saving..." : "Create edition"}
                </button>
                {renderAdminFeedback("createEdition")}
              </div>
            </section>

            <section className="list-card admin-card">
              <div className="admin-card__header">
                <h3>Target edition</h3>
                <div className="admin-card__actions">
                  {isTargetEditionDirty && <span className="active-chip">Changed</span>}
                  {isTargetEditionDirty && (
                    <button
                      type="button"
                      className="small-button"
                      onClick={() => {
                        setAdminEditionIdInput(adminContextEditionId);
                      }}
                    >
                      Reset target edition
                    </button>
                  )}
                </div>
              </div>
              <div className="admin-form-grid">
                <label className="rider-filter">
                  <span>Edition id</span>
                  <input
                    type="number"
                    min={1}
                    step={1}
                    value={adminEditionIdInput}
                    onChange={(event) => {
                      setAdminEditionIdInput(event.target.value);
                    }}
                  />
                </label>
              </div>
            </section>

            <section className="list-card admin-card">
              <div className="admin-card__header">
                <h3>Entry deadline</h3>
                <div className="admin-card__actions">
                  {isEntryDeadlineDirty && <span className="active-chip">Changed</span>}
                  {isEntryDeadlineDirty && (
                    <button
                      type="button"
                      className="small-button"
                      onClick={() => {
                        setAdminEntryDeadline(adminContextEntryDeadline);
                      }}
                    >
                      Reset deadline
                    </button>
                  )}
                </div>
              </div>
              <div className="admin-form-grid">
                <label className="rider-filter">
                  <span>Deadline (local)</span>
                  <input
                    type="datetime-local"
                    value={adminEntryDeadline}
                    onChange={(event) => {
                      setAdminEntryDeadline(event.target.value);
                    }}
                  />
                </label>
                <button
                  type="button"
                  className="small-button"
                  disabled={isLoadingAdmin || !canSaveEntryDeadline}
                  onClick={() => {
                    void saveEditionDeadline();
                  }}
                >
                  {activeAdminAction === "entryDeadline" ? "Saving..." : "Save deadline"}
                </button>
                {getSaveHint(canSaveEntryDeadline) !== "" && <p className="admin-form-hint">{getSaveHint(canSaveEntryDeadline)}</p>}
                {renderAdminFeedback("entryDeadline")}
              </div>
            </section>

            <section className="list-card admin-card">
              <div className="admin-card__header">
                <h3>Reserve budget</h3>
                <div className="admin-card__actions">
                  {isReserveBudgetDirty && <span className="active-chip">Changed</span>}
                  {isReserveBudgetDirty && (
                    <button
                      type="button"
                      className="small-button"
                      onClick={() => {
                        setAdminReserveBudget(adminContextReserveBudget);
                      }}
                    >
                      Reset reserve budget
                    </button>
                  )}
                </div>
              </div>
              <div className="admin-form-grid">
                <label className="rider-filter">
                  <span>Reserve budget</span>
                  <input
                    type="number"
                    min={0}
                    step={1}
                    value={adminReserveBudget}
                    onChange={(event) => {
                      setAdminReserveBudget(event.target.value);
                    }}
                  />
                </label>
                <button
                  type="button"
                  className="small-button"
                  disabled={isLoadingAdmin || !canSaveReserveBudget}
                  onClick={() => {
                    void saveReserveBudget();
                  }}
                >
                  {activeAdminAction === "reserveBudget" ? "Saving..." : "Save reserve budget"}
                </button>
                {getSaveHint(canSaveReserveBudget) !== "" && <p className="admin-form-hint">{getSaveHint(canSaveReserveBudget)}</p>}
                {renderAdminFeedback("reserveBudget")}
              </div>
            </section>

            <section className="list-card admin-card">
              <div className="admin-card__header">
                <h3>Required nationality</h3>
                <div className="admin-card__actions">
                  {isRequiredNationalityDirty && <span className="active-chip">Changed</span>}
                  {isRequiredNationalityDirty && (
                    <button
                      type="button"
                      className="small-button"
                      onClick={() => {
                        setAdminRequiredNationality(adminContextRequiredNationality);
                      }}
                    >
                      Reset nationality
                    </button>
                  )}
                </div>
              </div>
              <div className="admin-form-grid">
                <label className="rider-filter">
                  <span>3-letter code</span>
                  <input
                    type="text"
                    maxLength={3}
                    value={adminRequiredNationality}
                    onChange={(event) => {
                      setAdminRequiredNationality(event.target.value.toUpperCase());
                    }}
                  />
                </label>
                <button
                  type="button"
                  className="small-button"
                  disabled={isLoadingAdmin || !canSaveRequiredNationality}
                  onClick={() => {
                    void saveRequiredNationality();
                  }}
                >
                  {activeAdminAction === "requiredNationality" ? "Saving..." : "Save required nationality"}
                </button>
                {getSaveHint(canSaveRequiredNationality) !== "" && (
                  <p className="admin-form-hint">{getSaveHint(canSaveRequiredNationality)}</p>
                )}
                {renderAdminFeedback("requiredNationality")}
              </div>
            </section>

            <section className="list-card admin-card">
              <div className="admin-card__header">
                <h3>Double points stages</h3>
                <div className="admin-card__actions">
                  {isDoublePointsStagesDirty && <span className="active-chip">Changed</span>}
                  {isDoublePointsStagesDirty && (
                    <button
                      type="button"
                      className="small-button"
                      onClick={() => {
                        setAdminDoublePointsStages(adminContextDoublePointsStages);
                      }}
                    >
                      Reset stages
                    </button>
                  )}
                </div>
              </div>
              <div className="admin-form-grid">
                <label className="rider-filter">
                  <span>Comma-separated stages</span>
                  <input
                    type="text"
                    value={adminDoublePointsStages}
                    onChange={(event) => {
                      setAdminDoublePointsStages(event.target.value);
                    }}
                    placeholder="5,12"
                  />
                </label>
                <button
                  type="button"
                  className="small-button"
                  disabled={isLoadingAdmin || !canSaveDoublePointsStages}
                  onClick={() => {
                    void saveDoublePointsStages();
                  }}
                >
                  {activeAdminAction === "doublePointsStages" ? "Saving..." : "Save double points stages"}
                </button>
                {getSaveHint(canSaveDoublePointsStages) !== "" && (
                  <p className="admin-form-hint">{getSaveHint(canSaveDoublePointsStages)}</p>
                )}
                {renderAdminFeedback("doublePointsStages")}
              </div>
            </section>
          </div>
        </section>
      )}

      {activeView === VIEW_STANDINGS && selectedPlayerDetail !== null && (
        <section className="panel detail-stack">
          <div className="panel__header">
            <div>
              <h2 className="panel__title">Player Detail: {selectedPlayerDetail.player.playerName}</h2>
              <p className="panel__subtitle">
                Team {selectedPlayerDetail.player.teamName} • Total points {selectedPlayerDetail.player.totalPoints}
              </p>
            </div>
            <div className="panel__chips">
              {hasActiveDetailStageFilter && <span className="active-chip">Stage filter active</span>}
              <span className="score-chip">Stage history</span>
            </div>
          </div>

          {detailStageFilterOptions.length > 0 && (
            <div className="detail-toolbar">
              <span className="detail-toolbar__label">Filter stages</span>
              <div className="detail-toolbar__actions">
                <button
                  type="button"
                  className={`small-button ${detailStageFilter === ALL_STAGES_FILTER ? "small-button--active" : ""}`}
                  onClick={() => {
                    setDetailStageFilter(ALL_STAGES_FILTER);
                  }}
                  aria-pressed={detailStageFilter === ALL_STAGES_FILTER}
                >
                  All
                </button>
                {detailStageFilterOptions.map((stage) => (
                  <button
                    type="button"
                    className={`small-button ${detailStageFilter === stage ? "small-button--active" : ""}`}
                    onClick={() => {
                      setDetailStageFilter(stage);
                    }}
                    key={stage}
                    aria-pressed={detailStageFilter === stage}
                  >
                    Stage {stage}
                  </button>
                ))}
                {hasActiveDetailStageFilter && (
                  <button
                    type="button"
                    className="small-button"
                    onClick={() => {
                      setDetailStageFilter(ALL_STAGES_FILTER);
                    }}
                  >
                    Clear stage filter
                  </button>
                )}
              </div>
            </div>
          )}

          {filteredStageHistory.length > 0 ? (
            <div className="detail-stack">
              {filteredStageHistory.map((item) => (
                <article className="stage-card" key={item.stage}>
                  <div className="stage-card__summary">
                    <div className="panel__header" style={{ marginBottom: 0 }}>
                      <div>
                        <h3 className="panel__title">Stage {item.stage}</h3>
                        <p className="panel__subtitle">
                          Stage points {item.stagePoints} • total points {item.totalPoints}
                        </p>
                      </div>
                    </div>

                    <div className="metric-grid">
                      <div className="metric">
                        <span className="metric__label">Substitutions</span>
                        <span className="metric__value">{item.substitutions.length}</span>
                      </div>
                      <div className="metric">
                        <span className="metric__label">Rider scores</span>
                        <span className="metric__value">{item.riderScores.length}</span>
                      </div>
                    </div>
                  </div>

                  <div className="subgrid">
                    <div className="list-card">
                      <h4>Substitutions</h4>
                      {item.substitutions.length > 0 ? (
                        <div className="table-scroll">
                          <table>
                            <thead>
                              <tr>
                                <th>Position</th>
                                <th>Effective Stage</th>
                                <th>Replaced Rider</th>
                                <th>Substitute Rider</th>
                              </tr>
                            </thead>
                            <tbody>
                              {item.substitutions.map((substitution) => (
                                <tr key={`${item.stage}-${substitution.position}-${substitution.effectiveStage}`}>
                                  <td>{substitution.position}</td>
                                  <td>{substitution.effectiveStage}</td>
                                  <td>
                                    {substitution.replacedRider.riderName} (bib {substitution.replacedRider.raceBibNumber})
                                  </td>
                                  <td>
                                    {substitution.substituteRider.riderName} (bib {substitution.substituteRider.raceBibNumber})
                                  </td>
                                </tr>
                              ))}
                            </tbody>
                          </table>
                        </div>
                      ) : (
                        <p className="empty-state">No substitutions recorded for this stage.</p>
                      )}
                    </div>

                    <div className="list-card">
                      <h4>Rider scores</h4>
                      {item.riderScores.length > 0 ? (
                        <div className="table-scroll">
                          <table>
                            <thead>
                              <tr>
                                <th>Position</th>
                                <th>Rider</th>
                                <th>Substitute</th>
                                <th>Points</th>
                                <th>Score contributions</th>
                                <th>Rider detail</th>
                              </tr>
                            </thead>
                            <tbody>
                              {item.riderScores.map((riderScore) => {
                                const visibleScoreItems = getVisibleScoreContributions(riderScore.scoreItems);
                                const hiddenScoreItemCount = getHiddenScoreContributionCount(riderScore.scoreItems);

                                return (
                                  <tr key={`${item.stage}-${riderScore.position}-${riderScore.rider.riderId}`}>
                                    <td>{riderScore.position}</td>
                                    <td>
                                      {riderScore.rider.riderName} (bib {riderScore.rider.raceBibNumber})
                                    </td>
                                    <td>{riderScore.substitute ? "Yes" : "No"}</td>
                                    <td>{riderScore.points}</td>
                                    <td>
                                      {visibleScoreItems.length > 0 ? (
                                        <ul className="detail-inline-list">
                                          {visibleScoreItems.map((scoreItem, index) => (
                                            <li key={`${item.stage}-${riderScore.position}-${scoreItem.ruleCode}-${index}`}>
                                              {formatScoreContribution(scoreItem)}
                                            </li>
                                          ))}
                                          {hiddenScoreItemCount > 0 && (
                                            <li>
                                              <span className="muted">and {hiddenScoreItemCount} more contribution{hiddenScoreItemCount === 1 ? "" : "s"}</span>
                                            </li>
                                          )}
                                        </ul>
                                      ) : (
                                        <span className="muted">No score contributions recorded.</span>
                                      )}
                                    </td>
                                    <td className="button-cell">
                                      <button
                                        type="button"
                                        className="small-button"
                                        onClick={() => {
                                          void openRiderFromPlayerDetail(riderScore.rider.riderId);
                                        }}
                                        disabled={isLoading || isLoadingRiderPreview}
                                      >
                                        Open rider
                                      </button>
                                    </td>
                                  </tr>
                                );
                              })}
                            </tbody>
                          </table>
                        </div>
                      ) : (
                        <p className="empty-state">No rider scores recorded for this stage.</p>
                      )}
                    </div>
                  </div>
                </article>
              ))}
            </div>
          ) : (
            <p className="empty-state">
              {selectedPlayerDetail.stageHistory.length > 0
                ? "No stage history matches the active filter."
                : "No stage history returned for this player."}
            </p>
          )}
        </section>
      )}
    </main>
  );
}

