# Tour Game — Specification Index

## Status and source

These specifications were approved by the user as version 1.0.0 on 2026-09-27. The approved baseline is identified by Git tag `specs-v1.0.0`. They are derived from [the original description](raw.txt), which is retained unchanged for traceability.

Requirements below capture rules stated in the source and subsequent user decisions. Explicit user corrections supersede the original description. Unresolved behavior is recorded in [open questions](open-questions.md); it must not be treated as an approved requirement or silently decided during implementation. Acceptance criteria cover only behavior that is sufficiently clear.

## Documents

| Document | Authoritative subject |
| --- | --- |
| [Game rules](game-rules.md) | Terminology, rider data, team composition, entry constraints, and substitutions |
| [Scoring](scoring.md) | Stage points, bonuses, penalties, and final ranking |
| [Administration](administration.md) | Accounts, rider import, configuration, stage entry, and publication |
| [User screens](user-screens.md) | User-facing workflows and information |
| [Open questions](open-questions.md) | Decisions required before the affected behavior can be implemented |

## Design constraints

- The application is a web application with a Spring Boot backend and React/TypeScript frontend, as specified in [AGENTS.md](AGENTS.md).
- All specifications are written in English.
- No new application architecture, API contracts, or low-level implementation decisions are introduced in this specification baseline.

## Assumptions and review approach

No additional business rules are assumed. The user has limited the application to the Tour de France game under GR-01 in [game rules](game-rules.md), replacing the original configurable-race scope. Giro and Vuelta compatibility is no longer required.

The nationality code set under A-03 in [open questions](open-questions.md) remains an explicit outstanding detail; approval does not select a code set. Other low-level implementation details remain deferred as documented. Subsequent requirement changes must be explicitly approved by the user, recorded in the authoritative document, and checked for cross-document consistency. Preserve tag `specs-v1.0.0` as the approved baseline; record later revisions in new commits rather than moving this tag.
