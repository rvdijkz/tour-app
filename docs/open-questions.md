# Open Questions

Status: Decision tracking accompanying approved specification version 1.0.0 (2026-09-27). Unresolved options are not approved requirements. Resolved entries reference the authoritative rules confirmed by the user.

## Scoring — first review group

| ID | Decision needed |
| --- | --- |
| S-01 | Resolved: double-points scope is defined in SC-07 in [scoring](scoring.md). |
| S-02 | Resolved: next-stage timing, first/final-stage handling, and active-team eligibility including reserves are defined in SC-09; withdrawal-stage jersey exclusion is defined in SC-06 in [scoring](scoring.md). |
| S-03 | Resolved: classification leaders and accumulation of multiple jersey awards are defined in SC-09 in [scoring](scoring.md). |
| S-04 | Resolved: a withdrawing active rider earns no points in the withdrawal stage and incurs a one-time 2-point deduction, including activated reserves, under SC-04 and SC-06 in [scoring](scoring.md). |
| S-05 | Resolved: kluns scoring and the user-approved exclusion of overlapping top-20 and last-five conditions in stages with very few finishers are defined under SC-11 in [scoring](scoring.md); no replacement is defined under GR-17 in [game rules](game-rules.md). No overlap precedence rule is required. |
| S-06 | Resolved: intermediate ties share a rank without using predictions under SC-12; final ties use SC-13; the final score includes the sum of stage scores plus additional points under SC-15 in [scoring](scoring.md). Additional-point rules are tracked separately under S-07. |
| S-07 | Resolved: SC-15–SC-17 in [scoring](scoring.md) define the only additional points after the race, awarded independently per classification. Prediction entry rules are tracked under G-06 and results administration under A-06. |

Affected document: [Scoring](scoring.md). Timing answers may also affect [game rules](game-rules.md) and [administration](administration.md).

## Reserves and substitutions

| ID | Decision needed |
| --- | --- |
| R-01 | Resolved: substitution order within and across stages, retaining previously assigned positions, is defined in GR-13 in [game rules](game-rules.md). |
| R-02 | Resolved: a reserve of any nationality can replace position 11 and inherits its special scoring under GR-16 in [game rules](game-rules.md) and SC-02 in [scoring](scoring.md). |
| R-03 | Resolved: unavailable reserves, replacement of activated reserves, and empty positions after reserves are exhausted are defined in GR-15, GR-18, and GR-19 in [game rules](game-rules.md). |

Affected documents: [Game rules](game-rules.md), [scoring](scoring.md).

## Game setup and player entries

| ID | Decision needed |
| --- | --- |
| G-01 | Resolved: only the Tour de France game is supported, with a clean slate each year and no historical editions to browse, under GR-01 in [game rules](game-rules.md). Annual setup details are tracked under A-07. |
| G-02 | Resolved: entry requirements and Dutch local time with an exact closing instant are defined in GR-03–GR-04 and GR-23–GR-26 in [game rules](game-rules.md). Administrator reopening is allowed only before any stage result has been saved, under AD-25 in [administration](administration.md). Reopening visibility is tracked under G-08. |
| G-03 | Resolved: budgets and the default nationality are defined in GR-11, GR-12, and GR-27 in [game rules](game-rules.md). The administrator manually sets the entry deadline for each edition under AD-14 in [administration](administration.md). |
| G-04 | Resolved: public standings and player details, entries hidden from others before the deadline, and public submitted teams and all predictions after the deadline are defined in UI-06, UI-08, and UI-11–UI-13 in [user screens](user-screens.md). |
| G-05 | Resolved: the player-detail view includes current-edition stage history with points, applied reserves, and cumulative totals under UI-14 in [user screens](user-screens.md). |
| G-06 | Resolved: prediction selection and deadline rules are defined in GR-21 and GR-22; incomplete draft saving and mandatory completion for participation are defined in GR-23 and GR-24 in [game rules](game-rules.md). |
| G-07 | Resolved: the earlier accepted submitted version remains valid while a revised draft is edited. Only a successful new final submission before the deadline replaces it, under GR-25 and GR-26 in [game rules](game-rules.md). |
| G-08 | Resolved: accepted teams and predictions remain public during reopening under UI-12 in [user screens](user-screens.md). Unsubmitted drafts remain private; successful final submission replaces the public version. |

Affected documents: [Game rules](game-rules.md), [user screens](user-screens.md), [administration](administration.md), and race-dependent parts of [scoring](scoring.md).

## Administration and accounts

| ID | Decision needed |
| --- | --- |
| A-01 | Resolved: manual password delivery, account-creation validity, renewal after expiry, and administrator-mediated forgotten-password recovery are defined in AD-03, AD-04, and AD-20–AD-21 in [administration](administration.md). An administrator may also play under AD-01. Initial administrator provisioning is deferred as a deployment detail. |
| A-02 | Resolved: Excel import, rejection of the whole import on errors, and direct database updates of official numbers are defined in AD-05 and AD-06; further imports are blocked after any team has been saved under AD-19 in [administration](administration.md). No existing Excel layout is supplied; layout details are deferred to implementation. Number usage is defined in GR-28 in [game rules](game-rules.md). |
| A-03 | Partially resolved: each rider-number type is separately unique within an edition, and rider values are whole numbers from 2 through 86 inclusive, under GR-05 in [game rules](game-rules.md). Remaining decision: which three-character nationality code set is used? |
| A-04 | Resolved: configuration permissions and locking are defined in AD-22–AD-24 and reopening in AD-25 in [administration](administration.md). Initial and temporary passwords have a fixed, non-configurable 12-hour validity under AD-04 and AD-20–AD-21; player-chosen passwords do not expire under AD-26. |
| A-05 | Resolved: complete stage input, calculation, immediate publication after success, and corrections are defined in AD-07–AD-10; sequential stage entry and calculation are defined in AD-16 in [administration](administration.md). |
| A-06 | Resolved: manual final-classification input and actual finisher-count entry after stage 21, complete validation before saving, automatic calculation and publication, and corrections that automatically recalculate and republish are defined in AD-11, AD-17–AD-18, and AD-27 in [administration](administration.md). |
| A-07 | Resolved: annual restart, retained accounts and configuration, a complete new Excel rider import each year, and manual deadline setup are defined in AD-12–AD-14 in [administration](administration.md). Repeat-import restrictions are defined in AD-19. |
| A-08 | Resolved: daily ranked input is the individual stage's finishing result under AD-07 and AD-15 in [administration](administration.md). The stage winner earns the first-place points under SC-01 in [scoring](scoring.md). Final classification top fives are entered separately after stage 21 under AD-11. |

Affected documents: [Administration](administration.md), [game rules](game-rules.md), [user screens](user-screens.md), and recalculation behavior in [scoring](scoring.md).

## Deferred implementation details

Database schemas, endpoint names, component structure, and calculation internals are deferred. They do not require a business decision unless they affect the behavior described above.
