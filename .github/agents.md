# Project Overview & AI Agent Guide

## Repository Structure
This is a monorepo containing both the backend and frontend services.
* `/tour-backend` - Spring Boot 3 API (Hexagonal Architecture)
* `/tour-frontend` - React TypeScript SPA

## Tech Stack Summary
* **Backend:** Java 21+, Maven 3, Spring Boot 3, Spring Security, JPA/Hibernate, Liquibase, PostgreSQL.
* **Frontend:** React, TypeScript, Vite.
* **Security:** SPA + Backend API secured via External Identity Provider (OAuth2/OIDC).
* **API Contract:** OpenAPI 3.0 specification.

## Security Infrastructure Context
* **Architecture:** The application uses a decoupled SPA + Backend Resource Server architecture.
* **Identity Provider (IdP):** Authentication is outsourced to an external Identity Provider (e.g., Keycloak, Auth0, Entra ID) using OpenID Connect (OIDC).
* **Token Handshake:** The frontend obtains a JSON Web Token (JWT) directly from the IdP. The backend does not issue tokens; it only validates them.
* **Stateless Backend:** The backend does not maintain HTTP sessions (`SessionCreationPolicy.STATELESS`). Every request must contain a valid Bearer JWT.

## General Agent Rules
1. **Context Awareness:** Always check if your task applies to the `/tour-backend` or `/tour-frontend` directory.
2. **Architecture Enforcement:** Do not mix frontend and backend paradigms. Follow the specific instructions inside each directory.
3. **API Alignment:** All code modifications touching the API boundary must strictly align with the OpenAPI specification.
