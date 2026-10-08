import { useMemo, useState } from "react";
import { environment } from "./config/env";
import { FetchHttpClient } from "./api/httpClient";
import { TokenProvider } from "./api/tokenProvider";
import { TourApiClient } from "./api/tourApiClient";
import type { PlayerStandingDetail, StandingRow } from "./api/types";
import {
  formatScoreContribution,
  getHiddenScoreContributionCount,
  getVisibleScoreContributions
} from "./ui/detailPresentation";

/**
 * Minimal app shell proving API layer usage stays outside UI components.
 */
export default function App() {
  const [editionLabel, setEditionLabel] = useState<string>("No edition loaded");
  const [activeEditionId, setActiveEditionId] = useState<number | null>(null);
  const [standings, setStandings] = useState<StandingRow[]>([]);
  const [selectedPlayerDetail, setSelectedPlayerDetail] = useState<PlayerStandingDetail | null>(null);
  const [errorMessage, setErrorMessage] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isLoadingDetail, setIsLoadingDetail] = useState<boolean>(false);

  const apiClient = useMemo(() => {
    const tokenProvider = new TokenProvider(() => null);
    const httpClient = new FetchHttpClient({
      baseUrl: environment.apiBaseUrl,
      tokenProvider
    });

    return new TourApiClient(httpClient);
  }, []);

  const loadCurrentEditionAndStandings = async () => {
    setIsLoading(true);
    setErrorMessage("");

    try {
      const edition = await apiClient.getCurrentEdition();
      const standingsResponse = await apiClient.getStandings(edition.editionId);

      setEditionLabel(`${edition.name} (${edition.editionYear}) - Stage ${standingsResponse.stage}`);
      setActiveEditionId(edition.editionId);
      setStandings(standingsResponse.standings);
      setSelectedPlayerDetail(null);
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : "Unknown error";
      setEditionLabel("No edition loaded");
      setActiveEditionId(null);
      setStandings([]);
      setSelectedPlayerDetail(null);
      setErrorMessage(`Failed to load standings: ${errorMessage}`);
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
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : "Unknown error";
      setSelectedPlayerDetail(null);
      setErrorMessage(`Failed to load player detail: ${errorMessage}`);
    } finally {
      setIsLoadingDetail(false);
    }
  };

  return (
    <main className="app-shell">
      <section className="hero">
        <div className="hero__topline">
          <div>
            <h1>Tour Frontend</h1>
            <p className="hero__copy">
              Load the current edition standings, inspect a rider’s stage history, and review the first three score
              contributions for each rider score.
            </p>
          </div>

          <div className="toolbar">
            <button type="button" className="toolbar__button" onClick={loadCurrentEditionAndStandings} disabled={isLoading}>
              {isLoading ? "Loading..." : standings.length > 0 ? "Refresh Standings" : "Load Current Standings"}
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

      {standings.length === 0 && errorMessage === "" && !isLoading && (
        <p className="message message--info">
          Click <strong>Load Current Standings</strong> to fetch the latest public API data.
        </p>
      )}

      {standings.length > 0 && (
        <section className="panel">
          <div className="panel__header">
            <div>
              <h2 className="panel__title">Standings</h2>
              <p className="panel__subtitle">Rank, player, team, and total points from the live API response.</p>
            </div>
            <span className="score-chip">{standings.length} players</span>
          </div>

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
                {standings.map((row) => (
                  <tr key={row.playerId}>
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
        </section>
      )}

      {selectedPlayerDetail !== null && (
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

          {selectedPlayerDetail.stageHistory.length > 0 ? (
            <div className="detail-stack">
              {selectedPlayerDetail.stageHistory.map((item) => (
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
            <p className="empty-state">No stage history returned for this player.</p>
          )}
        </section>
      )}
    </main>
  );
}

