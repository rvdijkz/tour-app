# Administration

Status: Approved — version 1.0.0, approved by the user on 2026-09-27. Source: [raw.txt](raw.txt). See [specification status](specification-index.md).

## Requirements

### Accounts and roles

- AD-01: The application supports Player and Administrator roles. An administrator may also participate as a player using the same account, with a team and predictions subject to the same entry, deadline, and scoring rules as other players.
- AD-02: Only an administrator can create a player through the user interface.
- AD-03: Creating a player creates a user account with a username and initial password. The administrator personally passes the initial password to the player; the application does not send it by email.
- AD-04: The player must change the initial password at first sign-in, within a fixed period of 12 hours. This period starts when the account is created, not at first sign-in. It is not configurable. If it expires before the password is changed, the initial password can no longer be used to activate the account; the administrator can issue a replacement under AD-20.
- AD-20: After the initial-password period expires, the administrator can issue a new initial password for the existing account and personally pass it to the player. Issuing the replacement starts a new full period of 12 hours, during which the player must change that password at first sign-in. The previous initial password is no longer valid. This does not require creating a new player account.
- AD-21: If an activated player forgets their password, the administrator issues a new temporary password for the same account and personally passes it to the player. Its fixed 12-hour validity period starts when it is issued, and the player must change it at the next sign-in. The previous password is no longer valid. If the temporary password expires before being changed, the administrator can issue another one under the same renewal rules as AD-20. Password recovery preserves the player's team, predictions, submitted entry, and scores.
- AD-26: A password chosen by the player does not expire with time and does not require periodic replacement. It remains valid until it is changed or replaced through password recovery under AD-21. The fixed 12-hour validity applies only to initial and temporary passwords, not to player-chosen passwords.

Initial administrator provisioning is a deployment detail deferred to the coding agent; it does not introduce public account registration.

### Rider administration

Rider import and subsequent rider-data updates must satisfy the number-uniqueness and rider-value rules in GR-05 in [game rules](game-rules.md), including direct database updates of official race bib numbers.

- AD-05: The administrator imports the list of participating riders through Excel before team selection. The initial import supplies the game rider numbers used by players and initializes each race bib number to the same value, as specified in GR-06 in [game rules](game-rules.md). Validate the entire import before applying it: if any row is invalid, reject the whole import, import no rows, and leave existing data unchanged. The application does not require manual rider-number entry through a screen for this initial setup. No pre-existing Excel layout must be supported; its column names and layout are deferred implementation details, based on the rider fields in GR-05. The official race bib number is initialized by the import and need not be supplied separately. Repeat imports are restricted by AD-19.
- AD-06: Once the official Tour numbers are available after the player-entry deadline, the administrator updates the race bib numbers directly in the database. This later update does not use an application screen or an additional Excel-import workflow. Game rider numbers and existing rider references must be preserved under GR-06 in [game rules](game-rules.md). Subsequent stage and final-classification result input uses the updated race bib numbers. This is an explicit operational requirement; no database schema or update script is prescribed here.
- AD-19: Once any player has saved a team in the current edition, further rider-list imports for that edition are blocked. A saved incomplete team draft also triggers this restriction; final submission is not required. A blocked import leaves the rider list and all player entries unchanged. This restriction does not prevent the direct database update of official race bib numbers under AD-06. The new edition's initial import remains supported after annual setup under AD-12 and AD-13.

### Configuration

The following settings are configurable:

- Team-entry deadline.
- Required nationality for team position 11, with the default defined in GR-27 in [game rules](game-rules.md).
- Combined reserve budget, with the default defined in GR-12 in [game rules](game-rules.md).
- Stages designated for double points in advance.

The administrator sets the entry deadline manually for every edition under AD-14. The race scope and combined budget for positions 1–11 are fixed under GR-01 and GR-11 in [game rules](game-rules.md); neither is an editable setting.

- AD-22: Only an administrator can configure the reserve budget, required nationality for position 11, entry deadline, and double-points stages through the application.
- AD-23: The reserve budget and required nationality become locked for the current edition as soon as the first player saves a team, including an incomplete draft. They cannot subsequently be changed during that edition, even before the entry deadline. The annual restart retains their values under AD-12, while the new edition's lock is triggered by its own first saved team.
- AD-24: All double-points stage designations must be set before the entry deadline. Once the deadline is reached, the current edition's designations are locked: the administrator cannot add, remove, or change them, including for stages not yet ridden.

- AD-25: The administrator may reopen entry after the deadline by setting a new future deadline, but only if no stage result has yet been saved in the current edition. Once any stage result has been saved, reopening is prohibited, even if it has not yet been calculated. Reopening permits player entry changes and final submission under GR-03 and GR-22–GR-26 in [game rules](game-rules.md) until the revised deadline. It does not reset existing accepted submissions or release the configuration locks already reached under AD-23 and AD-24. Accepted teams and predictions remain public during reopening under UI-12 in [user screens](user-screens.md).

The initial and temporary password validity period is fixed under AD-04, AD-20, and AD-21; no configuration control is provided for it.

### Stage results

- AD-07: After each stage, an administrator enters the information needed for scoring through the user interface, identifying riders exclusively by official race bib number under GR-28 in [game rules](game-rules.md). Stage input can only be saved when all required data is complete and valid; saving an incomplete stage draft is not supported. A stage without withdrawals or a most-combative award is still complete when those optional categories do not apply.

| Input | Source requirement |
| --- | --- |
| Stage finishers | Placings 1–20 |
| Last finishers | Last five places, with enough ordering information to distinguish last from the other four |
| Jersey classification leaders | Leaders of the yellow, polka dot, white, and green jersey classifications, as defined in [scoring](scoring.md) |
| Most combative rider | When present |
| Withdrawals | When present |

- AD-08: The administrator can invoke scoring for complete valid stage input to calculate the standings for all accepted participating teams according to [scoring](scoring.md). Participation requirements are defined in GR-24 in [game rules](game-rules.md).
- AD-09: A successful stage calculation immediately publishes the resulting standings, without a separate publication action. An unsuccessful calculation must not publish partial results or replace the last successfully published standings.
- AD-10: The administrator can correct the most recently entered stage result and invoke scoring again. Successful recalculation updates the points, affected withdrawals and reserve substitutions, player stage histories, and cumulative standings, and immediately publishes the corrected outcome under AD-09. Recalculation replaces the previous outcome for that stage rather than adding it again; effects no longer justified by the corrected input are removed. Saving a correction alone does not publish a new outcome. Support for correcting earlier stages is not specified.

- AD-15: The daily top 20 and last five in AD-07 are the finishing results of that individual stage. They are not the cumulative general classification. No additional daily top-20 or last-five general-classification input is required. Final classification input after stage 21 is defined separately in AD-11.
- AD-16: Stages must be entered and calculated in order, starting with stage 1. Before the administrator can enter the next stage, the preceding stage must have been saved with complete valid input and successfully calculated. A failed calculation does not allow progression to the next stage.

### Final classification results

- AD-27: After the final stage (stage 21), the administrator also manually enters the actual number of race finishers through the final-results interface. This count is required alongside the three final top fives for saving and calculating final results under AD-17 and supplies the final tie-breaker under SC-13 in [scoring](scoring.md). It must be a whole-number count. The administrator can correct it under the same validation, automatic recalculation, and publication workflow as AD-18. Annual setup under AD-12 clears this count with the other final results.

- AD-11: After the final stage (stage 21), the administrator manually enters the ordered final top five for all three classifications specified in GR-20 in [game rules](game-rules.md): general classification, points classification (green jersey), and mountains classification (polka dot jersey). This is additional input through the user interface, alongside the usual input after the last stage. These final results supply the prediction scoring under SC-16 and SC-17 in [scoring](scoring.md). The administrator may subsequently correct these final classifications under AD-18.
- AD-17: The final classification input can only be saved as a complete valid set of all three ordered top fives and the actual finisher count under AD-27. Validate the input before saving, including that each entered race bib number identifies a participating rider in the current edition. Incomplete or invalid input is not saved and does not trigger calculation. After successful validation and saving, immediately calculate prediction points and final game scores, including the final tie-breaker; no separate calculation action is required. A successful calculation immediately publishes the final standings. A failed calculation leaves the last successfully published standings unchanged rather than publishing partial final results.

- AD-18: The administrator may correct saved final classifications or the actual finisher count. A correction must leave the complete set of all three top fives and the actual finisher count valid under AD-17 before it can be saved. Successfully saving a valid correction automatically recalculates prediction points, final game scores, and final ranks, then immediately publishes the corrected final standings if calculation succeeds. Recalculation replaces the prior prediction-point outcome rather than adding points again. Invalid corrections are not saved; unsuccessful calculations leave the last successfully published standings unchanged.

### Annual game setup

- AD-12: The administrator manually starts a new annual Tour edition. This clears the previous edition's teams (including drafts and submitted versions), finisher-count and classification predictions, stage and final classification results, and standings. Existing player accounts remain available, and configuration values are retained rather than reset to defaults. Earlier submissions do not count as participation in the new edition: players submit a new entry under GR-24–GR-26 in [game rules](game-rules.md). Annual rider-list preparation and deadline setup follow AD-13 and AD-14.
- AD-13: For every annual edition, the administrator imports a complete new participating rider list from Excel before team selection. This replaces the previous edition's participating rider list; the previous list is not reused as the current edition's list. Repeat imports within an edition are subject to AD-19.
- AD-14: The administrator manually sets the entry deadline for each edition, including the first edition, as part of game setup. Retaining configuration during the annual restart does not remove this yearly setup requirement. The deadline applies to all required entry data and final submission under GR-03–GR-04 and GR-22–GR-25 in [game rules](game-rules.md), using Dutch local time and the exact cutoff in GR-03. Reopening after expiry is restricted by AD-25.

## Acceptance criteria

- A player without the Administrator role cannot create another player, import riders, or enter stage results.
- A newly created account requires an initial-password change at first sign-in.
- An account created at 09:00 has an initial-password deadline 12 hours later, even if the player first signs in later than 09:00.
- If the initial password has expired without being changed, it cannot be used to activate the account. The administrator can issue a replacement for that same account with a new 12-hour period.
- After replacement, the old initial password is invalid and the replacement must be changed at first sign-in within its new validity period.
- When the administrator resets a forgotten password, the existing password no longer works and the new temporary password must be changed at the next sign-in within 12 hours of issuance. Existing game entries and scores remain unchanged.
- A player-chosen password remains valid after 12 hours and in subsequent annual editions unless it has been changed or reset; elapsed time alone does not require replacement.
- An administrator can enter and finally submit their own team and predictions using the player workflow; the same completeness rules and deadline apply.
- A player without the Administrator role cannot change the reserve budget, required nationality, entry deadline, or double-points stage designations.
- Once any team draft has been saved in the current edition, attempts to change the reserve budget or required nationality are rejected.
- After the entry deadline, an attempt to designate a future stage for double points, or remove an existing designation, is rejected.
- Importing a rider with game rider number 42 initializes their race bib number to 42 rather than leaving it empty.
- An Excel import with valid rows and at least one invalid row is rejected entirely; none of its rows are imported and existing rider data is unchanged.
- An import containing duplicate game rider numbers, a fractional rider value, or a rider value outside the inclusive range 2–86 is rejected entirely under AD-05 and GR-05 in [game rules](game-rules.md).
- If a player has saved an incomplete team draft in the current edition, a subsequent rider-list import is rejected without modifying riders or player entries.
- After an annual restart clears previous team entries, the previous edition's saved teams do not block the new edition's initial rider import.
- After that rider's race bib number is updated to 117 directly in the database, result entry using 117 resolves to the same rider already selected in teams and predictions; the game rider number remains 42.
- After that update, entering 42 as a result number must not select that rider via their initial number. It identifies another rider only if that rider currently has official race bib number 42; otherwise it is invalid.
- The stage-entry interface supports all input categories listed above, including a stage without a most-combative award or withdrawals.
- The most recently entered stage result can be edited by an administrator.
- An attempt to save stage input with missing required results is rejected; there is no saved incomplete stage draft.
- A successful stage calculation makes the new standings public without a second publication action.
- If a corrected withdrawal is removed from the latest stage and scoring is rerun, the associated withdrawal points and substitution are revised according to the corrected result, and the updated outcome is immediately public.
- Recalculating unchanged stage input does not duplicate points or consume an additional reserve.
- If calculation fails, the last successfully published standings remain available without partial updates from the failed calculation.
- Jersey inputs allow the same rider to be selected as the leader of multiple classifications, in accordance with SC-09 in [scoring](scoring.md).

- After an administrator starts a new annual edition, previous teams, predictions, results, and standings are absent, including previously submitted entries.
- Existing player accounts remain available after the annual restart; players do not need newly created accounts to enter the new game.
- A configured reserve budget or required nationality retains its value after the annual restart rather than reverting to its default.
- A user without the Administrator role cannot start a new annual edition.
- Each new edition uses the complete rider list imported for that edition, rather than the previous year's participating rider list.
- The administrator can manually set the current edition's deadline, which governs both player edits and final submission.
- The ranked riders entered for each stage are that stage's top 20 and last five finishers, even when their positions in the cumulative general classification differ.
- After stage 21, the administrator can manually enter five ordered riders for each of the general, points, and mountains final classifications, in addition to the usual stage input.
- The administrator cannot enter stage 2 before stage 1 has been successfully saved and calculated. The same ordering rule applies to each following stage.
- An attempt to save final classification input with a missing place or a race bib number that does not identify a current-edition participant is rejected, and no final calculation is triggered.
- Saving all three complete valid final top fives together with the actual finisher count automatically calculates prediction points and the final standings, then immediately publishes them on calculation success without another administrator action.
- Missing the actual finisher count prevents saving final results and triggering final calculation, even when all three final top fives are complete.
- Correcting only the actual finisher count automatically recalculates and publishes the final ranks on success, without changing or duplicating prediction points.

- After the final standings have been published, saving a valid change to a final classification automatically updates the affected prediction points and final ranks and publishes the corrected standings, without separate calculation or publication actions.
- Saving the same valid final classification results again does not duplicate prediction points.
- A correction containing an invalid race bib number is rejected without changing the saved final classifications or published standings.

- If the deadline has passed and no stage result has been saved, the administrator can set a new future deadline and reopen player entry until that time.
- If stage 1 has been saved but not yet calculated, an attempt to reopen entry is rejected.
- Reopening entry preserves accepted submissions and the already locked reserve budget, nationality, and double-points designations.

## Open decisions

See A-03 in [open questions](open-questions.md). G-02–G-03, G-08, A-01–A-02, and A-04–A-08 are resolved by user confirmation. The nationality code set remains open. Stage processing and publication are defined in AD-07–AD-10 and AD-16. Final-classification input, corrections, and automatic calculation and publication are defined in AD-11 and AD-17–AD-18. No low-level implementation is prescribed.
