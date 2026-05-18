# Specification Conflict Resolution Sheet

## Purpose

This document resolves inconsistencies across:

- PRD (`docs/prd.md`)
- Domain model (`docs/domain-model.md`)
- GUI guidelines (`docs/gui-guidelines/`)

It defines the final business rules to use for API design, implementation, and test cases.

## Decision Log

### CR-001: Tour finisher prediction scope

- Conflict:
  - PRD defines prediction as total tour finishers.
  - Domain model contains team-level ranges (`1-14`, `0-14`) and team-finish wording.
- Decision:
  - Prediction is the expected number of finishers for the entire tour (global race total), not team-specific.
- Required model update:
  - Replace team-level prediction semantics with `predictedTourFinishers` and `actualTourFinishers` at tour scope (or derived in standings service).
  - Valid range: `0..tour.totalStarters`.
- Acceptance tests:
  - A team can submit prediction `145` for a 176-rider tour.
  - A prediction `177` is rejected when total starters is `176`.

### CR-002: Tiebreaker bonus value

- Conflict:
  - PRD states a binary bonus: best prediction gets `+1`.
  - Domain model includes broader bonus ranges.
- Decision:
  - Tiebreaker bonus is binary: `0` or `1` only.
  - All tied players with the minimum absolute difference get `+1`.
- Required model update:
  - `finisherPredictionBonus` constrained to enum-like values `{0,1}`.
- Acceptance tests:
  - Two players tied on score and tied on best prediction both receive `+1`.

### CR-003: Stage immutability vs admin edits/deletes

- Conflict:
  - NFR/domain expect immutable stage history after scoring.
  - Admin UI includes EDIT/DELETE actions for completed stages.
- Decision:
  - Finalized stage results are immutable.
  - Corrections are done through a versioned recalculation workflow, never hard edit/delete of finalized data.
- Required product update:
  - Replace admin actions `EDIT` and `DELETE` for finalized stages with `CREATE CORRECTION` and `RECALCULATE`.
  - Preserve full audit trail of versions.
- Acceptance tests:
  - Attempting to directly edit a finalized stage returns conflict.
  - Recalculation creates a new version with timestamp and actor.

### CR-004: Dropout data constraints

- Conflict:
  - Domain model says dropouts must be in finish positions list.
  - Real-world dropouts are often not in top finish lists.
- Decision:
  - Dropouts are independent from finish positions.
  - A dropout must be a valid active cyclist in the tour and not previously marked dropped out.
- Required model update:
  - Remove constraint linking dropout membership to stage finish positions.
- Acceptance tests:
  - A cyclist can be marked dropped out even when absent from entered top-20 finishers.

### CR-005: Budget defaults mismatch

- Conflict:
  - PRD defines defaults `standard=100`, `reserve=20`.
  - Some UI examples show `400/110` style values.
- Decision:
  - Canonical defaults are `100` standard and `20` base reserve.
  - UI examples must align with config values and avoid conflicting sample totals.
- Required spec update:
  - Update all examples in team composition page to 100/20 baseline, unless explicitly showing custom config.
- Implementation note:
  - Applied in `docs/gui-guidelines/pages/03-team-composition.md` on 2026-05-17.
- Acceptance tests:
  - Reserve available budget equals `reserveBase + (standardBudget - standardUsed)`.

### CR-006: Reserve activation timing and ordering

- Gap:
  - Reserve activation edge cases are underdefined across docs.
- Decision:
  - Reserve activation takes effect from the next stage only.
  - In a stage with multiple new dropouts among positions 1-11, replace by ascending team position (lowest number first).
  - Reserve 1 is consumed before Reserve 2.
  - A reserve dropped out before activation can never activate and gives no dropout penalty before activation.
- Implementation note:
  - Applied in `docs/prd.md`, `docs/domain-model.md`, and `docs/gui-guidelines/pages/03-team-composition.md` on 2026-05-17.
- Acceptance tests:
  - If positions 3 and 8 drop in same stage, Reserve 1 replaces position 3 next stage, Reserve 2 replaces position 8 next stage.

### CR-007: Double points formula scope

- Gap:
  - "All points except kluns penalties are doubled" can be interpreted inconsistently.
- Decision:
  - Double points applies to all scoring components for positions 1-11 in that stage: base points, position bonus, jersey bonus, combative bonus, and dropout penalty magnitude.
  - Kluns scoring remains unchanged and is never doubled.
- Acceptance tests:
  - A `-2` dropout penalty for positions 1-11 becomes `-4` on double points stage.
  - Kluns `-10` remains `-10`.

### CR-008: Player deletion and historical standings

- Conflict:
  - Admin page suggests deleting player removes team from standings.
  - Historical data integrity requires stable standings.
- Decision:
  - Player deletion is replaced by deactivation (soft delete).
  - Historical teams, scores, and standings remain unchanged.
- Acceptance tests:
  - Deactivated player cannot log in.
  - Existing stage and final standings retain player entries.

## Canonical Terms

Use these terms consistently in all docs and API contracts:

- `predictedTourFinishers`: Player-provided forecast for total tour finishers.
- `actualTourFinishers`: Final total number of cyclists who completed tour.
- `finisherPredictionBonus`: Integer in `{0,1}` used only as final-tie tiebreaker.
- `standardPoolBudget`: Config max for positions 1-11.
- `reservePoolBudget`: Base config max for positions 12-13.
- `availableReserveBudget`: `reservePoolBudget + (standardPoolBudget - standardPoolUsed)`.

## Required Follow-up Updates

1. Update `docs/domain-model.md` to reflect CR-001, CR-002, CR-004, CR-006, and CR-007.
2. Update `docs/gui-guidelines/pages/03-team-composition.md` budget examples (CR-005).
3. Update `docs/gui-guidelines/pages/05-admin-pages.md` finalized-stage action model and player deletion behavior (CR-003, CR-008).
4. Adopt OpenAPI spec in `tour-backend/openapi/tour-game.openapi.yaml` as implementation contract.

## Status

- Resolution status: Proposed and ready for approval
- Version: `v0.1`
- Date: `2026-05-17`
