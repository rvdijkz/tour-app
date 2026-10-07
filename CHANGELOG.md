# Changelog

All notable changes to this project are documented in this file.

The format is based on Keep a Changelog, and this project follows semantic-ish milestone tags.

## [Unreleased]

### Added
- Initial frontend scaffold in `tour-frontend` (React + TypeScript + Vite).
- Token-ready HTTP client path for future Bearer token injection.

### Changed
- Frontend dev dependency upgrades for security and tooling compatibility:
  - `vite` -> `^7.3.7`
  - `vitest` -> `^4.1.11`

### Verified
- `npm ci` succeeds in `tour-frontend`.
- `npm run test` passes in `tour-frontend`.
- `npm run build` succeeds in `tour-frontend`.
- `npm audit` reports `0` vulnerabilities in `tour-frontend`.

## [backend-v0.1.0] - 2026-10-05

### Added
- Initial Spring Boot backend API implementation in `tour-backend`.
- OpenAPI-driven contract generation for API interfaces/DTOs.
- Endpoint-level regression coverage for player standing detail payload and not-found behavior.

### Changed
- Security/profile groundwork for dev/prod separation and JWT-ready production setup.
- Backend test/config cleanup to reduce warning noise in test output.

### Verified
- Maven backend test suite green (`25` tests passed at verification time).

## [specs-v1.0.0] - 2026-09-27

### Added
- Approved Tour game specification baseline.

[Unreleased]: https://github.com/rvdijkz/tour-app/compare/backend-v0.1.0...HEAD
[backend-v0.1.0]: https://github.com/rvdijkz/tour-app/compare/specs-v1.0.0...backend-v0.1.0
[specs-v1.0.0]: https://github.com/rvdijkz/tour-app/releases/tag/specs-v1.0.0

