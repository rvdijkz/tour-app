# Domain-Model.md

## Tour Game Domain Model

### 1. Core Entities

#### 1.1 User

User
├── Id: UUID
├── Username: String (unique, required)
├── Password: String (hashed, required)
├── Email: String (optional)
├── Role: Enum (PLAYER, ADMIN)
├── CreatedAt: DateTime
├── LastLogin: DateTime
├── PasswordChangedAt: DateTime
└── IsActive: Boolean

#### 1.2 Player (Speler)

Player extends User
├── PersonalName: String (required)
├── TeamName: String (required)
├── User: User (FK)
└── Teams: List<Team>
**Invariants:**

- Each User with role PLAYER has exactly one Player record
- PersonalName and TeamName cannot be empty
- Player becomes associated with User on account creation

---

#### 1.3 Cyclist (Renner)

Cyclist
├── Id: UUID
├── Name: String (required)
├── GameNumber: Integer (unique, required)
├── TourNumber: Integer (nullable, unique when populated)
├── PointsValue: Integer (required, ≥0)
├── CountryCode: String (3-char alphanumeric, required)
├── Status: Enum (ACTIVE, DROPPED_OUT)
├── DroppedOutStage: Integer (nullable)
├── ImportedAt: DateTime
└── UpdatedAt: DateTime

**Invariants:**

- GameNumber is unique across all cyclists
- TourNumber is unique when set (null allowed for multiple cyclists initially)
- PointsValue ≥ 0
- CountryCode matches pattern: [A-Z]{3}
- Once Status = DROPPED_OUT, it cannot revert to ACTIVE
- DroppedOutStage is set when Status changes to DROPPED_OUT

**Behaviors:**

- `markAsDroppedOut(stageNumber)`: Sets status and dropout stage
- `getCountryName()`: Returns full country name from code
- `isAvailableForTeam()`: Returns true if ACTIVE status

---

#### 1.4 Team

Team
├── Id: UUID
├── Player: Player (FK, required)
├── Name: String (required)
├── Status: Enum (DRAFT, SUBMITTED, LOCKED, ACTIVE)
├── SubmittedAt: DateTime (nullable)
├── CreatedAt: DateTime
├── UpdatedAt: DateTime
├── TeamCompositions: List<TeamComposition>
├── ExpectedFinishers: Integer (1-14)
├── ActualFinishers: Integer (0-14, nullable)
├── FinisherPredictionBonus: Integer (0-10, calculated)
└── CalculatedPoints: Integer (initially 0, calculated)

**Invariants:**

- Team belongs to exactly one Player
- Team contains exactly 14 TeamComposition entries
- Status transitions: DRAFT → SUBMITTED → LOCKED → ACTIVE (one-way)
- LOCKED status is set at tour submission deadline
- ACTIVE status is set when tour begins
- PredictedTourFinishers: Valid range depends on tour size (e.g., 0-200 for 176-person tour)
- ActualTourFinishers: null until tour completes, then final count set
- FinisherPredictionBonus: 0 or 1 point (0 if not best prediction, 1 if best prediction). calculated after tour completion
- PredictedTourFinishers cannot be changed after Status = LOCKED
- NOTE: This predicts TOTAL TOUR finishers, not team-specific finishers
- Total points spent must respect flexible budget rules
- Total points spent ≤ budget (validated at SUBMIT)

**Validation Logic:**
validate(gameConfig: GameConfiguration): ValidationResult {
// Calculate Standard Pool usage (positions 1-10 + 11)
standardPoolUsed = sumPoints(positions: [1-11])

// Validate standard pool limit
if (standardPoolUsed > gameConfig.standardPoolBudget) {
return INVALID("Standard pool exceeded by {X} points")
}

// Calculate remaining standard pool points
standardPoolRemaining = gameConfig.standardPoolBudget - standardPoolUsed

// Calculate available reserve budget (base + overflow)
availableReserveBudget = gameConfig.reservePoolBudget + standardPoolRemaining

// Calculate Reserve Pool usage (positions 12-13)
reservePoolUsed = sumPoints(positions: [12-13])

// Validate flexible reserve pool limit
if (reservePoolUsed > availableReserveBudget) {
return INVALID("Reserve pool exceeded by {X} points")
}

// All other validations (composition, nationality, etc.)
if (!hasAllPositions(1-14)) {
return INVALID("Team must have all 14 positions filled")
}

if (!hasUniqueAthletes()) {
return INVALID("Each cyclist can appear only once")
}

return VALID(remainingBudget: availableReserveBudget - reservePoolUsed)
**Behaviors:**

- `addCyclist(cyclist, position)`: Adds cyclist to team, triggers validation
- `removeCyclist(position)`: Removes cyclist, recalculates available budget
- `getTotalPointsSpent()`: Returns sum of positions 1-13 only
- `getStandardPoolUsed()`: Returns sum of positions 1-10 + 11
- `getStandardPoolRemaining()`: Returns budget - used
- `getAvailableReserveBudget()`: Returns base reserve + overflow
- `getReservePoolUsed()`: Returns sum of positions 12-13
- `getReservePoolRemaining()`: Returns available - used
- `validate()`: Checks all constraints with flexible budget
- `lock()`: Transitions to LOCKED status
- `activate()`: Transitions to ACTIVE status

---

#### 1.5 TeamComposition

TeamComposition
├── Id: UUID
├── Team: Team (FK, required)
├── Cyclist: Cyclist (FK, required)
├── Position: Integer (1-14, required)
├── ReserveStatus: Enum (NOT_RESERVE, RESERVE_1, RESERVE_2)
├── ActivatedAtStage: Integer (nullable)
├── CreatedAt: DateTime
└── UpdatedAt: DateTime

**Invariants:**

- Position 1-10: Standard cyclists (ReserveStatus = NOT_RESERVE)
- Position 11: Nationality specialist (ReserveStatus = NOT_RESERVE, Cyclist.CountryCode = ConfiguredCountry)
- Position 12: Reserve 1 (ReserveStatus = RESERVE_1)
- Position 13: Reserve 2 (ReserveStatus = RESERVE_2)
- Position 14: Kluns (ReserveStatus = NOT_RESERVE)
- Each Cyclist appears at most once per Team
- Position is unique within a Team
- ActivatedAtStage is null if ReserveStatus = NOT_RESERVE
- ActivatedAtStage is set when reserve is activated (≥ 1)
- Reserve activation always takes effect from the stage after the dropout stage
- If multiple dropouts in positions 1-11 occur in one stage, replacement order is ascending team position (lowest position number first)
- Reserve 1 is assigned before Reserve 2
- A reserve dropped out before activation can never activate

**Behaviors:**

- `isStandardPosition()`: Returns true if position 1-10
- `isNationalityPosition()`: Returns true if position = 11
- `isReservePosition()`: Returns true if position 12-13
- `isKlunsPosition()`: Returns true if position = 14
- `isActivated()`: Returns true if ActivatedAtStage is not null
- `activateAtStage(stageNumber)`: Sets ActivatedAtStage
- `assignReserveActivation(dropoutPositions, stageNumber)`: Assigns reserve activations for next stage using ascending team-position order

---

#### 1.6 Stage

Stage
├── Id: UUID
├── TourId: UUID (FK, required)
├── StageNumber: Integer (required, 1+)
├── Name: String (optional)
├── IsDoublePoints: Boolean (default false)
├── FinishPositions: List<StageFinish>
├── JerseyWinners: JerseyWinners
├── CombativeCyclist: Cyclist (FK, nullable)
├── DroppedOutCyclists: List<Cyclist>
├── ScoringCalculated: Boolean (default false)
├── CalculatedAt: DateTime (nullable)
└── Results: List<StageResult>

**Invariants:**

- StageNumber is unique within a Tour
- IsDoublePoints can be set only before FinishPositions are entered
- Once ScoringCalculated = true, stage data becomes immutable
- JerseyWinners are entered together with FinishPositions
- DroppedOutCyclists list cannot include TourNumbers not in FinishPositions

**Behaviors:**

- `recordFinishes(tourNumbers)`: Records positions 1-20
- `recordJerseys(yellow, white, green, polkaDot)`: Records jersey winners
- `recordCombativeCyclist(tourNumber)`: Records combative cyclist
- `recordDropouts(tourNumbers)`: Adds dropouts
- `calculateScores()`: Triggers scoring for all teams, sets ScoringCalculated = true
- `isReadOnly()`: Returns ScoringCalculated value

---

#### 1.7 StageFinish

StageFinish
├── Id: UUID
├── Stage: Stage (FK, required)
├── FinishPosition: Integer (1-20, required)
├── Cyclist: Cyclist (FK via TourNumber, required)
└── CreatedAt: DateTime

**Invariants:**

- FinishPosition is unique within a Stage
- FinishPosition: 1 ≤ x ≤ 20
- Cyclist.TourNumber must match the recorded position

---

#### 1.8 JerseyWinners

JerseyWinners
├── Id: UUID
├── Stage: Stage (FK, required)
├── YellowJersey: Cyclist (FK, nullable)
├── WhiteJersey: Cyclist (FK, nullable)
├── GreenJersey: Cyclist (FK, nullable)
├── PolkaDotJersey: Cyclist (FK, nullable)
└── CreatedAt: DateTime

**Invariants:**

- Each jersey can be won by at most one cyclist per stage
- Jersey winners must be from that stage's finishers (or leader from previous stage for leader jerseys)

---

#### 1.9 StageResult

StageResult
├── Id: UUID
├── Team: Team (FK, required)
├── Stage: Stage (FK, required)
├── CyclistResults: List<CyclistStageResult>
├── TotalPoints: Integer
├── CreatedAt: DateTime
└── UpdatedAt: DateTime

**Invariants:**

- StageResult is created after Stage.calculateScores()
- Unique constraint: Team + Stage
- TotalPoints is the sum of all CyclistStageResult points for the team

**Behaviors:**

- `calculatePoints()`: Computes TotalPoints from CyclistResults
- `getCyclistResult(cyclist)`: Returns CyclistStageResult for a cyclist

---

#### 1.10 CyclistStageResult

CyclistStageResult
├── Id: UUID
├── StageResult: StageResult (FK, required)
├── TeamComposition: TeamComposition (FK, required)
├── Cyclist: Cyclist (FK, required)
├── TeamPosition: Integer (1-14, required)
├── StageFinishPosition: Integer (1-20+, nullable)
├── BasePoints: Integer (0-12)
├── BonusPoints: Integer
├── JerseyPoints: Integer
├── PenaltyPoints: Integer
├── TotalPoints: Integer
├── ScoringConditions: List<ScoringCondition>
└── CreatedAt: DateTime

**Invariants:**

- TotalPoints = BasePoints + BonusPoints + JerseyPoints - PenaltyPoints
- StageFinishPosition is null if cyclist is reserve and not yet activated, or if dropped before activation
- Penalties are negative values stored in PenaltyPoints

**Behaviors:**

- `getTotalPoints()`: Returns calculated total
- `getExplanation()`: Returns human-readable description of all scoring conditions applied

---

#### 1.11 ScoringCondition

ScoringCondition
├── Id: UUID
├── CyclistStageResult: CyclistStageResult (FK, required)
├── ConditionType: Enum (BASE, POSITION_BONUS, JERSEY, COMBATIVE, KLUNS, DROPOUT_PENALTY, RESERVE_PENALTY, DOUBLE_POINTS)
├── Points: Integer
└── Description: String

**Example Descriptions:**

- "Finished 3rd in stage (8 points)"
- "Position 5 in team = stage position 5 (2 bonus points)"
- "Yellow jersey holder (+3 points)"
- "Kluns finished last (+5 points)"
- "Dropout penalty (-2 points)"
- "Double points stage (×2)"

**Behaviors:**

- `getDisplayText()`: Returns user-friendly text

---

#### 1.12 Tour

Tour
├── Id: UUID
├── Name: String (e.g., "Tour de France 2024")
├── Edition: String (e.g., "2024")
├── Type: Enum (TOUR_DE_FRANCE, GIRO_DITALIA, VUELTA_ESPANA, CUSTOM)
├── StartDate: DateTime
├── EndDate: DateTime (calculated: StartDate + (number of stages - 1) days)
├── Stages: List<Stage>
├── Status: Enum (PLANNING, TEAM_SUBMISSION, ACTIVE, COMPLETED)
├── TeamSubmissionDeadline: DateTime
├── TotalStages: Integer
├── FinalStandings: List<RankedTeam>
├── CreatedAt: DateTime
└── UpdatedAt: DateTime

**Invariants:**

- TeamSubmissionDeadline ≤ StartDate (typically 24-48 hours before)
- TotalStages ≥ 1
- Status transitions: PLANNING → TEAM_SUBMISSION → ACTIVE → COMPLETED (one-way)
- All teams must be LOCKED before Status = ACTIVE

**Tiebreaker:**
calculateFinalStandings() {
// Called after final stage is scored
List<Team> allTeams = findAllTeamsByTour(this);

    // Calculate final points for each team
    Map<Team, Integer> teamPoints = new HashMap<>();
    for (Team team : allTeams) {
        int total = sumAllStageResults(team);
        teamPoints.put(team, total);
    }

    // Group by total points
    Map<Integer, List<Team>> groupedByPoints = groupBy(teamPoints, points);

    List<RankedTeam> ranked = new ArrayList<>();
    int currentRank = 1;

    for (Integer points : groupedByPoints.keySet().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList())) {
        List<Team> tiedTeams = groupedByPoints.get(points);

        if (tiedTeams.size() == 1) {
            // Single team with this point total - no tiebreaker needed
            ranked.add(new RankedTeam(
                tiedTeams.get(0),
                points,
                currentRank
            ));
            currentRank++;
        } else {
            // Multiple teams tied - apply tiebreaker
            List<RankedTeam> broken = applyPredictionTiebreaker(tiedTeams, points);
            ranked.addAll(broken);
            currentRank += broken.size();
        }
    }

    this.FinalStandings = ranked;
    this.Status = Status.COMPLETED;
    return ranked;

}

applyPredictionTiebreaker(List<Team> tiedTeams, Integer points) {
// Find the best (closest) prediction among tied teams
int actualTourFinishers = this.ActualTourFinishers;

    // Calculate prediction differences for each team
    List<TeamPredictionDifference> predictions = new ArrayList<>();

    for (Team team : tiedTeams) {
        int difference = Math.abs(team.PredictedTourFinishers - actualTourFinishers);
        predictions.add(new TeamPredictionDifference(
            team,
            difference
        ));
    }

    // Find minimum difference (best prediction)
    int minDifference = predictions.stream().mapToInt(p -> p.difference).min().orElse(0);

    // Split into winners (best prediction) and others
    List<Team> bestPredictors = predictions.stream()
        .filter(p -> p.difference == minDifference)
        .map(p -> p.team)
        .collect(Collectors.toList());

    List<Team> others = predictions.stream()
        .filter(p -> p.difference > minDifference)
        .map(p -> p.team)
        .collect(Collectors.toList());

    List<RankedTeam> result = new ArrayList<>();
    int currentRank = 1;

    // Winners get +1 bonus point
    for (Team team : bestPredictors) {
        result.add(new RankedTeam(
            team,
            points + 1,  // +1 tiebreaker bonus
            currentRank
        ));
    }

    // After all winners, next rank starts (accounting for multiple winners)
    currentRank += bestPredictors.size();

    // Others get no bonus
    for (Team team : others) {
        result.add(new RankedTeam(
            team,
            points,  // No bonus
            currentRank
        ));
    }

    return result;

}

---

#### 1.13 GameConfiguration

GameConfiguration
├── Id: UUID
├── TourId: UUID (FK, required)
├── NationalityRequirement: String (3-char country code)
├── StandardPoolBudget: Integer (default: 100)
├── ReservePoolBudget: Integer (default: 20)
├── PasswordResetWindowHours: Integer
├── DoublePointsStages: List<Integer> (stage numbers)
├── CreatedAt: DateTime
└── UpdatedAt: DateTime

**Invariants:**

- StandardPoolBudget > ReservePoolBudget (e.g., 100 > 20) ✓
- PasswordResetWindowHours > 0
- DoublePointsStages contains only valid stage numbers

---

### 2. Value Objects

#### 2.1 CyclistPoints

CyclistPoints (Immutable)
├── Value: Integer (0-12)
├── Constraints:

- Mapping: Position 1→12, 2→10, 3→8, ..., 10→1
- ReadOnly after construction

---

#### 2.2 NationalityCode

NationalityCode (Immutable)
├── Code: String (3-char, uppercase)
├── Constraints:

- Format: ^[A-Z]{3}$
- ReadOnly after construction
  └── getCountryName(): Resolves to full name (e.g., "Belgium")

---

#### 2.3 JerseyType

JerseyType (Enum)
├── YELLOW (3 points)
├── WHITE (1 point)
├── GREEN (1 point)
└── POLKA_DOT (1 point)

---

#### 2.4 RankedTeam

RankedTeam (Value Object)
├── Team: Team (reference)
├── TotalPoints: Integer
├── FinisherAccuracy: Enum (EXACT_MATCH, OFF_BY_1, OFF_BY_2_PLUS)
├── SubmittedAt: DateTime (for final tiebreaker)
└── Rank: Integer (1, 2, 3, ...)

---

#### 2.5 PredictionAccuracy (Value Object)

PredictionAccuracy (Value Object)
├── PredictedFinishers: Integer
├── ActualFinishers: Integer
├── Difference: Integer (calculated as absolute difference)
├── IsBestPrediction: Boolean
└── TiebreakerBonus: Integer (0 or 1)

Example:
├── Predicted: 141
├── Actual: 145
├── Difference: 4
├── IsBestPrediction: true (if all others have difference > 1)
└── TiebreakerBonus: 1

---

### 3. Aggregates

#### 3.1 Player Aggregate

Root: Player
├── Player
│ └── User (embedded)
│ └── Teams (list of Team aggregates)

- Responsible for: Player identity, user credentials, team collection

#### 3.2 Team Aggregate

Root: Team
├── Team
│ ├── TeamComposition[14] (owned entities)
│ └── Cyclist references (external)

- Responsible for: Team validation, budget constraints, composition management

#### 3.3 Stage Aggregate

Root: Stage
├── Stage
│ ├── StageFinish[1-20] (owned entities)
│ ├── JerseyWinners (owned value object)
│ ├── StageResult[n] (owned entities, calculated)
│ └── CyclistStageResult[n*14] (owned entities, calculated)

- Responsible for: Stage data entry, scoring calculation, immutability after calculation

#### 3.4 Tour Aggregate

Root: Tour
├── Tour
│ ├── Stage[n] (owned aggregates)
│ ├── GameConfiguration (owned value object)
│ ├── RankedTeam[n] (owned value objects, calculated)
│ └── Cyclist references (external)

- Responsible for: Tour lifecycle, stage management, team deadline enforcement, final standings

---

### 4. Repositories

PlayerRepository
├── findByUsername(username): Player
├── findById(id): Player
├── save(player): void
└── findAll(): List<Player>

CyclistRepository
├── findByGameNumber(number): Cyclist
├── findByTourNumber(number): Cyclist
├── findAll(): List<Cyclist>
├── save(cyclist): void
└── search(filters): List<Cyclist>

TeamRepository
├── findById(id): Team
├── findByPlayer(player): List<Team>
├── save(team): void
└── findActiveTeamsByTour(tour): List<Team>

StageRepository
├── findById(id): Stage
├── findByTourAndNumber(tour, number): Stage
├── findCompletedStages(tour): List<Stage>
└── save(stage): void

StageResultRepository
├── findByTeamAndStage(team, stage): StageResult
├── findByTeam(team): List<StageResult>
├── save(stageResult): void
└── calculateTeamStandings(tour): List<TeamStanding>

TourRepository
├── findById(id): Tour
├── findAll(): List<Tour>
├── findActive(): List<Tour>
├── save(tour): void
└── findFinalStandings(tour): List<RankedTeam>

---

### 5. Domain Events

PlayerCreatedEvent
├── PlayerId: UUID
├── Username: String
└── CreatedAt: DateTime

TeamSubmittedEvent
├── TeamId: UUID
├── PlayerId: UUID
├── SubmittedAt: DateTime
└── TotalPointsSpent: Integer

StageResultsEnteredEvent
├── StageId: UUID
├── TourId: UUID
├── EnteredAt: DateTime
└── EnteredByAdmin: UUID

StagesScoredEvent
├── StageId: UUID
├── TourId: UUID
├── NumberOfTeamsScored: Integer
├── CalculatedAt: DateTime
└── PerformanceMetrics: Object

CyclistDroppedOutEvent
├── CyclistId: UUID
├── StageNumber: Integer
├── DroppedOutAt: DateTime
└── AffectedTeams: List<UUID>

ReserveActivatedEvent
├── TeamId: UUID
├── ReserveNumber: Integer (1 or 2)
├── ReplacedCyclist: UUID
├── ActivatedAtStage: Integer
└── ActivatedAt: DateTime

TourCompletedEvent
├── TourId: UUID
├── FinalStandingsCalculated: Boolean
├── CalculatedAt: DateTime
└── TopPlayers: List<RankedTeam>

---

### 6. Entity Relationships Diagram

Tour (1) ──────────── (Many) Stage
├─────────────────────┤

Tour (1) ──────────── (1) GameConfiguration

Cyclist (1) ──────── (Many) TeamComposition

Player (1) ──────── (Many) Team

Team (1) ──────── (14) TeamComposition
├────────────────────────┤

Cyclist (1) ──────── (Many) StageFinish
├────────────────┤

Stage (1) ──────── (Many) StageFinish

Stage (1) ──────── (1) JerseyWinners

Stage (1) ──────── (Many) StageResult

Team (1) ──────── (Many) StageResult
└─(Team, Stage)──→ (1) StageResult (unique constraint)

StageResult (1) ──────── (Many) CyclistStageResult

Cyclist (1) ──────── (Many) CyclistStageResult

TeamComposition (1) ──── (Many) CyclistStageResult

## Tour (1) ──────── (Many) RankedTeam (calculated)

### 7. Constraints Summary

| Entity          | Constraint                                | Type             |
| --------------- | ----------------------------------------- | ---------------- |
| Cyclist         | GameNumber unique                         | Unique           |
| Cyclist         | TourNumber unique when populated          | Unique           |
| Cyclist         | CountryCode 3-char alphanumeric           | Format           |
| Team            | 14 compositions (positions 1-14)          | Cardinality      |
| Team            | Each cyclist appears once                 | Uniqueness       |
| Team            | Total points ≤ budget (flexible)          | Business         |
| TeamComposition | Position 11 has specific nationality      | Business         |
| TeamComposition | Positions 12-13 are reserves              | Business         |
| TeamComposition | Position 14 is Kluns                      | Business         |
| Stage           | FinishPosition 1-20 unique                | Unique           |
| StageResult     | Unique per (Team, Stage)                  | Composite Unique |
| Tour            | Status progression one-way                | State            |
| Tour            | FinalStandings calculated post-completion | Business         |

---
