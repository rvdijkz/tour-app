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

- Click **Load Current Standings** to call:
  - `GET /editions/current`
  - `GET /editions/{editionId}/standings`
- The table renders rank, player, team, and points from the backend response.
- Click **View** on a row to call `GET /editions/{editionId}/standings/{playerId}` and render each stage's substitutions plus the first three score contributions per rider score.

