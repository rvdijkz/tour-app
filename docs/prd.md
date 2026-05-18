# PRD.md - Product Requirements Document

## Tour Game Web Application

### 1. Executive Summary

The Tour Game is a web-based fantasy cycling competition platform where players build and manage cycling teams based on real-world cycling tour participants (e.g., Tour de France, Giro d'Italia, Vuelta a España). Players earn points based on actual stage performances, jersey wins, and special achievements. The application manages team composition, scoring calculations, and competition standings.

---

### 2. Product Overview

#### 2.1 Purpose

To provide an engaging fantasy cycling game where players:

- Build and manage strategic cycling teams
- Compete against other players based on real cycling tour results
- Track performance through an interactive leaderboard

#### 2.2 Target Users

- **Players**: Cycling enthusiasts aged 18+ who want to participate in the fantasy competition
- **Administrators**: Tournament organizers who manage cyclist data and stage results

#### 2.3 Core Features

- Cyclist roster management (admin)
- Team composition and validation
- Daily stage result entry (admin)
- Automated scoring calculation
- Real-time standings and player statistics
- User authentication and role-based access

---

### 3. Functional Requirements

#### 3.1 User Management

##### 3.1.1 Authentication

- Login page with username and password
- Session management
- Password reset requirement on first login (12 hours configurable)

##### 3.1.2 Player (Speler)

- Players enter personal name and team name
- Players have username and password
- Initial password provided by admin, must change on first login
- Default role: Player

##### 3.1.3 Admin User

- Admin role manages:
  - Importing cyclist roster
  - Entering stage results and scores
  - Adding new player accounts
  - Configuring game parameters

##### 3.1.4 Player Creation (Admin Only)

- Admin creates new player account
- Initial password auto-generated
- Player must change password within 12 hours of first login
- Time limit is configurable

---

#### 3.2 Cyclist (Renner) Management

##### 3.2.1 Cyclist Properties

- Name (required)
- Game number/jersey number (required, unique)
- Tour cyclist number (initially empty, populated before tour starts)
- Points value (numeric, required)
- Country code (3-character alphanumeric, required)
- Status: Active, Dropped Out

##### 3.2.2 Cyclist Import (Admin Only)

- Admin imports cyclist roster via Excel upload
- Required fields: Name, Game Number, Points Value, Country Code
- Optional fields: Tour Cyclist Number (populated closer to tour start)
- System validates for duplicates and required fields
- Imported data replaces or updates existing roster

##### 3.2.3 Cyclist Dropout

- Cyclists can be marked as dropped out during the tour
- Dropouts trigger team adjustments (reserve activation)
- Historical dropout data preserved for scoring calculations

---

#### 3.3 Team Management

##### 3.3.1 Team Composition

A team consists of 14 cyclists in the following positions:

| Position | Count | Role                   | Special Rules                             |
| -------- | ----- | ---------------------- | ----------------------------------------- |
| 1-10     | 10    | Standard Cyclists      | Standard scoring rules                    |
| 11       | 1     | Nationality Specialist | Specific country (configurable)           |
| 12       | 1     | Reserve 1              | Replaces first dropout in positions 1-11  |
| 13       | 1     | Reserve 2              | Replaces second dropout in positions 1-11 |
| 14       | 1     | Kluns                  | Unique scoring rules, no point limit      |

##### 3.3.2 Team Point Budget (FLEXIBLE SYSTEM)

Teams have a flexible two-tier budget system:

**Tier 1: Standard Pool** (Positions 1-10 + Nationality Specialist at Position 11)

- Maximum configurable points (default: **100 points**)
- All cyclists in positions 1-11 must be acquired within this budget
- Example: A strong 35-point elite cyclist + 7 solid riders in positions 1-11

**Tier 2: Reserve Pool** (Positions 12-13 Reserve Cyclists)

- Base maximum configurable points (default: **20 points**)
- **FLEXIBLE**: Automatically increased by any unused Standard Pool points
- Allows strategic flexibility: save on Standard Pool → more Reserve budget

**Tier 3: Kluns** (Position 14)

- No point limit
- Can select any cyclist regardless of cost

**BUDGET OVERFLOW MECHANISM:**

When a player has unused Standard Pool points, those are automatically available for Reserve Pool:

Available Reserve Budget = Base Reserve Budget + Unused Standard Pool Points

Example:
├── Standard Pool Budget: 100 points
├── Spent on Positions 1-11: 70 points
├── Overflow Available: 30 points
├── Base Reserve Budget: 20 points
├── Total Reserve Budget: 20 + 30 = 50 points available for positions 12-13
└── Player can select reserves worth up to 50 points

**VALIDATION RULES:**

1. ✅ All positions 1-11 must be filled with total ≤ Standard Pool Budget (100 points)
2. ✅ All positions 12-13 must be filled with total ≤ (20 + Overflow)
3. ✅ Position 14 (Kluns) has no restrictions
4. ✅ Each cyclist appears only once in team
5. ❌ Cannot exceed either budget tier

**STRATEGIC IMPLICATIONS:**

- **Conservative Team**: Spend 70 on positions 1-11 → 50 points available for reserves
- **Balanced Team**: Spend 85 on positions 1-11 → 35 points available for reserves
- **Elite Team**: Spend 100 on positions 1-11 → 20 points available for reserves (base only)

This system rewards budget discipline while maintaining flexibility for team strategy.

##### 3.3.3 Team Constraints

- Each cyclist can appear only once in a team
- Nationality specialist must match configured country
- All 14 positions must be filled
- Player must specify expected number of finishers (1-14)

##### 3.3.4 Team Submission

- Players can modify teams until configurable deadline
- After deadline: Teams become read-only
- Deadline is typically 2 hours before tour start

##### 3.3.5 Tour Finisher Prediction

Players must predict the total number of cyclists that will finish the entire tour:

**Input:**

- Each player specifies an expected number of total finishers
- This prediction is made at team submission (before tour starts)
- Cannot be changed after tour has started (team is LOCKED)
- NOTE: This is a prediction of the TOTAL TOUR, not just their team's cyclists

**Purpose:**

- Tiebreaker mechanism for final standings.
- Rewards players who accurately predict tour difficulty/completion rate

**Example:**
Tour starts with: 176 cyclists
Player A predicts: 145 cyclists will finish
Player B predicts: 148 cyclists will finish
Actual result: 145 cyclists finish the tour
Result: Player A had the best prediction and gains +1 tiebreaker point

---

#### 3.4 Stage Results and Scoring

##### 3.4.1 Stage Result Entry (Admin Only)

Admin enters data for each completed stage:

1. **Finish Positions**: Cyclist tour numbers for positions 1-20
2. **Jersey Winners**: Yellow, White (young rider), Green (points), Polka Dot (mountain)
3. **Combative Cyclist**: Tour number of most combative cyclist (if awarded)
4. **Dropouts**: Tour numbers of cyclists who dropped out during stage

##### 3.4.2 Scoring Rules

**Base Stage Points** (Positions 1-10 in team):
| Stage Position | Points |
|---|---|
| 1 | 12 |
| 2 | 10 |
| 3 | 8 |
| 4 | 7 |
| 5 | 6 |
| 6 | 5 |
| 7 | 4 |
| 8 | 3 |
| 9 | 2 |
| 10 | 1 |

**Position 11 Scoring** (Nationality Specialist):

- Positions 1-8: Same as above
- Positions 9-20: 3 points each
- Positions 21+: 0 points

**Bonus Points**:

- Position Bonus: +2 points if team position equals stage position (positions 1-11 only)

**Dropout Penalties** (Positions 1-11):

- Cyclist drops out before reserve activation: -2 points
- Reserve drops before activation: 0 points
- Reserve activation: Applied next stage

**Reserve Activation**:

- Reserves replace dropouts in positions 1-11
- If multiple dropouts occur in positions 1-11 in the same stage, replacements are assigned in ascending team position order (lowest team position number first)
- Reserve 1 replaces the first eligible dropout by that order
- Reserve 2 replaces the next eligible dropout by that order
- A reserve cyclist who drops out before activation is never activated and does not trigger dropout penalty before activation
- Reserves don't score for the stage they're activated; start scoring next stage

**Jersey Bonuses** (Positions 1-11 only, applied next stage):

- Yellow Jersey: +3 points
- White Jersey: +1 point
- Green Jersey: +1 point
- Polka Dot Jersey: +1 point

**Combative Cyclist Bonus** (Positions 1-10 only):

- +5 points if held in team

**Kluns (Position 14) Special Scoring**:
| Stage Finish | Points |
|---|---|
| Last (final position) | +5 |
| 2nd-5th last | +2 |
| 11-20 | -5 |
| 2-9 | -10 |
| 1st | -15 |

**Double Points Stages**:

- Certain stages (configurable) award double points
- All points (except kluns penalties) are doubled for positions 1-11
- Applied immediately in scoring

**Tour Finisher Prediction Tiebreaker** (Applied only if points are tied):

This tiebreaker is based on predicting the TOTAL NUMBER OF CYCLISTS
that will finish the entire tour (not team-specific).

The player(s) with the closest prediction to the actual number of tour finishers
gets/get +1 bonus point. All other players get 0 bonus points.

"Closest prediction" = lowest absolute difference from actual finishers

Examples:

Example 1:
├── Actual finishers: 145
├── Player A predicted: 144 → Difference: 1 → BEST PREDICTION → +1 point ✓
├── Player B predicted: 143 → Difference: 2 → 0 points
├── Player C predicted: 143 → Difference: 2 → 0 points
└── Player D predicted: 140 → Difference: 5 → 0 points

Example 2:
├── Actual finishers: 145
├── Player A predicted: 144 → Difference: 1 → BEST PREDICTION → +1 point ✓
├── Player B predicted: 144 → Difference: 1 → BEST PREDICTION → +1 point ✓
├── Player C predicted: 140 → Difference: 5 → 0 points
└── Player D predicted: 150 → Difference: 5 → 0 points
(Players A and B both get +1 because they tied for best prediction)

This bonus is calculated once after the final stage completes and applied only
when final standings have tied players with identical total points.

---

#### 3.5 Final Standings & Tiebreaker

##### 3.5.1 Standings Display

- Leaderboard shows all players ranked by total points
- Leaderboard is updated after each stage completion
- Shows: Player name, team name, total points, points gained in latest stage

##### 3.5.2 Tiebreaker Rule - Final Standings

If multiple players have the **same total points** at the end of the final stage,
the **Tour Finisher Prediction** accuracy is used as a tiebreaker.

**Tiebreaker Mechanism:**

1. Identify all players with identical final points
2. Calculate each player's prediction accuracy: |Predicted Finishers - Actual Finishers|
3. The player(s) with the LOWEST difference (closest prediction) get +1 bonus point
4. Players are then ranked by: Total Points + Bonus
5. If still tied after prediction tiebreaker: Multiple co-winners are possible

**Example 1: Single winner via tiebreaker**
Final Standings (After Stage 21):
All 4 players tied at 500 points
Actual tour finishers: 145 cyclists

Player A: Predicted 144 → Difference: |144-145| = 1 → BEST → Gets +1 point → 501 final
Player B: Predicted 143 → Difference: |143-145| = 2 → Gets 0 points → 500 final
Player C: Predicted 143 → Difference: |143-145| = 2 → Gets 0 points → 500 final
Player D: Predicted 140 → Difference: |140-145| = 5 → Gets 0 points → 500 final

Final Ranking:
1st: Player A (501 points via tiebreaker)
2nd: Players B, C, D (500 points, co-tied)

**Example 2: Multiple co-winners via tiebreaker**
Final Standings (After Stage 21):
All 4 players tied at 500 points
Actual tour finishers: 145 cyclists

Player A: Predicted 144 → Difference: 1 → BEST (tied with B) → Gets +1 → 501 final
Player B: Predicted 144 → Difference: 1 → BEST (tied with A) → Gets +1 → 501 final
Player C: Predicted 140 → Difference: 5 → Gets 0 points → 500 final
Player D: Predicted 150 → Difference: 5 → Gets 0 points → 500 final

Final Ranking:
1st: Players A and B (501 points each - joint champions)
3rd: Players C and D (500 points each, co-tied)

**Important Notes:**

- The prediction is for TOTAL TOUR finishers (all cyclists in the race), not team-specific
- Only applied when there is a tie in final points
- Multiple players can win if they share the best prediction accuracy
- No further tiebreaker is applied if predictions are also tied

##### 3.5.3 Player Detail View

- Clickable from standings
- Shows:
  - Player and team name
  - Total points and breakdown by stage
  - Team composition with cyclist names and numbers
  - Points earned per cyclist per stage
  - Scoring condition met for each point (e.g., "position bonus", "yellow jersey", "kluns bonus")
  - Status of reserve cyclists
  - Dropouts affecting the team

---

### 4. Non-Functional Requirements

#### 4.1 Performance

- Scoring calculations must complete within 5 minutes for all teams
- Leaderboard loads within 2 seconds
- Cyclist search/sort on 500+ cyclist list must be responsive

#### 4.2 Reliability

- Scoring calculations are idempotent (re-running produces same results)
- Historical stage data is immutable after calculation
- Data backups before each scoring calculation

#### 4.3 Security

- HTTPS enforced
- Passwords hashed (bcrypt or equivalent)
- Role-based access control (Player vs Admin)
- Admin actions logged (team input, scoring changes)

#### 4.4 Usability

- Responsive design for desktop and tablet
- Accessible forms with clear error messages
- Sortable cyclist lists
- Point budget tracker in team composition UI

---

### 5. Configuration Parameters (Admin Configurable)

| Parameter                | Default                | Notes                                                    |
| ------------------------ | ---------------------- | -------------------------------------------------------- |
| Game Tour                | Tour de France         | Configurable to Giro, Vuelta, etc.                       |
| Team Submission Deadline | 2 hours before tour    | Timestamp-based                                          |
| Nationality Requirement  | FRANCE (FRA)           | Configurable country code                                |
| Standard Pool Budget     | 100 points             | Max points for positions 1-11; overflow goes to reserves |
| Reserve Pool Budget      | 20 points              | Base max for reserves; can increase with overflow        |
| Overflow Mechanism       | Enabled                | Unused Standard Pool points are added to Reserve Pool    |
| Password Reset Window    | 12 hours               | Time for new players to reset password                   |
| Double Points Stages     | Stage 15, 20 (example) | Configurable list                                        |
| Combative Cyclist Award  | Optional               | May not be awarded each stage                            |
| Tour Finisher Prediction | Enabled                | Tiebreaker for final standings                           |
| Tiebreaker Bonus Points  | 1 point                | +1 for best (closest) prediction                         |

---

### 6. Out of Scope (Future Phases)

- Mobile app (initial release web-only)
- Real-time live scoring during stages
- Player-to-player messaging
- Tournament seasons or multiweek events
- API for external integrations
- Dark mode UI
- Multi-language support (English only for MVP)

---

### 7. Success Metrics

- Admin can import 200+ cyclists in <2 minutes
- Scoring calculation for 50+ teams completes in <5 minutes
- Player can complete team in <10 minutes
- > 95% uptime during tour season
- Zero scoring calculation errors
- Final standings calculated correctly with tiebreaker logic
- Tied players ranked properly by finisher prediction accuracy
- All three tiebreaker tiers (EXACT, OFF BY 1, OFF BY 2+) working correctly
- Tour completion triggers automatic ranking calculation

---
