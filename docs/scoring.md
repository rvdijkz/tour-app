# Scoring and Ranking

Status: Approved — version 1.0.0, approved by the user on 2026-09-27. Source: [raw.txt](raw.txt). See [specification status](specification-index.md).

## Requirements

### Stage placing points

- SC-01: Standard riders in team positions 1–10 earn points according to their finishing place in the individual stage, using the table below. Cumulative general-classification positions do not determine these stage placing points.
- SC-02: The rider in team position 11 uses the same scale through stage placing 8, and earns 3 points for each stage placing from 9 through 20. This also applies to a reserve who replaces that rider, regardless of the reserve's nationality, under GR-16 in [game rules](game-rules.md).

| Stage placing | Team positions 1–10 | Team position 11 |
| --- | --- | --- |
| 1 | 12 | 12 |
| 2 | 10 | 10 |
| 3 | 8 | 8 |
| 4 | 7 | 7 |
| 5 | 6 | 6 |
| 6 | 5 | 5 |
| 7 | 4 | 4 |
| 8 | 3 | 3 |
| 9 | 2 | 3 |
| 10 | 1 | 3 |
| 11–20 | No placing points | 3 |
| Beyond 20 | No placing points | No placing points |

### Position bonus

- SC-03: A rider in team positions 1–10 earns an additional 2 points when their team position exactly matches their stage placing.

### Withdrawals and reserves

- SC-04: A withdrawal from team positions 1–11 incurs a one-time 2-point deduction in the withdrawal stage. This also applies to an activated reserve occupying one of those positions.
- SC-05: A reserve who withdraws before activation incurs no withdrawal deduction.
- SC-06: Substitution timing and availability follow [game rules](game-rules.md). An active rider in positions 1–11 who withdraws during a stage earns no points for that stage, including placing points, position bonuses, jersey points from the preceding stage, and most-combative points. The withdrawal-stage contribution is therefore exactly -2 points under SC-04, including for an activated reserve and on double-points stages.

### Double-points stages

- SC-07: A stage can be designated in advance as a double-points stage, subject to the administrator permissions and entry-deadline lock in AD-22 and AD-24 in [administration](administration.md). Only stage placing points (SC-01 and SC-02) for team positions 1–11 are doubled. Position bonuses, jersey points, most-combative points, and withdrawal deductions retain their normal values. Kluns scoring is unchanged.

### Jerseys

- SC-08: Jersey points apply to team positions 1–11 according to the following table.

| Jersey | Points |
| --- | --- |
| Yellow | 3 |
| White | 1 |
| Green | 1 |
| Polka dot | 1 |

- SC-09: The classification leaders recorded after stage X generate jersey points in stage X+1. Stage 1 awards no jersey points, and jerseys awarded after the final stage generate no points. Points belong to the classification leaders, not to other riders physically wearing the jerseys. If a rider leads multiple jersey classifications, the corresponding points are added together. Eligibility uses the active team positions 1–11 for the stage receiving the points: a reserve entering the active team for stage X+1 can immediately receive jersey points from classifications led after stage X. Riders withdrawing during the scoring stage receive no jersey points, as specified in SC-06.

### Most combative rider

- SC-10: The most combative rider earns 5 points when in team positions 1–10. The award may be absent from a stage.

### Kluns rider

- SC-11: Team position 14 uses the following scoring rules.

| Stage result | Points |
| --- | --- |
| Last | 5 |
| Second-last through fifth-last | 2 |
| Placing 11–20 | -5 |
| Placing 2–10 | -10 |
| Placing 1 | -15 |
| Outside the top 20 and outside the last five | 0 |
| Withdrawal | 5 |

A kluns withdrawal awards 5 points for the withdrawal event. The kluns is not replaced, as specified in GR-17 in [game rules](game-rules.md).

Scope decision: overlap between the top 20 and last five in a stage with very few finishers is excluded from the supported game scenarios by user decision. No precedence or combined-scoring rule is required for this exceptional situation.

### Standings and final ranking

- SC-12: After each stage, all accepted participating teams under GR-24 in [game rules](game-rules.md) are scored using the entered results and the standings are calculated. Players with equal points share a rank in intermediate standings; the finisher prediction is not used to break intermediate ties.
- SC-13: At the final ranking, equal game scores are resolved using the predicted number of race finishers: the player whose prediction has the smallest absolute difference from the actual number ranks highest among the tied players. Players with equal game scores and equal prediction distances share a rank; there is no further tie-breaker.

The actual finisher count used by SC-13 is entered by the administrator under AD-27 in [administration](administration.md).
- SC-14: The player detail view must explain points earned by rider and scoring rule; presentation is specified in [user screens](user-screens.md).
- SC-15: The final game score is the sum of all stage scores plus the final classification prediction points under SC-16 and SC-17. These predictions are the only additional points awarded after the race. The final tie-breaker in SC-13 applies to the resulting final game scores.

### Final classification prediction points

- SC-16: After the final stage, evaluate each player's predictions under GR-20 in [game rules](game-rules.md) against the actual final top five of the corresponding classification. Each predicted rider who finishes in that classification's top five earns 3 base points, even if the predicted place is incorrect. This amount is the same for all five places and all three classifications.
- SC-17: If that rider also finishes in the exact predicted place within the corresponding classification, award 1 additional bonus point on top of the base points. Evaluate each classification independently: the same rider may earn prediction points in multiple classifications when included in the corresponding prediction lists. A result in one classification does not earn points for a prediction in another classification.

These awards use the separately submitted predictions and final classification results. They are distinct from the daily jersey points in SC-08 and SC-09; those daily rules remain unchanged. Predicted riders need not belong to the player's team, as defined in GR-21 in [game rules](game-rules.md).

For a single prediction:

| Actual result in the predicted classification | Prediction points |
| --- | --- |
| Top five, but a different place | 3 |
| Exact predicted place | 4 (3 base points + 1 bonus point) |
| Outside the top five | No points under SC-16 or SC-17 |

## Acceptance criteria

Unless explicitly stated otherwise, these examples concern individual scoring components on ordinary stages. They do not resolve open interactions between rules.

- A standard rider winning the stage receives 12 placing points on an ordinary stage, regardless of their cumulative general-classification position.
- A rider in team position 4 finishing fourth receives 7 placing points and a 2-point position bonus.
- A rider in team position 11 finishing tenth receives 3 placing points and no position bonus.
- A standard rider finishing eleventh receives no placing points.
- A most combative rider in team position 11 receives no most-combative bonus.
- An unused reserve's withdrawal causes no withdrawal deduction.
- A kluns rider finishing first receives -15 kluns points; a kluns rider finishing last receives 5 kluns points.
- Among players tied on game points with predictions of 150 and 153, an actual finisher count of 151 ranks the prediction of 150 ahead.
- On a double-points stage, a rider in team position 4 finishing fourth receives 14 placing points and a 2-point position bonus, totaling 16 for these two components.
- On a double-points stage, a position-11 rider finishing tenth receives 6 placing points. A qualifying most-combative award remains 5 points, and an active rider's withdrawal deduction remains 2 points.
- An eligible rider leading both the yellow and white jersey classifications after stage X earns 4 jersey points in stage X+1, even if X+1 is a double-points stage. A different rider wearing the white jersey on behalf of that leader receives no white-jersey points.
- Stage 1 has no jersey points; jersey classifications recorded after the final stage add no points to the final score.
- A reserve who leads the yellow jersey classification after stage 5 and enters the active team for stage 6 receives 3 jersey points in stage 6, provided the rider does not withdraw during that stage.
- An active rider who led the yellow jersey classification after stage 5 but withdraws during stage 6 receives no jersey points in stage 6. The withdrawal deduction under SC-04 still applies.
- A kluns rider finishing tenth receives -10 kluns points on both ordinary and double-points stages.
- A kluns rider finishing outside the top 20 and outside the last five receives 0 points.
- A kluns rider's withdrawal awards 5 points on both ordinary and double-points stages.
- A reserve of a different nationality replacing team position 11 receives 3 placing points for finishing twentieth on an ordinary stage once active.
- Players tied on final game points with predictions of 150 and 152 share a rank when the actual finisher count is 151. Players with equal game points and identical predictions also share a rank.
- Players with equal points share an intermediate rank even when their finisher predictions differ.
- An activated reserve in position 8 who withdraws contributes exactly -2 points in that stage, even on a double-points stage. The same withdrawal does not incur deductions in later stages.
- A rider predicted second in the final general classification who finishes fourth earns 3 prediction points, without the exact-place bonus.
- A rider predicted third in the final mountains classification who finishes third earns 4 prediction points (3 base points plus 1 bonus point).
- A rider predicted in the green jersey classification who finishes outside its top five earns no points for that prediction, even if the rider finishes in the general classification's top five.
- A rider outside the player's team who is correctly predicted fifth in the final green jersey classification earns 4 prediction points.
- If the same rider is predicted first in both the general and mountains classifications and finishes first in both, the player earns 8 prediction points for those two predictions.
- A completely correct ordered top five earns 20 prediction points. Three completely correct lists earn 60 prediction points in total, the maximum additional score.

## Open decisions

S-01–S-07, G-06, A-06, and R-01–R-03 in [open questions](open-questions.md) are resolved by user confirmation, including the explicit scope exclusion under SC-11. Final-result input, corrections, and recalculation are defined in AD-11, AD-17–AD-18, and AD-27 in [administration](administration.md).
