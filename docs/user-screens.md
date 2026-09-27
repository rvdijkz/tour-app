# User Screens and Workflows

Status: Approved — version 1.0.0, approved by the user on 2026-09-27. Source: [raw.txt](raw.txt). See [specification status](specification-index.md).

## Requirements

| ID | Screen or workflow | Required behavior | Authoritative rules |
| --- | --- | --- | --- |
| UI-01 | Sign-in | Sign in with a username and password; require the initial-password change within the account-creation validity period, or the renewed period after replacement | [Administration](administration.md), AD-04 and AD-20 |
| UI-02 | Add player | Administrator creates a player and initial account and personally passes the initial password to the player | [Administration](administration.md), AD-03 |
| UI-03 | Team entry | Player enters their own name, team name, ordered rider selection using game rider numbers (initial rider numbers), and predicted finisher count; the interface validates budgets | [Game rules](game-rules.md) |
| UI-04 | Rider import | Administrator imports participating riders and their game rider numbers from Excel; race bib numbers are initially copied from those numbers under AD-05. Further imports are blocked once any team has been saved in the current edition under AD-19 | [Administration](administration.md) |
| UI-05 | Stage results and scoring | Administrator processes stages in order, saves complete valid input, corrects the latest result, and invokes calculation or recalculation; successful calculation immediately publishes the outcome without a separate publication action | [Administration](administration.md), AD-07–AD-10 and AD-16 |
| UI-06 | Standings | Publicly display the standings published after each stage without requiring sign-in and allow navigation to a player's details | [Scoring](scoring.md) |
| UI-07 | Available riders | For player entry before the deadline, list riders with nationality, rider value, and game rider number; support sorting by game rider number, rider value, and nationality | [Game rules](game-rules.md) |
| UI-08 | Player details | Provide a public detail view without requiring sign-in; show the player's team, points by rider and scoring rule, and current-edition stage history under UI-14, subject to the entry visibility rules below | [Scoring](scoring.md) |
| UI-09 | Final classification predictions | Player enters and orders the three top-five prediction lists required by GR-20, selecting from all participating riders under GR-21; the interface applies the shared team-entry deadline under GR-22 | [Game rules](game-rules.md) |
| UI-10 | Final entry submission | Player explicitly clicks "Definitief indienen"; acceptance requires complete valid entry data and submission before the deadline under GR-24 and GR-25. Subsequent draft edits preserve the accepted version under GR-26 | [Game rules](game-rules.md) |
| UI-15 | Final classification results | After stage 21, the administrator manually enters or corrects all three ordered final top fives and the actual finisher count; only complete valid input can be saved, which automatically triggers calculation or recalculation and, on success, publication of the final standings | [Administration](administration.md), AD-11, AD-17–AD-18, and AD-27 |
| UI-16 | Password reset | Administrator replaces an expired initial password or resets a forgotten password for the same account, starting a new validity period, and personally passes the temporary password to the player | [Administration](administration.md), AD-20–AD-21 |

The team-entry and prediction screens support saving incomplete drafts before the editing deadline under GR-23 in [game rules](game-rules.md). Participation requires the complete entry and explicit final submission described in GR-24 and GR-25. Saving and final submission are distinct actions.

An administrator can manually start a new annual edition through the interface under AD-12 in [administration](administration.md). This action clears previous game data while retaining player accounts and configuration. For each edition, the administrator imports the complete participating rider list and manually sets the entry deadline under AD-13 and AD-14.

The application provides administrator-only controls for the reserve budget, required nationality, entry deadline, and double-points stages under AD-22 in [administration](administration.md). These controls respect the configuration locks in AD-23 and AD-24 and indicate when settings can no longer be changed.

Deadline controls use Dutch local time and the exact closing instant under GR-03 in [game rules](game-rules.md). The administrator can reopen entry by setting a new future deadline only while no stage result has been saved, under AD-25 in [administration](administration.md).

### Entry visibility

- UI-11: During the initial entry period, before its deadline is first reached, players cannot view other players' teams, finisher-count predictions, or classification predictions. Public access to standings and player-detail pages must not expose those entries during that period, even when they have already been finally submitted. A player retains access to their own entry through the authenticated entry workflow.
- UI-12: Once the entry deadline has first been reached, accepted, finally submitted teams, finisher-count predictions, and all three classification predictions are publicly visible without sign-in. They remain public during any administrator-authorized reopening. Use the latest successfully submitted version under GR-26 in [game rules](game-rules.md), not any later unsubmitted draft. A successful final submission during reopening updates the public version immediately; saving a draft does not.
- UI-13: Public access to standings, player details, and submitted predictions does not grant permission to edit entries or use administrative functions.

Reopening permits edits under AD-25 in [administration](administration.md) but does not restore the initial entry period's visibility restrictions.

### Player stage history

- UI-14: The public player-detail view provides a history of stages in the current edition. For each stage, show the player's points, applied reserve substitutions, and cumulative point total through that stage. Retain the breakdown by rider and scoring rule under SC-14 in [scoring](scoring.md). Make clear which stage a substitute starts scoring in under GR-14 in [game rules](game-rules.md). This history does not extend to previous annual editions, which are cleared under AD-12 in [administration](administration.md).

## Acceptance criteria

- The team-entry interface identifies when either defined rider-value budget is exceeded.
- At or after the entry deadline, the player can read their entry but cannot change it while entry is closed. An authorized reopening permits changes until the revised deadline.
- The available-rider list can be sorted independently by each of the three specified sort fields.
- Player entry before the deadline uses game rider numbers. Later database updates of official race bib numbers do not change the selected riders.
- Administrative stage and final-classification input identifies riders only by official race bib numbers under GR-28 in [game rules](game-rules.md).
- An import containing an invalid row is reported as rejected; the valid rows are not presented as successfully imported.
- If import is blocked because a player has already saved a team in the current edition, the interface explains that restriction to the administrator.
- A visitor can view published standings and open a player's public detail view without signing in; entry content respects UI-11–UI-13.
- The detail view attributes earned points to the contributing riders and scoring rules.
- Administrative workflows are restricted to the Administrator role.
- A player whose initial password has expired is directed to the administrator for a replacement; issuing a replacement starts a new password-change period and still requires a password change at first sign-in.
- A player who has forgotten their password is directed to the administrator; the temporary-password workflow follows AD-21 in [administration](administration.md).
- Initial and temporary passwords require a change within the fixed 12-hour period. A player-chosen password does not trigger a periodic expiry or renewal prompt under AD-26 in [administration](administration.md).
- An administrator who participates can use the player team-entry and prediction workflows in addition to the administrative workflows.
- The prediction interface provides a separate ordered list of five places for each classification defined in GR-20.
- Prediction rider selection includes participating riders outside the player's team, and prediction editing closes at the team-entry deadline.
- The prediction interface rejects duplicate riders within a single classification list, while allowing a rider to appear in different classification lists under GR-21.
- A player can save and reopen an incomplete team or incomplete prediction lists before the deadline; incomplete drafts are not presented as accepted participation entries.
- Saving a complete entry does not present it as finally submitted; the player must use "Definitief indienen".
- Final submission requires the finisher-count prediction as well as the complete valid team and all three complete classification prediction lists.
- When a player edits a previously submitted entry, the interface distinguishes the current draft from the accepted submitted version and makes clear that the draft must be finally submitted to replace that version.
- An incomplete draft after an earlier successful submission does not make the interface report that the player has lost their accepted participation entry.
- Starting a new annual edition is restricted to the Administrator role and follows AD-12 in [administration](administration.md).
- During the initial entry period, another player or a visitor cannot view a player's team or predictions, including already submitted versions.
- After the deadline, a visitor without an account can view a participant's accepted team, finisher-count prediction, and all three classification predictions.
- If submitted version A remains accepted while a later draft B was never submitted, other viewers see the permitted content from A, not B.
- During reopening, accepted version A remains publicly visible while draft B is edited. Successfully submitting B replaces the public content with B without waiting for the revised deadline.
- A visitor can inspect a player's points and cumulative total for each published stage in the current edition, together with the applied reserves.
- If a reserve replaces a withdrawn rider after stage 5, the history identifies that substitution and shows that the reserve starts contributing points in stage 6.
- The administrator can enter all three final classification top fives and the actual finisher count through the interface after stage 21; users without the Administrator role cannot use this workflow. A missing actual finisher count prevents saving and final calculation under AD-27.
- The administrator cannot proceed to the next stage until the preceding stage has been saved and successfully calculated.
- Invalid race bib numbers or incomplete final classification input prevent saving and calculation; saving complete valid input automatically starts the final calculation without another click.
- The stage-input screen does not allow saving incomplete stage drafts. This does not change players' ability to save incomplete entry drafts.
- After successful calculation or recalculation, the public standings and player stage details reflect the new result without requiring the administrator to publish separately.

## Design decisions and implementation details

No visual design, navigation layout, component library, API contract, or storage design is prescribed by this specification. These screens describe behavior, not a required one-to-one mapping to routes.

## Open decisions

The visibility and entry-workflow questions G-02, G-04, G-05, and G-08 are resolved, as are A-01–A-02, A-05–A-06, and A-08. Other remaining application decisions are tracked in [open questions](open-questions.md).
