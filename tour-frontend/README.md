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
- Click **View** on a row to call `GET /editions/{editionId}/standings/{playerId}` and render each stage's substitutions plus the first three score contributions per rider score.
- Use the detail stage filter chips (**All**, **Stage N**) to focus the player history on specific stages.
- Select **Riders** and click **Load Riders** to call:
  - `GET /editions/current`
  - `GET /editions/{editionId}/riders`
- The riders dashboard supports name search, nationality filtering, and optional withdrawn-rider visibility.
- Click **Preview scores** on a rider card to sample top standings players and aggregate rider score rows from:
  - `GET /editions/{editionId}/standings`
  - `GET /editions/{editionId}/standings/{playerId}`
- The rider drawer renders stage/points/substitute state and a short score-contribution preview.

