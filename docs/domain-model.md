# Domain Model

# Core Aggregates

## User

Represents an authenticated application user.

Fields:
- id
- username
- passwordHash
- role
- firstLoginRequired
- passwordExpiration
- active

Roles:
- ADMIN
- PLAYER

Relationships:
- One-to-one with PlayerProfile

---

## PlayerProfile

Represents a game participant.

Fields:
- id
- displayName
- teamName

Relationships:
- belongs to User
- owns one Team

---

## Team

Represents a player's selected riders.

Fields:
- id
- predictedFinishers
- lockedAt
- createdAt
- updatedAt

Relationships:
- belongs to PlayerProfile
- contains TeamSelections

Business Rules:
- Exactly 14 selections
- No duplicate riders
- Point limits enforced
- Immutable after lock deadline

---

## TeamSelection

Represents a rider assigned to a specific team position.

Fields:
- id
- teamPosition
- effectiveFromStage
- active

Relationships:
- belongs to Team
- references Rider

Team Positions:
- STANDARD_1 ... STANDARD_10
- NATIONALITY_RIDER
- RESERVE_1
- RESERVE_2
- KLUNS

---

## Rider

Represents a professional cyclist.

Fields:
- id
- name
- gameNumber
- officialTourNumber
- riderValue
- nationalityCode
- abandoned

Relationships:
- can appear in many teams
- can appear in stage results

---

## Tour

Represents a cycling competition.

Fields:
- id
- name
- type
- year
- active

Examples:
- Tour de France 2026
- Giro 2026

Relationships:
- contains Stages
- contains Riders

---

## Stage

Represents one stage within a tour.

Fields:
- id
- stageNumber
- name
- date
- doublePoints
- scoringProcessed
- published

Relationships:
- belongs to Tour
- contains StageResult

---

## StageResult

Represents all stage outcome data.

Fields:
- id
- yellowJerseyRiderId
- greenJerseyRiderId
- polkaDotRiderId
- whiteJerseyRiderId
- combativityRiderId

Relationships:
- belongs to Stage
- contains StagePlacings
- contains Abandonments

---

## StagePlacing

Represents rider ranking within a stage.

Fields:
- id
- position
- placingType

Placing Types:
- TOP_20
- LAST_5

Relationships:
- belongs to StageResult
- references Rider

---

## Abandonment

Represents a rider abandoning the tour.

Fields:
- id
- stageNumber

Relationships:
- belongs to Rider
- belongs to Stage

---

## ScoreCalculation

Represents execution of stage scoring.

Fields:
- id
- calculationTimestamp
- completed
- calculationVersion

Relationships:
- belongs to Stage

---

## TeamStageScore

Represents calculated score for a team for a stage.

Fields:
- id
- totalScore
- rankingPosition
- cumulativeScore

Relationships:
- belongs to Team
- belongs to Stage
- contains ScoreDetails

---

## ScoreDetail

Represents detailed explanation of score contributions.

Fields:
- id
- scoreType
- points
- explanation

Score Types:
- STAGE_RESULT
- POSITION_BONUS
- JERSEY
- COMBATIVITY
- KLUNS
- PENALTY
- RESERVE_SUBSTITUTION

Relationships:
- belongs to TeamStageScore
- optional reference to Rider

---

# Domain Services

## TeamValidationService
Responsibilities:
- Validate team composition
- Validate point limits
- Validate nationality rider
- Validate duplicate riders

---

## StageScoringService
Responsibilities:
- Calculate stage scores
- Apply bonus rules
- Apply penalties
- Apply jersey logic
- Process reserves

---

## ReserveSubstitutionService
Responsibilities:
- Determine active reserves
- Apply substitutions after abandonments

---

## StandingsService
Responsibilities:
- Generate leaderboard
- Calculate cumulative scores
- Determine rankings

---

# Important Domain Rules

## Team Locking
Teams become immutable after configured deadline.

---

## Reserve Activation
Reserve riders become active only from the next stage after substitution.

---

## Jersey Delayed Scoring
Jersey points apply starting the next stage.

---

## Score Immutability
Published stage scores must remain reproducible and auditable.

---

# Suggested Bounded Contexts

## Identity Context
- Users
- Authentication
- Authorization

## Team Context
- Teams
- Team validation
- Rider selection

## Tour Context
- Tours
- Stages
- Riders

## Scoring Context
- Stage results
- Score calculations
- Standings

## Administration Context
- Imports
- Configuration
- Publication