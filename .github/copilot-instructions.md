# Global Copilot Instructions

## Code Philosophy
* **Readability Over Abstraction:** Write clean, explicit, and self-documenting code. Avoid premature abstractions and over-engineering.
* **No Magic Strings:** Use strongly-typed constants, enums, or configuration files. Never hardcode configuration values or repetitive string literals.
* **Small Composable Functions:** Break complex logic down into small, single-purpose, easily testable functions.

## Security & Architecture
* **Security First:** The application uses an SPA + Backend API pattern secured by an external Identity Provider (IdP) using OAuth2 and OpenID Connect. Never bypass authentication checks.
* **API First:** The interface between frontend and backend is driven by the OpenAPI spec (yaml format). Ensure validation matches on both sides.

## Future Security & Development Profiles
* **Keycloak/OIDC Integration (Planned):** The production environment will be secured via OpenID Connect (OIDC) through Keycloak. Ensure all code is structured to support this integration without major refactoring.
* **Development Mode (No Auth):** During development, authentication must be disabled to allow the frontend to call the backend API directly. Use Spring profiles (`dev`, `prod`) to toggle security behavior.
* **Security Configurability:** Build security as pluggable via Spring profiles and feature toggles, not hardcoded conditions.
* **CORS for Local Development:** Configure CORS to allow local React development (typically `http://localhost:5173`). Use environment-based configuration, not hardcoded origins.
* **Bearer Token Ready:** Even in dev mode with security disabled, structure the frontend API client to support Bearer token injection for future production use.

## API-First Development (Strict Requirement)
* **Design First:** You must always update or create the OpenAPI specification file *before* writing or modifying any backend or frontend code.
* **No Manual DTOs/Controllers:** Do not manually write API Data Transfer Objects (DTOs) or Controller interfaces. They must be generated via the build tooling based on the OpenAPI spec.
* **Single Source of Truth:** The OpenAPI specification is the absolute source of truth for the application contract.
