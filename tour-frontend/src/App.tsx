import { useEffect, useMemo, useState } from "react";
import { environment } from "./config/env";
import { FetchHttpClient } from "./api/httpClient";
import { TokenProvider } from "./api/tokenProvider";
import { TourApiClient } from "./api/tourApiClient";
import type { PlayerStandingDetail, RiderSummary, StandingRow } from "./api/types";
import {
  ALL_STAGES_FILTER,
  filterStageHistory,
  formatScoreContribution,
  getStageHistoryFilterOptions,
  getHiddenScoreContributionCount,
  getVisibleScoreContributions,
  type StageHistoryFilter
} from "./ui/detailPresentation";
import { ALL_TEAMS_FILTER, filterStandings, getTeamOptions } from "./ui/standingsPresentation";
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
import { filterRiders, formatRiderValue, getNationalityOptions } from "./ui/riderPresentation";

type AppView = "standings" | "riders";

const VIEW_STANDINGS: AppView = "standings";
const VIEW_RIDERS: AppView = "riders";
const ALL_NATIONALITIES = "ALL";
const RIDER_PREVIEW_PLAYER_LIMIT = 8;

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
  const [isDashboardStateHydrated, setIsDashboardStateHydrated] = useState<boolean>(false);

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

  const filteredStandings = useMemo(
    () =>
      filterStandings(standings, {
        playerQuery: standingsPlayerQuery,
        teamName: standingsTeamFilter
      }),
    [standings, standingsPlayerQuery, standingsTeamFilter]
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
    if (mergedState.riderSearchQuery !== undefined) {
      setRiderSearchQuery(mergedState.riderSearchQuery);
    }
    if (mergedState.riderNationalityFilter !== undefined) {
      setRiderNationalityFilter(mergedState.riderNationalityFilter);
    }
    if (mergedState.includeWithdrawnRiders !== undefined) {
      setIncludeWithdrawnRiders(mergedState.includeWithdrawnRiders);
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
      riderSearchQuery,
      riderNationalityFilter,
      includeWithdrawnRiders,
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
    riderPreviewStageFilter,
    riderSearchQuery,
    standingsPlayerQuery,
    standingsTeamFilter
  ]);

  const loadCurrentEditionData = async (view: AppView) => {
    setIsLoading(true);
    setErrorMessage("");
    setActiveView(view);

    try {
      const edition = await apiClient.getCurrentEdition();
      setActiveEditionId(edition.editionId);

      if (view === VIEW_STANDINGS) {
        const standingsResponse = await apiClient.getStandings(edition.editionId);
        setEditionLabel(`${edition.name} (${edition.editionYear}) - Stage ${standingsResponse.stage}`);
        setStandings(standingsResponse.standings);
        setStandingsPlayerQuery("");
        setStandingsTeamFilter(ALL_TEAMS_FILTER);
      } else {
        const riderResponse = await apiClient.listRiders(edition.editionId);
        setEditionLabel(`${edition.name} (${edition.editionYear}) - Stage ${edition.stage}`);
        setRiders(riderResponse.riders);
      }

      if (view === VIEW_RIDERS) {
        setSelectedPlayerDetail(null);
        setDetailStageFilter(ALL_STAGES_FILTER);
      } else {
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
                  : riders.length > 0
                    ? "Refresh Riders"
                    : "Load Riders"}
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

      {activeView === VIEW_STANDINGS && standings.length > 0 && (
        <section className="panel">
          <div className="panel__header">
            <div>
              <h2 className="panel__title">Standings</h2>
              <p className="panel__subtitle">Rank, player, team, and total points from the live API response.</p>
            </div>
            <span className="score-chip">
              {filteredStandings.length} / {standings.length} players
            </span>
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
                  {filteredStandings.map((row) => (
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
            <span className="score-chip">
              {filteredRiders.length} / {riders.length} riders
            </span>
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
          </div>

          {filteredRiders.length > 0 ? (
            <div className="rider-grid">
              {filteredRiders.map((rider) => (
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

      {activeView === VIEW_STANDINGS && selectedPlayerDetail !== null && (
        <section className="panel detail-stack">
          <div className="panel__header">
            <div>
              <h2 className="panel__title">Player Detail: {selectedPlayerDetail.player.playerName}</h2>
              <p className="panel__subtitle">
                Team {selectedPlayerDetail.player.teamName} • Total points {selectedPlayerDetail.player.totalPoints}
              </p>
            </div>
            <span className="score-chip">Stage history</span>
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

