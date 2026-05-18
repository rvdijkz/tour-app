# Standings / Leaderboard Page Specification

## Page Overview

Purpose: Show player rankings and competition standings
Path: `/standings`
Role: PLAYER (read-only) and ADMIN (view all)
Accessibility: WCAG 2.1 Level AA

---

## Layout

### Header

Breadcrumb:

- Dashboard > Standings
- 12px, gray #666666

Title:

- "Overall Rankings - [Tour Name 2024]"
- H1, 32px, bold

Status line:

- "After Stage [N] ([X] stages remaining)"
- 14px, gray #666666
- Update after each stage scoring

---

## Filter & View Controls

Card:

- Padding: 16px
- Background: #FFFFFF
- Border: 1px solid #DDDDDD
- Margin-bottom: 24px

### View Options (Tabs)

Tabs:

- "[View: Overall]" (active, blue #0066CC border-bottom)
- "[View: By Stage]" (inactive, gray #666666)

Behavior:

- Click to switch view
- Active tab: Blue bottom border (2px), bold text
- Inactive tab: Gray text, transparent background

### Export Button

Position: Right side of filter row

Button: "[Export Results]"

- Secondary style (border)
- Height: 40px
- Font: 14px, bold
- Action: Downloads standings as CSV or PDF

---

## Main Leaderboard Table

Card:

- Padding: 0 (padding applied to inner cells)
- Background: #FFFFFF
- Border: 1px solid #DDDDDD
- Margin-bottom: 32px

### Table Header

Background: #F5F5F5
Font: Bold, 14px, #333333
Border-bottom: 1px #DDDDDD

Columns:

1. # (Rank)
2. Player / Team
3. Total Points
4. Latest Stage
5. Trend
6. Detail

---

### Table Data Rows

Height: 48px
Padding: 16px per cell
Alternating backgrounds: White / #FAFAFA
Borders: 1px #DDDDDD (bottom only)
Hover background: Light blue #F0F5FF

---

### Row 1: Rank 1 (🥇 Gold)

Left border: 4px solid #FFD700
Background: #FFD700 with 20% opacity

Columns:

**#** (Rank)

- Text: "🥇" or "1"
- Font: Bold, 18px
- Color: Gold #FFD700

**Player / Team**

- Player name: Bold, 16px, #333333
- Team name: Regular, 14px, #0066CC, clickable
- Layout: Stacked (name on top, team below)

**Total Points**

- Large number: Bold, 18px, #0066CC
- Example: "[500 pts]"
- Formatted with commas if applicable

**Latest Stage**

- Points gained: Bold, 14px
- Green if positive: #00AA00 "+18"
- Red if negative: #CC0000 "-5"

**Trend**

- Arrow and change: "↑ +10" (green)
- Icons:
  - ↑ = improved rank
  - ↓ = decreased rank
  - → = same rank
- Color matches trend (green, red, gray)

**Detail**

- Button: "[VIEW]"
- Secondary style, 32px height
- Action: Links to player detail page

---

### Row 2 & 3: Rank 2 & 3 (🥈🥉 Silver & Bronze)

Same as Row 1, but with:

- 🥈 Silver: #C0C0C0 with 20% opacity
- 🥉 Bronze: #CD7F32 with 20% opacity
- Border colors adjusted to match

---

### Rows 4+: Regular Ranks

Background: White / #FAFAFA (alternating)
Left border: None

Columns:

**#** (Rank)

- Text: "4", "5", "6", etc.
- Font: Regular, 14px, bold
- Color: #333333

**Player / Team**

- Player name: Regular, 14px, #333333
- Team name: Regular, 14px, #0066CC, clickable
- Layout: Stacked

**Total Points**

- Font: Bold, 16px, #0066CC
- Example: "450 pts"

**Latest Stage**

- Font: Bold, 14px
- Green/red based on change

**Trend**

- Font: Regular, 14px
- Color matches trend

**Detail**

- Button: "[VIEW]"
- Secondary style, 32px height

---

### Current Player Row (Wherever they are ranked)

Highlight:

- Left border: 3px solid #0066CC
- Background: Slightly darker (light gray #F0F5FF)
- Label: "You →" (blue, right-aligned in rank column)

Same column styling as other rows

---

## Pagination Controls

Position: Below leaderboard table

Format:

- "Showing 1-10 of 28 players"
- Text: 12px, gray #666666

Controls:

- [< Prev] button (secondary style, 40px height)
- Page selector dropdown or pagination links
  - "Page 1 of 3"
  - Click other pages
- [Next >] button (secondary style, 40px height)
- Spacing: 8px between controls
- Center-aligned

---

## Information Box (Collapsible)

**Content Box:**

Title: "How are tied scores ranked?" (bold, 14px)

Content:
"When players have the same total points at the end of the tour,
a tiebreaker is applied using the Tour Finisher Prediction.

TIEBREAKER MECHANISM:

1️⃣ IDENTIFY THE BEST PREDICTION
Compare all tied players' predictions to actual tour finishers
Best prediction = LOWEST difference (closest to actual)

2️⃣ AWARD BONUS
Player(s) with best prediction: +1 bonus point
All others: 0 bonus points

3️⃣ FINAL RANKING
Players ranked by total points (including bonus)
Multiple players can win if they share best prediction

Examples:

Actual tour finishers: 145 cyclists

Example 1 (Single Winner):
├── Player A: Predicted 144 → Difference: 1 → BEST → +1 bonus ✓
├── Player B: Predicted 143 → Difference: 2 → 0 bonus
├── Player C: Predicted 140 → Difference: 5 → 0 bonus
└── Result: Player A wins!

Example 2 (Co-Winners):
├── Player A: Predicted 144 → Difference: 1 → BEST → +1 bonus ✓
├── Player B: Predicted 144 → Difference: 1 → BEST → +1 bonus ✓
├── Player C: Predicted 140 → Difference: 5 → 0 bonus
└── Result: Players A and B are joint champions!"

Font: 12px, regular, #333333
Examples clearly formatted with boxes or indentation

---

## Player Detail View

Path: `/standings/{playerId}` or modal from [VIEW] button

### Header

Back button: "[← Back to Standings]"

- Blue link, 14px, underline on hover
- Position: Top-left

Title: "[PlayerName] / [TeamName]"

- H1, 32px, bold

Status badges (right side):

- "Rank: 1st" or "Rank: 5th" (large, bold)
- Points: "500 pts" (large, blue #0066CC, bold)
- Status: "[ACTIVE]" (green badge)

---

### Points Breakdown by Stage

Card:

- Padding: 24px
- Background: #FFFFFF
- Border: 1px solid #DDDDDD
- Margin-bottom: 32px

Title: "POINTS BREAKDOWN BY STAGE"

- H3, 18px, bold
- Margin-bottom: 16px

Table:

Columns:

- Stage
- Cyclist Name
- Position (in Stage)
- Points
- Scoring Notes

Header:

- Background: #F5F5F5
- Bold text, 14px

Data rows (per stage, then per cyclist):

Row example for Stage 1:

| Stage | Cyclist Name     | Position    | Pts | Scoring Notes            |
| ----- | ---------------- | ----------- | --- | ------------------------ |
| 1     | Giro Max (Pos 1) | 1 (Stage)   | +12 | Base: Pos 1 = 12pts      |
| 1     | Giro Max (Pos 1) | 1 (Stage)   | +2  | Bonus: Position match    |
| 1     | Speedy (Pos 2)   | 3 (Stage)   | +8  | Base: Pos 3 = 8pts       |
| 1     | Alpine (Pos 11)  | 7 (Stage)   | +4  | Pos 11 range: 1-8 = same |
| 1     | Total Stage 1    | -           | +26 | Stage 1 total            |
|       |                  |             |     |                          |
| 2     | Giro Max (Pos 1) | 2 (Stage)   | +10 | Base: Pos 2 = 10pts      |
| 2     | Yellow Jersey    | -           | +3  | Yellow Jersey holder     |
| 2     | Kluns (Pos 14)   | 145 (Stage) | -10 | Positions 2-9: -10pts    |
| 2     | Total Stage 2    | -           | +3  | Stage 2 total            |

Row height: 40px
Padding: 12px per cell
Alternating: White / #FAFAFA
Font: 12px, regular

Highlighting:

Cyclist rows:

- Normal styling

"Total for Stage X" rows:

- Bold entire row
- Background: Light gray #F5F5F5
- Border-top: 2px #0066CC
- Font: Bold, 14px

---

### Team Roster & Status

Card:

- Padding: 24px
- Background: #FFFFFF
- Border: 1px solid #DDDDDD
- Margin-bottom: 32px

Title: "TEAM ROSTER & STATUS"

- H3, 18px, bold
- Margin-bottom: 16px

List (not table):

**Position 1-10: Standard Cyclists**

Each cyclist shown as row:

Format: "✓ Cyclist Name (Game# 45) - Status"

- ✓ Icon: Green checkmark if finished
- ✗ Icon: Red X if dropped out
- Cyclist Name: Bold, 14px
- Game Number: Gray, in parentheses
- Status options:
  - "Finished" (green #00AA00)
  - "Dropped out Stage 14" (red #CC0000)
  - "Active" (blue #0066CC)

Spacing: 8px between cyclists

**Position 11: Nationality Specialist**

Labeled: "11 ☆ [Cyclist Name]"

- Same format as above

**Positions 12-13: Reserves**

Labeled: "12 ⚪ [Cyclist Name] (Reserve 1)"

Status options:

- "Available (Not yet needed)" (gray #999999)
- "Active from Stage 8" (blue, lighter)
- "Dropped out before activation" (red)
- "Finished" (green)

**Position 14: Kluns**

Labeled: "14 ★ [Cyclist Name]"

- Same format as others

---

### Tour Finisher Prediction Summary (If Tour Completed)

Card:

- Padding: 24px
- Background: #FFFFFF
- Border: 1px solid #DDDDDD
- Margin-bottom: 32px

Title: "TOUR FINISHER PREDICTION"

- H3, 18px, bold
- Margin-bottom: 16px

Content:

Predicted Tour Finishers: 145 cyclists

- Font: 14px, bold
- Color: #333333

Actual Tour Finishers: 145 cyclists

- Font: 14px, bold
- Color: Green #00AA00 (if exact match) or normal

Accuracy: "✓✓✓ EXACT MATCH"

- Font: 14px, bold
- Color: Green #00AA00 (for exact)
- Color: Yellow #FFAA00 (for off by 1)
- Color: Gray #999999 (for off by 2+)

Bonus Points: "+10 points"

- Font: 16px, bold
- Color: Green #00AA00

Note (if tour not complete):

- "Tour finisher prediction will be finalized when the tour ends"
- Font: 12px, gray #666666

### Final Ranking Explanation (If Tour Completed & Tied)

Card:

- Padding: 24px
- Background: #FFFFFF
- Border: 1px solid #DDDDDD

Title: "FINAL RANKING EXPLANATION"

- H3, 18px, bold

#### If NOT tied:

Message: "You won outright with [X] points. No tiebreaker needed."

- Font: 14px, green #00AA00
- Icon: 🏆 Trophy

#### If Tied (Example 1: Single Winner via Tiebreaker)

Player Name: "[PlayerName]" (right side: "RANK: 1st 🥇")
Final Points: 501 (large, bold, 16px, green #00AA00 for tiebreaker winner)

Separator line: 1px #DDDDDD

"4 players tied at 500 points"

- Font: 14px, bold

"Tiebreaker Applied: Tour Finisher Prediction"

- Font: 14px, bold, blue #0066CC

**Your Prediction:**

Predicted: 144 finishers
Actual: 145 finishers
Difference: |144-145| = 1

- Font: 12px

Result: ✓ BEST PREDICTION

- Font: 14px, bold, green #00AA00

Tiebreaker Bonus: +1 point

- Font: 14px, bold, green #00AA00

Final Score: 500 + 1 = 501 points

- Font: 14px, bold, blue #0066CC

**Comparison with other tied players:**

"4 players all had 500 points:
├─ You: Predicted 144 (Difference: 1) → BEST → +1 bonus → 501 final ✓
├─ Player B: Predicted 143 (Difference: 2) → 500 final
├─ Player C: Predicted 143 (Difference: 2) → 500 final
└─ Player D: Predicted 140 (Difference: 5) → 500 final"

Font: 12px, gray #666666
Left-aligned list format

Trophy message:
"🏆 Your most accurate prediction secured 1st place!"

- Font: Bold, 14px, green #00AA00
- Margin-top: 12px

---

#### If Tied (Example 2: Multiple Co-Winners via Tiebreaker)

Player Name: "[PlayerName]" (right side: "RANK: 1st 🥇 (Co-Champions)")
Final Points: 501 (large, bold, 16px, gold #FFD700)

Separator line: 1px #DDDDDD

"4 players tied at 500 points"

- Font: 14px, bold

"Tiebreaker Applied: Tour Finisher Prediction"

- Font: 14px, bold, blue #0066CC

**Your Prediction:**

Predicted: 144 finishers
Actual: 145 finishers
Difference: |144-145| = 1

- Font: 12px

Result: ✓ TIED FOR BEST PREDICTION

- Font: 14px, bold, gold #FFD700

Tiebreaker Bonus: +1 point (shared with 1 other player)

- Font: 14px, bold, gold #FFD700

Final Score: 500 + 1 = 501 points

- Font: 14px, bold, blue #0066CC

**Comparison with other tied players:**

"4 players all had 500 points:
├─ You: Predicted 144 (Difference: 1) → TIED BEST → +1 bonus → 501 final ✓
├─ Player B: Predicted 144 (Difference: 1) → TIED BEST → +1 bonus → 501 final ✓
├─ Player C: Predicted 140 (Difference: 5) → 500 final
└─ Player D: Predicted 150 (Difference: 5) → 500 final"

Font: 12px, gray #666666

Trophy message:
"🏆 You and Player B tied for best prediction - joint champions!"

- Font: Bold, 14px, gold #FFD700
- Margin-top: 12px

---

## Responsive Behavior

### Laptop (1440px)

- Table: Full width
- All columns visible
- Font sizes: As specified
- Padding: 16px per cell

### Tablet (768px - 1024px)

- Table: Scrollable horizontally if needed
- Hide "Trend" column on smaller tablets
- Show compact version of ranks (icons only)
- Padding: 12px per cell
- Font sizes: 12px for secondary info

### Mobile (< 768px)

NOT SUPPORTED in MVP
Future phase

---

## Loading States

Initial Load:

- Show skeleton loaders for table rows
- Gray placeholder boxes
- Pulse animation 1s
- Show 10 skeleton rows

Data Loading:

- Pagination disabled
- Sort controls disabled
- Loading indicator in header: "Loading standings..."

---

## Empty States

If No Players Yet:

- Message: "No players have joined yet."
- Position: Center of table area
- Font: 14px, gray #666666

If No Stages Completed:

- Message: "Waiting for first stage results..."
- Position: Center of table area
- Font: 14px, gray #666666

---

## Error Handling

### General Error

Full-width alert (top of page):

- Red border-left (4px), light red background #FFF0F0
- Icon: Red X circle
- Message: "Unable to load standings. Please refresh the page."
- Close button: X
- Action: Refresh button
- Auto-dismiss: After 5 seconds or manual close

### Partial Data Error

Alert within section:

- Red styling
- Message: "Unable to load this section. Refresh to retry."
- Refresh button

---

## Accessibility Features

### Tables

- Semantic <table>, <thead>, <tbody>, <th>
- Column headers read aloud
- Row headers (rank numbers) associated
- Data cells properly associated

### Sorting & Filtering

- Aria-sort on sortable columns
- Aria-label on filter controls
- Keyboard accessible

### Links

- Blue color (#0066CC) + underline
- Aria-label on icon buttons
- Tab order: Logical

### Color Contrast

- All text ≥ 4.5:1 contrast
- Icons + color for status (not color alone)

### Focus Management

- Focus outline: 2px #0066CC
- Visible on all interactive elements
- Tab order: Logical top-to-bottom

---

## Performance

- Page load: < 2 seconds
- Table rendering: < 500ms
- Detail view load: < 1 second
- Sorting/filtering: < 300ms (client-side)

---

## Analytics Events

Track:

- Standings page load
- Filter/sort interactions
- Player detail view clicks
- Export clicks
- View by Stage clicks
- Tiebreaker info expansion

---

## By-Stage View (Secondary Tab)

Path: `/standings?view=by-stage`

Alternative view showing standings after each stage:

Layout:

- Dropdown: "Select Stage: [Stage 1 ▼]"
- Shows leaderboard after that specific stage
- Useful for tracking how standings evolved
- Clicking a stage shows rankings at that point in time

Table columns (same as main view):

- # (Rank after this stage)
- Player / Team
- Points (accumulated up to this stage)
- Stage Points (points earned in this specific stage)
- Detail

Same styling and behavior as main standings view

---
