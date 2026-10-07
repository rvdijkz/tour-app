# tour-frontend

Minimal React + TypeScript + Vite frontend scaffold with a dedicated API layer.

## What is included

- Strict TypeScript setup (`tsconfig.app.json`)
- Dedicated API layer under `src/api`
- Bearer-token-ready HTTP client via `TokenProvider`
- Simple UI in `src/App.tsx` that calls the API service
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

