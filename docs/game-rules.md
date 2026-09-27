# Game Rules

Status: Approved — version 1.0.0, approved by the user on 2026-09-27. Source: [raw.txt](raw.txt). See [specification status](specification-index.md).

## Terminology

| Term | Meaning |
| --- | --- |
| Player | A game participant with a user account who selects a team |
| Administrator | A user with administrative permissions who may also participate as a player under AD-01 in [administration](administration.md) |
| Rider | A cyclist participating in the cycling race |
| Team | A player's ordered selection of riders |
| Team position | A rider's numbered slot in a player's team, from 1 to 14 |
| Stage placing | A rider's finishing position in a stage |
| Game rider number (initial rider number) | The initial number imported from Excel and used by players to select riders before the entry deadline |
| Race bib number (official rider number) | The number used to identify riders in race-result input; initially copied from the game rider number and later updated to the official Tour number |
| Rider value | The cost of selecting a rider; distinct from earned game points |
| Withdrawal | A rider leaving the cycling race |

## Requirements

### Game and entries

- GR-01: The application supports only the Tour de France game. Race selection and support for Giro or Vuelta are out of scope, superseding the configurable-race scope in the original description. Each year, the administrator manually starts a new edition and clears the previous teams, predictions, results, and standings; historical editions are not available to browse. Existing player accounts and configuration values are retained, as specified in AD-12 in [administration](administration.md).
- GR-02: A player supplies their own name and a team name.
- GR-03: A player can change their team entry until the configured deadline, expressed in Dutch local time (Europe/Amsterdam), including daylight-saving changes. Entry closes exactly at that instant: saving or final submission is permitted only before the deadline, and is rejected at or after it. Closed entries are read-only unless the administrator reopens entry under AD-25 in [administration](administration.md). Draft saving, participation, and submitted-version requirements are defined in GR-23–GR-26.
- GR-04: Alongside the team, the player must enter the predicted number of riders who will finish the Tour de France before the same entry deadline under GR-03. This prediction is required for participation. The source describes this as reaching Paris after 21 stages.

### Rider data

- GR-05: Each rider has a name, game rider number, race bib number, rider value, and nationality code. Within an edition, game rider numbers must be unique among riders, and race bib numbers must separately be unique among riders. Uniqueness is checked within each number type, not across the two types. Rider values must be whole numbers from 2 through 86 inclusive.
- GR-06: At the initial Excel import, each rider's race bib number is set equal to their game rider number. The official Tour numbers become available after the player-entry deadline and are then updated by the administrator directly in the database under AD-06 in [administration](administration.md). The game rider number remains unchanged. Updating the race bib number must preserve the rider's identity and all references from saved drafts, submitted teams, and classification predictions; entries remain linked to the same rider.
- GR-07: A nationality code is three alphanumeric characters. The permitted code set is unresolved under A-03.
- GR-08: A rider can withdraw during the race.
- GR-28: Game rider numbers are used only for player entry before the deadline. After the deadline, administrative stage-result entry uses only official race bib numbers, including for finishing places, jersey leaders, withdrawals, and the most combative rider. Final-classification result entry also uses official race bib numbers. An initial number must not be used as an alternative lookup for administrative result entry.

### Team composition

| Team position | Role | Selection constraint |
| --- | --- | --- |
| 1–10 | Standard rider | Combined budget with position 11 |
| 11 | Nationality rider | Initial selection must have the configured nationality; combined budget with positions 1–10. For substitutions, see GR-16. |
| 12 | Reserve 1 | Combined configurable reserve budget with position 13 |
| 13 | Reserve 2 | Combined configurable reserve budget with position 12 |
| 14 | Kluns rider | No rider-value limit |

- GR-09: A complete team contains 14 riders in the positions above. The Dutch role name "kluns" is retained as a domain term for its special scoring role.
- GR-10: A rider may occur only once in a team, including reserve and kluns positions.
- GR-11: The sum of rider values in positions 1–11 must not exceed 100. This combined budget is fixed and is not configurable.
- GR-12: The sum of rider values in positions 12–13 must not exceed the configurable reserve maximum. Its default value is 20.
- GR-27: The required nationality for the initial rider in team position 11 is configurable and defaults to French. The substitution exception in GR-16 remains applicable.

Configuration permissions and the locking of the reserve budget and required nationality after the first saved team are defined in AD-22 and AD-23 in [administration](administration.md).

### Substitutions

- GR-13: Withdrawn riders in positions 1–11 are replaced after each stage by available reserves, using Reserve 1 before Reserve 2. When multiple riders withdraw in the same stage and both reserves are available, Reserve 1 takes the lowest-numbered withdrawn team position, followed by Reserve 2 taking the next lowest-numbered position. Substitutes remain in their assigned positions when further withdrawals occur in later stages; they are not moved to a lower-numbered position.
- GR-14: A substitute starts contributing points from the stage following the substitution, including eligibility for jersey points under SC-09 in [scoring](scoring.md).
- GR-15: A reserve who withdraws before entering the active team is no longer available for substitution. If Reserve 1 has withdrawn before activation, available Reserve 2 takes the next substitution instead.
- GR-16: A reserve of any nationality can replace the rider in position 11 and inherits that position's special scoring under SC-02 in [scoring](scoring.md). The configured nationality requirement applies only to the initial selection for position 11.
- GR-17: A kluns rider who withdraws is not replaced.
- GR-18: If an activated reserve subsequently withdraws, the remaining available reserve can replace that rider, subject to the substitution order and timing in GR-13 and GR-14.
- GR-19: If no reserve is available to replace a withdrawn rider in positions 1–11, the position remains empty and contributes no points in subsequent stages. The withdrawal-stage rules still apply as defined in [scoring](scoring.md).

### Final classification predictions

- GR-20: In addition to the team entry and finisher-count prediction, each player must submit an ordered prediction of places 1–5 for each of three final classifications: general classification, points classification (green jersey), and mountains classification (polka dot jersey).
- GR-21: These are separate prediction lists, not additional team positions. Players may select from all participating riders, including riders outside their team. Each rider may appear at most once within a classification's prediction list, but may appear in multiple classification lists. Prediction scoring is defined in SC-16 and SC-17 in [scoring](scoring.md).
- GR-22: Final classification predictions use the same submission and editing deadline as the team entry under GR-03, including any permitted reopening under AD-25 in [administration](administration.md). When entry is closed, predictions are read-only. Draft saving and participation requirements follow GR-23 and GR-24.

### Drafts and participation

- GR-23: Before the entry deadline, players may save an incomplete team or incomplete final classification predictions as a draft and return later to complete them. Saving an incomplete draft does not make it an accepted entry for participation.
- GR-24: Participation requires a complete valid team under GR-09–GR-12, the finisher-count prediction under GR-04, and all three complete final classification prediction lists under GR-20 and GR-21 by the deadline, together with final submission under GR-25. A player without an accepted submitted version is not eligible for participation and is not scored or included as a participant in the standings. An incomplete or unsubmitted draft does not invalidate an earlier accepted submitted version under GR-26.
- GR-25: The player must explicitly click the final submission action ("Definitief indienen") before the entry deadline. Saving a complete valid entry alone does not submit it. Final submission is accepted only when the required entry data is complete and satisfies the entry rules. Each successful final submission replaces the previously accepted version with the complete newly submitted entry.
- GR-26: After final submission, a player may edit and save a new draft before the deadline while the previously accepted submitted version remains valid. Draft changes, including changes to the team, finisher-count prediction, and classification predictions, have no effect on that accepted version until the player successfully submits the complete revised entry again. At the deadline, the latest successfully submitted version is used for participation and scoring; unsubmitted draft changes are ignored. A failed submission does not replace the previously accepted version.

During an administrator-authorized reopening under AD-25, these same draft and final-submission rules apply until the revised deadline. Earlier accepted versions remain valid unless replaced by a successful new submission.

Entry visibility before and after the deadline is defined in UI-11–UI-13 in [user screens](user-screens.md).

## Acceptance criteria

- Within an edition, two riders cannot share a game rider number or share a race bib number. A game rider number may equal a race bib number because the two number types have separate uniqueness rules.
- Rider values of 2 and 86 are valid; values of 1, 87, or 2.5 are invalid. These per-rider limits also apply to reserves and the kluns; team budget rules remain separate.
- A team containing the same rider in two positions fails the complete-team rules.
- An initial complete team entry with a position-11 rider of a different nationality fails the selection rules. This restriction does not prevent a later substitution under GR-16.
- With the default nationality setting, the initial rider in position 11 must be French. If the setting is changed, the initial rider must match the configured nationality instead.
- A team whose positions 1–11 exceed their combined budget fails the budget rules; the same applies independently to positions 12–13.
- A total rider value of 100 for positions 1–11 satisfies that budget rule; a total of 101 fails it.
- With the default reserve budget, a combined value of 20 for positions 12–13 satisfies that budget rule and 21 fails it. Changing the reserve budget does not change the fixed budget for positions 1–11.
- The kluns rider's value is not included in either constrained budget.
- A player cannot edit their entry at or after the deadline while entry is closed; an authorized reopening permits changes until the revised deadline.
- A rider imported with game rider number 42 initially also has race bib number 42. When the administrator later changes that rider's race bib number to 117, the game rider number remains 42 and existing teams and predictions still reference the same rider.
- A reserve who has withdrawn before activation cannot subsequently replace a rider.
- If team positions 3 and 8 withdraw in the same stage and both reserves are available, Reserve 1 replaces position 3 and Reserve 2 replaces position 8. Their scoring starts in the following stage under GR-14.
- A withdrawn kluns rider is not replaced, even when a reserve remains available.
- If Reserve 1 replaces position 8 after one stage and position 3 withdraws in a later stage, Reserve 1 remains in position 8 and available Reserve 2 replaces position 3.
- If Reserve 1 has withdrawn before activation, available Reserve 2 replaces the next eligible withdrawn rider.
- A reserve whose nationality differs from the configured nationality can replace position 11 and uses that position's scoring rules once active.
- If Reserve 1 is active in position 8 and subsequently withdraws while Reserve 2 remains available, Reserve 2 replaces that rider in position 8 and starts scoring in the following stage.
- If a rider in position 3 withdraws with no available reserves, position 3 remains empty and contributes no points in subsequent stages.
- A complete set of final classification predictions contains three ordered lists, each covering places 1–5 of its respective classification.
- A player can select a participating rider outside their team for a final classification prediction.
- At or after the team-entry deadline, a player cannot enter or change final classification predictions unless entry has been reopened under AD-25.
- A prediction list containing the same rider in two places is invalid.
- The same rider may be included once in each of the three prediction lists.
- Before the deadline, a player can save a team containing fewer than 14 riders and return to complete it.
- Before the deadline, a player can save partially filled prediction lists and return to complete them.
- A player with no earlier accepted submitted version whose team is incomplete at the deadline is not accepted for participation, even if all prediction lists are complete.
- A player with no earlier accepted submitted version who has a complete team but an incomplete prediction list at the deadline is not accepted for participation and receives no partial prediction score.
- Final submission fails if the finisher-count prediction is missing, even when the team and all classification predictions are complete.
- A player who only saves a complete valid entry and never finally submits it before the deadline is not accepted for participation.
- A complete valid entry submitted using "Definitief indienen" before the deadline is accepted for participation.
- A player submits version A, then saves an incomplete draft B without resubmitting it before the deadline: version A remains valid and is used for participation and scoring.
- A player submits version A, then successfully submits a complete valid version B before the deadline: version B replaces version A for participation and scoring.
- A player submits version A, then attempts to submit invalid version B: submission of B fails and version A remains accepted.

## Open decisions

See A-03 in [open questions](open-questions.md). G-01–G-03, G-06–G-08, A-07, and R-01–R-03 are resolved by user confirmation. Annual rider-list preparation and deadline setup are defined in AD-13 and AD-14 in [administration](administration.md). No further business assumptions or implementation details are prescribed.
