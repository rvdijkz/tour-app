# tour-frontend

Minimal React + TypeScript + Vite frontend scaffold with a dedicated API layer.

## What is included

- Strict TypeScript setup (`tsconfig.app.json`)
- Dedicated API layer under `src/api`
- Bearer-token-ready HTTP client via `TokenProvider`
- Simple UI in `src/App.tsx` that loads current edition standings through the API service
- Unit tests for auth header behavior (`src/api/httpClient.test.ts`)

## Environment

- `VITE_API_BASE_URL` (default: `http://localhost:8080`)

## Quick start

```powershell
npm install
npm run dev
```

## Run tests

```powershell
npm run test
```

## Current UI behavior

- Select **Standings** and click **Load Current Standings** to call:
  - `GET /editions/current`
  - `GET /editions/{editionId}/standings`
- The table renders rank, player, team, and points from the backend response.
- Use standings filters (player search + team selector) to narrow visible rows without additional API calls.
- Use standings sort to switch between **Rank** and **Points (high-low)** ordering.
- Use **Reset standings filters** to quickly return to the full standings list.
- Header badges indicate when standings/rider/stage filters are currently active.
- Dashboard view and filter selections are persisted in browser local storage and restored after reload.
- Dashboard view and filter selections are also mirrored in URL query params for shareable links.
- Use **Reset dashboard filters** in the top toolbar to clear standings/rider/stage filters in one action.
- Use **Copy share link** to copy the current URL (including dashboard query state) to your clipboard (Clipboard API with legacy fallback).
- Click **View** on a row to call `GET /editions/{editionId}/standings/{playerId}` and render each stage's substitutions plus the first three score contributions per rider score.
- Use the detail stage filter chips (**All**, **Stage N**) to focus the player history on specific stages.
- In player detail, click **Open rider** on a rider-score row to jump to **Riders**, auto-focus that rider, and open score preview.
- Select **Riders** and click **Load Riders** to call:
  - `GET /editions/current`
  - `GET /editions/{editionId}/riders`
- The riders dashboard supports name search, nationality filtering, and optional withdrawn-rider visibility.
- Use rider sort to switch between **Name** and **Value (high-low)** ordering.
- Use **Reset rider filters** to restore default rider filter settings.
- Click **Preview scores** on a rider card to sample top standings players and aggregate rider score rows from:
  - `GET /editions/{editionId}/standings`
  - `GET /editions/{editionId}/standings/{playerId}`
- The rider drawer renders stage/points/substitute state and a short score-contribution preview.
- Use rider-preview stage filter chips (**All**, **Stage N**) to focus preview rows on a selected stage.
- In both preview/detail stage filter toolbars, **Clear stage filter** returns to **All**.
- From the rider drawer, click **Open detail** to jump to standings and load that player's full stage history.

